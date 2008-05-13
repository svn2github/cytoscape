#!/usr/bin/perl -I../perl-lib -w

#
# Adapted from Mike Daly's sif2gl.pl script
#
# Process a publication directory to create gl files, for 
# enrichment scoring, and an sql file to insert the models
# into the database

use MultiOrganismSIF;
use GeneNameMapper;
use EdgeMapper;
use YeastHumanGeneMapper;
use Publication;

use File::Spec;

if(scalar(@ARGV) < 3) { die "$0: <data dir> <output-dir> <publication>+\n";}

my $DATA_DIR = shift @ARGV;
my $OUTPUT_DIR = shift @ARGV;
my $cacheDir = $OUTPUT_DIR . "/cache";

my $GOMap = YeastHumanGeneMapper->new($cacheDir);

my $nm = GeneNameMapper->new();
my $em = EdgeMapper->new();

foreach my $pub (@ARGV)
{
    my $glDir = "$OUTPUT_DIR/gl";
    my $glPubDir = "$glDir/$pub";
    my $sqlDir = "$OUTPUT_DIR/sql";

    checkDir($glDir);
    checkDir($glPubDir);
    checkDir($sqlDir);

    processPub($pub, $DATA_DIR, $glPubDir, $sqlDir);
}

sub processPub
{
    my ($pubName, $dataDir, $glDir, $sqlDir) = @_;

    print "publication: $pubName\n";

    my $pub = Publication->new($pubName, $dataDir, $nm, $em);

    my $sqlFile = $pubName . ".insert-MODEL-GENE_MODEL.sql";
    
    open(SQLOUT, ">$sqlDir/$sqlFile") || die "Can't open $sqlDir/$sqlFile\n";
    
    while( my ($file, $sif) = each %{$pub->sifs()})
    {
	print "   reading $file\n";

	makeSubDirs($glDir, $sif->name());

	writeGL($glDir, $sif->name() . ".gl",  $file, $sif);
	writeSQL(*SQLOUT, $sif);
    }
    close SQLOUT;
}

sub makeSubDirs
{
    my ($parent, $name) = @_;

    my ($vol, $dir, $file) = File::Spec->splitpath($name);
    checkDir(File::Spec->catdir($parent, $dir));
}

sub checkDir
{
    my ($dir) = @_;
    if (! -d $dir)
    {
	print STDERR "$dir is not a directory.  Creating...";
	mkdir $dir;
	if(-d $dir)
	{
	    print STDERR "ok\n";
	}
	else
	{
	    die "failed: $!\n";
	}
    }
}

sub writeSQL
{
    my ($FH, $sif) = @_;

    my @organisms = @{$sif->organisms()};

    my %goids;
    my $tmp;

    foreach my $org (@organisms)
    {
	foreach my $gene (keys %{$sif->org2genes()->{$org}})
	{
	    if($org =~ /Homo sapiens/i)
	    {
		$gene .= "_HUMAN";
	    }

	    $tmp = $GOMap->mapName($gene, $org);
	    if(defined($tmp))
	    {
		$goids{$tmp}++;
	    }
	    else
	    {
		print STDERR "### Missing gene $gene\n";
	    }
	}
    }

    printf $FH ("INSERT INTO model (pub,name) VALUES ('%s', '%s');\n", 
		$sif->pub(), $sif->name());

    if(scalar(keys %goids) > 0)
    {
	print $FH ("INSERT INTO gene_model (model_id, gene_product_id) VALUES\n");
	print $FH join(",\n  ", 
		       # CMAK 9/12/07
		       # LAST_INSERT_ID() does not seem to work when inserting multiple values
		       # on MySQL v 4.1.20 (default RedHat installation on chianti).
		       # So, use the less elegant way instead (select id from model ...).
		       # LAST_INSERT_ID() works on MySQL v 4.1.21 on claret
		       #
                       #map { sprintf("(LAST_INSERT_ID(), %s)", $_) } 
		       #
		       map { sprintf("((select id from model where pub= '%s' and name = '%s'), %s)", $sif->pub(), $sif->name(), $_) } 
		       sort { $a<=>$b} keys %goids);
	print $FH ";\n\n";
    }
}

sub writeGL
{
    my ($dir, $outfile, $sifFile, $sif) = @_;

    my @organisms = @{$sif->organisms()};

    my $n_genes = 0;
    $n_genes = scalar(keys %{$sif->genes()});
#    map { $n_genes += scalar(keys %{$sif->org2genes()->{$_}}) } @organisms;


    my $intxn_count = 0;
    my %intxn_types;
    foreach my $org (@organisms)
    {
	my @intx = keys %{$sif->org2interactions()->{$org}};
	$intxn_count += scalar(@intx);

	foreach my $i (@intx)
	{
	    my ($type, @genes) = split("::", $i);
	    $intxn_types{$type}++;
	}
    }

    open(OUT, ">$dir/$outfile") || die "Can't open $dir/$outfile\n";

    printf OUT "#sif=%s\n", $sifFile;
    printf OUT "#%s\n", join("|", @organisms);
    printf OUT "#n_genes=$n_genes\n", $n_genes;
    printf OUT "#n_intxns=%s\n", $intxn_count;

    map { printf OUT "#%s=%d\n", $_, $intxn_types{$_}} keys %intxn_types;

    foreach my $gene (keys %{$sif->genes()})
    {
	print OUT $gene . "\n";
    }
    close OUT;
}

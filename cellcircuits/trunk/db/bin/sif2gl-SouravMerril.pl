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
    my $sifDir = "$DATA_DIR/$pub/sif";
    my $glDir = "$OUTPUT_DIR/gl";
    my $glPubDir = "$glDir/$pub";
    my $sqlDir = "$OUTPUT_DIR/sql";

    if (! -d $sifDir)
    {
	print STDERR "$sifDir is not a directory\n";
	next;
    }
    checkDir($glDir);
    checkDir($glPubDir);
    checkDir($sqlDir);

    processPub($pub, $sifDir, $glPubDir, $sqlDir);
}

sub processPub
{
    my ($pub, $sifDir, $glDir, $sqlDir) = @_;

    print STDERR "publication: $pub\n";

    my @sifs = glob("$sifDir/*.sif");
    
    my $sqlFile = $pub . ".insert-MODEL-GENE_MODEL.sql";
    #my @sifs = "$sifDir/" . "24.sif";
    
    open(SQLOUT, ">$sqlDir/$sqlFile") || die "Can't open $sqlDir/$sqlFile\n";
    
    foreach my $file (@sifs)
    {
	print STDERR "   reading $file\n";
	my $sif = MultiOrganismSIF->new($file, 
					["Saccharomyces cerevisiae","Homo sapiens"], 
					$nm,
					$em);
	my $name = getName($file);
	$sif->name($name);
	$pub =~ /(\w+)/;
	$sif->pub($1);
	
	writeGL($glDir, $name . ".gl",  $file, $sif);
	writeSQL(*SQLOUT, $sif);
    }
    close SQLOUT;
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
		       map { sprintf("(LAST_INSERT_ID(), %s)", $_) } sort { $a<=>$b} keys %goids);
	print $FH ";\n\n";
    }
}

sub getName
{
    my ($file) = @_;
    my ($volume, $dirs, $name) = File::Spec->splitpath( $file );

    if($name =~ /(.+)\.(\w+)/)
    {
	return $1;
    }
   
    return $name;
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

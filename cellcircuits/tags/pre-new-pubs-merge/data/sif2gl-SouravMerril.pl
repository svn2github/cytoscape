#!/usr/bin/perl -I../meta-analysis -w

#
# Adapted from Mike Daly's sif2gl.pl script
#

use IPITable;
use MultiOrganismSIF;
use GeneNameMapper;
use EdgeMapper;
use YeastHumanGeneMapper;

use File::Spec;

if(scalar(@ARGV) < 1) { die "$0: <publication dir>+\n";}

my $GL = "gl";
my $SIF = "sifs";

my $GOMap = YeastHumanGeneMapper->new();
my $human2go = $GOMap->getHumanMap();

my $humanXrefTable = IPITable->new("ipi.HUMAN.xrefs");
#my $go = HumanUniprot2GO->new("sourav-merril/human-genes-in-GO-DB.txt");
my $nm = GeneNameMapper->new();
my $em = EdgeMapper->new();

foreach my $pub (@ARGV)
{
    my $sifDir = "$pub/$SIF";
    my $glDir = "$pub/$GL";
    if (! -d $sifDir)
   {
	print STDERR "$sifDir is not a directory\n";
	next;
    }
    if (! -d $glDir)
    {
	print STDERR "$glDir is not a directory\n";
	next;
    }

    processPub($pub, $sifDir, $glDir, ".");
}

sub processPub
{
    my ($pub, $sifDir, $glDir, $sqlDir) = @_;

    my @sifs = glob("$sifDir/*.sif");
    
    my $sqlFile = $pub . "-insert-models.sql";
    #my @sifs = "$sifDir/" . "24.sif";
    
    open(SQLOUT, ">$sqlDir/$sqlFile") || die "Can't open $sqlDir/$sqlFile\n";
    
    foreach my $file (@sifs)
    {
	print STDERR "reading $file\n";
	my $sif = MultiOrganismSIF->new($file, 
					["Saccharomyces cerevisiae","Homo sapiens"], 
					$nm,
					$em);
	my $name = getName($file);
	$sif->name($name);
	$sif->pub($pub);
	
	writeGL($glDir, $name . ".gl",  $file, $sif);
	writeSQL(*SQLOUT, $sif);
    }
    close SQLOUT;
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
	    if($org =~ /Homo sapiens/i && $humanXrefTable->exists($gene))
	    {
		my $found = 0;
		map 
		{ 
		    $tmp = $GOMap->mapName($_, $org);
		    if(defined($tmp))
		    {
			$found = 1;
			$goids{$tmp}++;
		    }
		} @{$humanXrefTable->get($gene)};

		print STDERR "### Missing gene $gene\n" if(! $found);
	    }
	    else
	    {
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
	print OUT normalizeGeneNames($gene, \@organisms) . "\n";
    }
    close OUT;
}

sub normalizeGeneNames
{
    my ($gene, $organisms) = @_;

    my $humanIndex = -1;
    foreach my $i (0..(scalar(@{$organisms})-1))
    {
	if($organisms->[$i] =~ /homo sapiens/i)
	{
	    $humanIndex = $i;
	}
    }

    if($humanIndex > 0)
    {
	my @g = split(/\|/, $gene);

	my $entrezId = $g[$humanIndex];
	if($humanXrefTable->exists($entrezId))
	{
	    my @symbols;
	    foreach my $xref (@{$humanXrefTable->get($entrezId)})
	    {
		my $foundXref = undef;
		if($human2go->existsXref($xref))
		{
		    $foundXref = $xref;
		}
		elsif($xref =~ /(.*)-\d+/ && $human2go->existsXref($1))
		{
		    $foundXref = $1;
		}

		if(defined($foundXref))
		{
		    my $symbol = $human2go->xref2symbol($foundXref);

		    ## remove _HUMAN to conform to format expected by
		    ## compute_enrichment.pl
		    $symbol =~ s/_HUMAN//g; 
		    if($symbol !~ /$xref/)
		    {
			unshift(@symbols, $symbol);
		    }
		    else
		    {
			push @symbols, $symbol;
		    }
		}
	    }

	    $g[$humanIndex] = shift @symbols if(scalar(@symbols) > 0);
	}
	
	return join("|", @g);
    }

    return $gene;
}

#     foreach my $org (@organisms)
#     {
# 	foreach my $gene (keys %{$sif->org2genes()->{$org}})
# 	{
# 	    my %done;

# 	    if($org eq "Homo sapiens" && $tab->exists($gene))
# 	    {
# 		map { 
# 		    if($human2go->existsXref($_))
# 		    {
# 			#printf OUT ("%s\t%s\t%s\n", 
# 			#	    $human2go->xref2go($_),
# 			#	    $_, 
# 			#	    $human2go->xref2symbol($_)
# 			#	    );
# 			printf OUT ("%s\n", $human2go->xref2symbol($_) );
# 		    }
# 		    elsif(/(.*)-\d+/ && 
# 			  $human2go->existsXref($1))
# 		    {
# 			my $uid= $1;
# 			if(!exists($done{$uid}))
# 			{
# 			    $done{$uid}++;
# 			    #printf OUT ("%s\t%s\t%s\n", 
# 			    #            $human2go->xref2go($uid),
# 			    #		$uid, 
# 			    #		$human2go->xref2symbol($uid)
# 			    #		);
# 			    printf OUT ("%s\n", $human2go->xref2symbol($uid) );
# 			}
# 		    }
# 		    else
# 		    {
# 			#uncomment to see uniprot ids that are not in the GO DB
# 			#push @{$text}, join(":", $_, "**not-in-GO-database**");
# 		    }
# 		} @{$tab->get($gene)};
# 	    }
# 	    else
# 	    {
# 		printf OUT "%s\n", $gene;
# 	    }
# 	}
#     }

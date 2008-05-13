#!/usr/bin/perl -I../meta-analysis -w


use IPITable;
use MultiOrganismSIF;
use YeastHumanGeneMapper;
use File::Spec;


my $GL  = "gl";
my $SIF = "old_sifs";
my $NEW_SIF = "sifs";

my $GOMap    = YeastHumanGeneMapper->new();
my $human2go = $GOMap->getHumanMap();

my $humanXrefTable = IPITable->new("ipi.HUMAN.xrefs");

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

    processPub($pub, $sifDir, "$pub/$NEW_SIF");
}

sub processPub
{
    my ($pub, $sifDir, $newSifDir) = @_;

    my @sifs = glob("$sifDir/*.sif");
    
    #my @sifs = "$sifDir/" . "24.sif";
    
    my $organisms = ["Saccharomyces cerevisiae","Homo sapiens"];

    foreach my $file (@sifs)
    {
	my ($volume, $dirs, $name) = File::Spec->splitpath( $file );

	print STDERR "reading $file\n";
	
	open(OUT, ">$newSifDir/$name") || die "Cannot open $newSifDir/$name\n";
	open(SIF, $file) or die "Cannot open " . $file . ": $!\n";
	my $tmp = "";
	while(<SIF>)
	{
	    chomp; s/^\s+//g; s/\s+$//g;
	    my (@line) = split(/\s+/);
	    
	    if(scalar(@line) == 3) {
		
		my $gene1 = uc($line[0]);
		my $type = lc($line[1]);
		my $gene2 = uc($line[2]);
		
		#printf "### %s\t%s\n", $gene1, normalizeGeneNames($gene1, $organisms);
		#printf "### %s\t%s\n", $gene2, normalizeGeneNames($gene2, $organisms);

		printf OUT ("%s\t%s\t%s\n", 
			    normalizeGeneNames($gene1, $organisms), 
			    $type,
			    normalizeGeneNames($gene2, $organisms));
		
		
	    }
	}
	close SIF;
	close OUT;
    }
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

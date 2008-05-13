#!/usr/bin/perl -I../perl-lib -w

use IPITable;
use MultiOrganismSIF;
use YeastHumanGeneMapper;
use File::Spec;
use EntrezGene;
use DBCache;

if(scalar(@ARGV) != 6) 
{ 
    die "$0: <db cache dir> <output-sql-dir> <pub name> <input-sif-dir> <output-sif-dir> <organsisms>\n";
}

my ($CACHE, $OUT_SQL, $PUB, $SIF, $NEW_SIF, $ORG_STR) = @ARGV;
			 
my @ORGS = split(/\:/, $ORG_STR);

print STDERR "### DB cache dir: $CACHE\n";
print STDERR "### Output SQL dir: $OUT_SQL\n";
print STDERR "### Pub: $PUB\n";
print STDERR "### Input SIF dir: $SIF\n";
print STDERR "### Ouput SIF dir: $NEW_SIF\n";
printf STDERR "### Organsisms: [%s]\n", join(", ", @ORGS);


my $GOMap    = YeastHumanGeneMapper->new($CACHE);
my $human2go = $GOMap->getHumanMap();

my $db = DBCache->new($CACHE);

my $mol_fcn_id = $db->name2termId("molecular function unknown");
my $bio_process_id = $db->name2termId("biological process unknown");
my $cell_comp_id = $db->name2termId("cellular component unknown");

my $humanXrefTable = IPITable->new("ipi.HUMAN.xrefs");
my $entrezGeneTable = EntrezGene->new("gene_info_human");

my %missing;

if (! -d $SIF)
{
    print STDERR "$SIF is not a directory\n";
    die;
}

if (! -d $NEW_SIF)
{
    print STDERR "$NEW_SIF is not a directory\n";
    die;
}

processPub($SIF, "$NEW_SIF");

my $f = "$OUT_SQL/$PUB.insert-DBXREF-GENE_PRODUCT-ASSOCIATION.sql";

open(SQL, ">$f") || die "Can't open $f\n";

foreach my $id (sort { $a <=> $b} keys %missing)
{
    my $species = $missing{$id}->{species};
    my $speciesId = $db->name2speciesId($species);
    my $symbol = $missing{$id}->{symbol} . "_HUMAN";

    printf SQL ("INSERT IGNORE INTO dbxref (xref_key, xref_dbname) VALUES ('%s', 'CELLCIRCUITS_ENTREZ_GENE');\n", 
		$id);

    printf SQL ("INSERT IGNORE INTO gene_product (symbol,dbxref_id,species_id) VALUES ('%s', LAST_INSERT_ID(), %d);\n",
		uc($symbol), 
		$speciesId);
    
    printf SQL ("INSERT IGNORE INTO association (term_id, gene_product_id) VALUES (%d, LAST_INSERT_ID()), (%d, LAST_INSERT_ID()), (%d, LAST_INSERT_ID());\n", 
		$mol_fcn_id, 
		$bio_process_id, 
		$cell_comp_id);

    print SQL "\n";

    printf "(%s, %s, %s)\n", $id, $symbol, $speciesId;
}
close SQL;

sub processPub
{
    my ($sifDir, $newSifDir) = @_;

    my @sifs = glob("$sifDir/*.sif");
    
    #my @sifs = "$sifDir/" . "24.sif";
    

    my $humanIndex = -1;
    foreach my $i (0..$#ORGS)
    {
	if($ORGS[$i] =~ /homo sapiens/i)
	{
	    $humanIndex = $i;
	}
    }
    print STDERR "### Human index in species array: $humanIndex\n";
    
    foreach my $file (@sifs)
    {
	my ($volume, $dirs, $name) = File::Spec->splitpath( $file );

	print STDERR "reading $file\n";
	print STDERR "writing $newSifDir/$name\n";

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
		
		printf OUT ("%s\t%s\t%s\n", 
			    normalizeHumanEntrezId($gene1, $humanIndex), 
			    $type,
			    normalizeHumanEntrezId($gene2, $humanIndex));
		
		
	    }
	}
	close SIF;
	close OUT;
    }
}


sub normalizeHumanEntrezId
{
    my ($gene, $humanIndex) = @_;

    if($humanIndex >= 0)
    {
	my @g = split(/\|/, $gene);

	my $entrezId = $g[$humanIndex];
	my @symbols;
	if($humanXrefTable->exists($entrezId))
	{
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

		    print "@@@ $symbol\n" if($entrezId == 927);

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
	}

	if(scalar(@symbols) == 0)
	{
	    ## Lookup in entrez gene mapping
	    if($entrezGeneTable->existsGid($entrezId))
	    {
		$g[$humanIndex] = $entrezGeneTable->gid2symbol($entrezId);
		printf STDERR (" !!! found in entrez gene table: %s [%d]\n",
			       $g[$humanIndex], $entrezId);
		$missing{$entrezId} = {symbol => $g[$humanIndex],
				       species => "Homo sapiens"};
	    }
	    else
	    {
		print STDERR ">>> No symbol for $entrezId [$gene]\n";
		$missing{$entrezId} = { symbol => $entrezId,
					species => "Homo sapiens"};
	    }
	}
	else
	{
	    $g[$humanIndex] = $symbols[0];
	}
	
	#print "@@@ $g[$humanIndex]\n" if($entrezId eq "5112");

    	return join("|", @g);
    }

    return $gene;
}


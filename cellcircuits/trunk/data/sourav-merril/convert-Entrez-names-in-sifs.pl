#!/usr/bin/perl -I../../meta-analysis -w

use IPITable;
use MultiOrganismSIF;
use GeneNameMapper;
use EdgeMapper;
use HumanUniprot2GO;

my $tab = IPITable->new("ipi.HUMAN.xrefs");

my $go = HumanUniprot2GO->new("human-genes-in-GO-DB.txt");

my $nm = GeneNameMapper->new();
my $em = EdgeMapper->new();

my @sifs = glob("sifs/*.sif");

#my @sifs = qw(sifs/27.sif);

my %genes;

foreach my $file (@sifs)
{
    print STDERR "reading $file\n";
    my $sif = MultiOrganismSIF->new($file, 
				    ["Saccharomyces cerevisiae","Homo sapiens"], 
				    $nm,
				    $em);

    foreach my $gene (keys %{$sif->org2genes()->{"Homo sapiens"}})
    {
	my %done;
	my $text = [];
	
	if($tab->exists($gene))
	{
	    map { 
		if($go->exists($_))
		{
		    push @{$text}, join(":", 
					$_,
					$go->uniprot2symbol($_),
					$go->uniprot2go($_));
		}
		elsif(/(.*)-\d+/ && 
		      $go->exists($1))
		{
		    if(!exists($done{$1}))
		    {
			$done{$1}++;
			push @{$text}, join(":", 
					    $1,
					    $go->uniprot2symbol($1),
					    $go->uniprot2go($1));
		    }
		}
		else
		{
		    push @{$text}, join(":", $_, "**not-in-GO-database**");
		}
	    } @{$tab->get($gene)};
	}

	$genes{$gene} = $text;
    }
}

printf STDERR "### %d total entrez ids\n", scalar(keys %genes);

foreach my $gene (sort {$a <=> $b} keys %genes)
{
    printf "%s\t%s\n\n", $gene, join("\n\t", @{$genes{$gene}});
}



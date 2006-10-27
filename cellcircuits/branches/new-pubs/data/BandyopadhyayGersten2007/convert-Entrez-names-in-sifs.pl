#!/usr/bin/perl -I../../meta-analysis -w

use IPITable;
use MultiOrganismSIF;
use GeneNameMapper;
use EdgeMapper;
use HumanUniprot2GO;

my $tab = IPITable->new("../ipi.HUMAN.xrefs");

my $go = HumanUniprot2GO->new("../../db/dump/human-genes-in-GO-DB.txt");

my $nm = GeneNameMapper->new();
my $em = EdgeMapper->new();

my @sifs = glob("sifs/*.sif");

#my @sifs = qw(sifs/24.sif);

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
		    #my $symb = $go->uniprot2symbol($_);
		    #if($symb !~ /$_/)
		    #{
		    push @{$text}, join(":", 
					$_,
					$go->uniprot2symbol($_),
					$go->uniprot2go($_));
		    #}
		}
		elsif(/(.*)-\d+/ && 
		      $go->exists($1))
		{
		    my $uid= $1;
		    if(!exists($done{$uid}))
		    {
			#my $symb = $go->uniprot2symbol($uid);
			#if($symb !~ /$uid/)
			#{
			$done{$uid}++;
			push @{$text}, join(":", 
					    $uid,
					    $go->uniprot2symbol($uid),
					    $go->uniprot2go($uid));
			#}
		    }
		}
		else
		{
		    #uncomment to see uniprot ids that are not in the GO DB
		    #push @{$text}, join(":", $_, "**not-in-GO-database**");
		}
	    } @{$tab->get($gene)};
	}

	$genes{$gene} = $text;
    }
}

printf STDERR "### %d total entrez ids\n", scalar(keys %genes);

foreach my $gene (sort {$a <=> $b} keys %genes)
{
    if(scalar(@{$genes{$gene}}) == 0)
    {
	print STDERR "### No mappings for $gene\n";
    }
    printf "%s\t%s\n\n", $gene, join("\n\t", @{$genes{$gene}});
}



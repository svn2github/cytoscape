#!/usr/bin/perl -w

use MultiOrganismSIF;
use EdgeMapper;

my $sif = MultiOrganismSIF->new("/opt/www/htdocs/search/data/Suthram2005_Nature/sif/94.sif", 
				["Plasmodium falciparum"], 
				sub{ return shift;},
				EdgeMapper->new());

print "### Single organism SIF\n";
print $sif->print(); 


my $sif2 = MultiOrganismSIF->new("/opt/www/htdocs/search/data/Suthram2005_Nature/sif/Endocytosis.sif", 
				["Plasmodium falciparum", "Saccharomyces cerevisiae"], 
				 sub{ return shift;},
				 EdgeMapper->new());

print "### Multi Organism SIF\n";
print $sif2->print(); 

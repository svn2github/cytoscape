#!/usr/bin/perl -w

use MultiOrganismSIF;
use EdgeMapper;
use GeneNameMapper;

my $gMap = GeneNameMapper->new();
my $eMap = EdgeMapper->new();

my $dataDir = "/opt/www/htdocs/search/data";

test("Single organism", "Suthram2005_Nature/sif/94.sif", 
     ["Plasmodium falciparum"]);

test("2 organism", "Suthram2005_Nature/sif/Endocytosis.sif", 
     ["Plasmodium falciparum", "Saccharomyces cerevisiae"]);

test("3 organism", "Sharan2005_PNAS/sif/three-way/728.sif", 
     ["Saccharomyces cerevisiae", "Caenorhabditis elegans",
      "Drosophila melanogaster"]);

test("yeast genes", "Yeang2005_GB/sif/variant35.sif", 
     ["Saccharomyces cerevisiae"]);


test("yeast genes", "Yeang2005_GB/sif/variant3.sif", 
     ["Saccharomyces cerevisiae"]);


test("yeast genes", "Yeang2005_GB/sif/variant25.sif", 
     ["Saccharomyces cerevisiae"]);

sub test
{
    my ($msg, $file, $orgs) = @_;
    my $sif = MultiOrganismSIF->new(join("/", $dataDir, $file), 
				    $orgs,
				    $gMap,
				    $eMap);

    print "\n### $msg\n";
    print $sif->print(1); 
}


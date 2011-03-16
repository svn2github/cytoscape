#!/usr/bin/env perl

use strict;
use warnings;

use GenomeSegm1;

my $gsm = new GenomeSegm 100;
$gsm->add_region("chr10", 105, 304, "XXYYZZ1", "This is a test #1.");
$gsm->add_region("chr10",  99, 800, "XXYYZZ2");
$gsm->add_region("chr10", 900, 1000, "XXYYZZ3");
$gsm->add_region("chr10", 900, 1000, "XXYYZZ3");
$gsm->add_region("chr10", 900, 1000, "XXYYZZ3");
$gsm->add_region("chr10", 900, 1000, "XXYYZZ3");
$gsm->add_region("chr10", 900, 1001, "XXYYZZ3");
$gsm->add_region("chr11", 700, 900, "XXYYZZ3");

$gsm->add_region("chr12", 50, 100, "Region 1");
$gsm->add_region("chr12", 90, 200, "Region 2");
$gsm->add_region("chr12", 30, 230, "Region 3");
$gsm->add_region("chr12", 250, 300, "Region 4");
$gsm->add_region("chr12", 400, 500, "Region 1");
$gsm->add_region("chr12", 600, 800, "Region 1");
$gsm->add_region("chr12", 700, 750, "Region 1");

$gsm->display_region_info("chr10");

print "Region query:\n";
for my $annotation ($gsm->query_region("chr12", 100, 270)){
    print join("\t", @$annotation), "\n";
}

print "Spacer region:\n";
for my $annot ($gsm->spacer_region("chr12")){

    my($spacer_start, $spacer_end,
       $region_left, $region_right) = @$annot;
    print join("\t", $spacer_start, $spacer_end,
	$region_left, $region_right), "\n";

}

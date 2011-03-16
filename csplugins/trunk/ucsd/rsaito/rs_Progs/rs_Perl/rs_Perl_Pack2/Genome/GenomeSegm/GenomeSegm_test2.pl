#!/usr/bin/env perl

use strict;
use warnings;

use GenomeSegm2;

my $gsm = new GenomeSegm 100;
$gsm->add_region("chr10", "+", 105, 304, "XXYYZZ1", "This is a test #1.");
$gsm->add_region("chr10", "+",  99, 800, "XXYYZZ2");
$gsm->add_region("chr10", "+", 900, 1000, "XXYYZZ3");
$gsm->add_region("chr10", "+", 900, 1000, "XXYYZZ3");
$gsm->add_region("chr10", "-", 900, 1000, "XXYYZZ3");
$gsm->add_region("chr10", "+", 900, 1000, "XXYYZZ3");
$gsm->add_region("chr10", "+", 900, 1001, "XXYYZZ3");
$gsm->add_region("chr11", "+", 700, 900, "XXYYZZ3");

$gsm->add_region("chr12", "+",  50, 100, "Region 1");
$gsm->add_region("chr12", "+",  90, 200, "Region 2");
$gsm->add_region("chr12", "-",  30, 230, "Region 3");
$gsm->add_region("chr12", "+", 250, 300, "Region 4");
$gsm->add_region("chr12", "+", 400, 500, "Region 1", "Hello!");
$gsm->add_region("chr12", "+", 600, 800, "Region 1");
$gsm->add_region("chr12", "+", 700, 750, "Region 1");

$gsm->display_region_info("chr10");

print "Region query:\n";
for my $region_info ($gsm->query_region("chr12", 100, 270, "-")){
    print join("\t", @$region_info), "\n";
}

print "Spacer region:\n";
for my $annot ($gsm->spacer_region("chr12", "+")){

    my($spacer_start, $spacer_end,
       $region_left, $region_right) = @$annot;
    print join("\t", $spacer_start, $spacer_end,
	$region_left, $region_right), "\n";

}
print "Spacer region #2:\n";
for my $annot ($gsm->spacer_region2("chr12", "+")){

    my($spacer_start, $spacer_end,
       $region_left_info, $region_right_info) = @$annot;
    my($start_left, $end_left, $region_ID_left, $strand_left) = @$region_left_info;
    my($start_right, $end_right, $region_ID_right, $strand_right) = @$region_right_info;

    print join("\t", $spacer_start, $spacer_end,
	       $region_ID_left, $strand_left, $start_left, $end_left,
	       $region_ID_right, $strand_right, $start_right, $end_right), "\n";

}

print "Region info:\n";
for my $region_info ($gsm->get_info_from_region_ID("Region 1")){
    print join("\t", @$region_info), "\n";
}

print "Chromosome info:\n";
for my $chr ($gsm->get_chromosomes()){
    for my $region_info ($gsm->get_chr_info($chr)){
	print join("\t", $chr, @$region_info), "\n";
    }
}

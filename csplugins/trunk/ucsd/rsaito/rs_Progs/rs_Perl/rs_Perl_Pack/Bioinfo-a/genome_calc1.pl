#!/usr/bin/perl

use strict;
use G;

my $genome_file = shift @ARGV;
my $gb = new G($genome_file, "no msg");

$gb->seq_info();

my %count;

my $length = $gb->{LOCUS}->{length};
for my $i (0..$length-2){
    my $nuc1 = $gb->getseq($i, $i);
    my $nuc2 = $gb->getseq($i+1, $i+1);
    $count{ $nuc1 }->{ $nuc2 } ++;
}

foreach my $nuc1 (keys %count){
    foreach my $nuc2 (keys %{$count{ $nuc1 }}){
	print join("\t", $nuc1, $nuc2,
		   $count{$nuc1}->{$nuc2}), "\n";
    }
}







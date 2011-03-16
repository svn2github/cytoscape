#!/usr/bin/env perl

use strict;
use warnings;

use G;

my $gb = new G("ecoli", "no msg");

say $gb->{"thrL"}->{translation};

for my $cds ($gb->feature('CDS')){

    my $gene;
    if(defined($gb->{$cds}->{gene})){
	$gene = $gb->{$cds}->{gene};
    }
    else {
	$gene = "?";
    }

    my $start = $gb->startcodon($cds);

    print join("\t", $gene, $start), "\n";

}



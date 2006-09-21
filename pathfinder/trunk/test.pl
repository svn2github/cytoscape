#!/usr/bin/perl -w

use DirectedGraph;

my $g = DirectedGraph->new("test.sif");

#$g->populateFromSIF("test.sif");
$g->print();

my $g2 = DirectedGraph->new();

$g2->populateFromSIF("test.sif");
$g2->print();

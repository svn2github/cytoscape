#!/usr/bin/env perl

use strict;
use warnings;
require Exporter;

use RS_data_handler;
use rslib_SOM qw(distance find_winner neighbour);

*::X_LEN = \ 10;
*::Y_LEN = \ 10;

my $data_obj = new RS_data_handler "./testdata1.txt";

print "Dimension: ", $data_obj->dim(), "\n";
print "Some input data:\n";
for my $i (0..29){
    my($label, @data) = $data_obj->next_data();
    print join("\t", $label, @data), "\n";
}

print "All data:\n";
for my $data_ref (@{$data_obj->all_data()}){
    
    my($label, @data) = @$data_ref;
    print join("\t", $label, @data), "\n";

}

my @w;

for(my $j = 0; $j < $::X_LEN * $::Y_LEN;$j ++){
    for(my $i = 0;$i < $data_obj->dim();$i ++){
	$w[$j]->[$i] = rand();
    }
}

my($label, @data) = $data_obj->next_data();

my $min_j = find_winner(\@data, \@w);
print "$min_j\n";

my @neighbours = neighbour($min_j, 3, $::X_LEN, $::Y_LEN);
print "Neighbours: ", join(",", @neighbours), "\n";


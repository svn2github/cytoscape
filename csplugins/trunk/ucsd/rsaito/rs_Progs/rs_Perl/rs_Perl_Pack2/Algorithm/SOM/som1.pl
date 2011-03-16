#!/usr/bin/env perl

use strict;
use warnings;
require Exporter;

use RS_data_handler;
use rslib_SOM qw(distance find_winner neighbour);

*::X_LEN = \ 10;
*::Y_LEN = \ 10;
*::MAX_TIME = \ 1000;

sub update_rate($){

    my $t = shift;

    return 1.0 * $::MAX_TIME / ($t + 1);

}


my $data_obj = new RS_data_handler $ARGV[0];

my @w;

for(my $j = 0; $j < $::X_LEN * $::Y_LEN;$j ++){
    for(my $i = 0;$i < $data_obj->dim();$i ++){
	$w[$j]->[$i] = rand();
    }
}

for my $t (0..$::MAX_TIME - 1){

    my($label, @data) = $data_obj->next_data();
    my $min_j = find_winner(\@data, \@w);
    my @neighbours = neighbour($min_j, 3, $::X_LEN, $::Y_LEN);

    for my $j (@neighbours){
	for my $i (0..$data_obj->dim() - 1){
	    $w[$j]->[$i] += update_rate($t) * ($data[$i] - $w[$j]->[$i]);
	}
    }

}

for my $data_ref (@{$data_obj->all_data()}){
    
    my($label, @data) = @$data_ref;
    my $min_j = find_winner(\@data, \@w);
    my $x = $min_j % $::X_LEN;
    my $y = int($min_j / $::X_LEN);
    print join("\t", $label, $min_j, $x, $y, @data), "\n";

}

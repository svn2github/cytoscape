#!/usr/bin/env perl

use strict;
use warnings;

*::INF = \ 9999999;

sub distance($$){

    my $data1 = shift;
    my $data2 = shift;


    my  $total;
    
    for($total = 0.0, my $k = 0;$k <= $#$data1;$k ++){
	$total += ($data1->[$k] - $data2->[$k]) ** 2;
    }

    # print "#Input 1: ", join(",", @$data1), "\n";
    # print "#Input 2: ", join(",", @$data2), "\n";
    # print "$total\n";

    return $total;

}


sub find_winner($$){

    my $inp = shift;
    my $w   = shift;

    my($min_j);
    my($dist, $min);

    for(my $min = $::INF, $min_j = 0, my $j = 0;$j <= $#$w;$j ++){

	my $dist = distance($inp, $w->[$j]);

	# print "$j, $dist\n";
	
	if($dist < $min){
	    $min = $dist;
	    $min_j = $j;
	}
	
    }

    # print "Min j = $min_j\n";
	 
    return $min_j;
    
}

sub neighbour($$$$){

    my $j = shift;
    my $range = shift;
    my $x_len = shift;
    my $y_len = shift;
	
    my $x = $j % $x_len;
    my $y = int($j / $x_len);
	
    my @positions = ();

    my($k, $l);

    # print "Position $j ($x, $y)\n";

    if(($l = $y - $range) < 0){ $l = 0; }
    for(;$l < $y_len && $l <= $y + $range;$l ++){
	if(($k = $x - $range) < 0){ $k = 0 };
	for(;$k < $x_len && $k <= $x + $range;$k ++){
	    if(($k - $x)**2 + ($l - $y)**2 <= $range ** 2){
		push(@positions, $k + $l * $x_len);
	    }
	}
    }

    return @positions;

}

1;

#!/usr/bin/env perl

use strict;
use warnings;

package GenomeSingleBaseMap;

## --- Genomic coordinate starts with 0 --- ##

sub new {

    my $class = shift;
    bless {};

}

sub add_map($$$$){

    my $map_info = shift;
    my $chrom    = shift;
    my $strand   = shift;
    my $start    = shift;
    my $end      = shift;

    for(my $i = $start;$i <= $end;$i ++){
	$map_info->{ map_info }->{ $chrom }->{ $strand }->[ $i ] += 1; #
    }
}

sub query_region($$$$$){
    
    my $map_info = shift;
    my $chrom    = shift;
    my $start    = shift;
    my $end      = shift;  
    my $strand_i   = shift; # Arbitrary
    
    my @ret = ();

    for my $strand ("+", "-"){
	if(!defined($strand_i) or $strand_i eq $strand){
	    for(my $i = $start;$i <= $end;$i ++){
		$ret[$i - $start ] += 
		    defined($map_info->{ map_info }->{ $chrom }->{ $strand }->[ $i ]) ?
		    $map_info->{ map_info }->{ $chrom }->{ $strand }->[ $i ] :
		    0; ##
	    }
	}
    }

    return @ret;
}

1;

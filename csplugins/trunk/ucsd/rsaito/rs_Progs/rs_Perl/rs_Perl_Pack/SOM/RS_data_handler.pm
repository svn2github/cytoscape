#!/usr/bin/env perl

use strict;
use warnings;

package RS_data_handler;

sub new {
    my $class = shift;
    my $filename = shift;
    local *FH;

    my $data;

    open(FH, $filename) || die "Cannot open \"$filename\": $!";
    while(<FH>){
	chomp;
	my($label, @data) = split(/\t/);
	if(defined($data->{ $label })){
	    die "$label duplicate...";
	}
	push(@{$data->{ order }}, $label);
	$data->{ data }->{ $label } = [ @data ];
    }
    close FH;

    $data->{ pointer } = 0;
    bless $data;
    
}

sub next_data {

    my $obj = shift;
    my $pointer = $obj->{ pointer };
    my $label   = $obj->{ order }->[ $pointer ];
    my $ret     = $obj->{ data }->{ $label };

    $obj->{ pointer } ++;

    if($obj->{ pointer } > $#{$obj->{ order }}){
	$obj->{ pointer } = 0;
    }
    return($label, @$ret);

}

sub dim {
    
    my $obj = shift;

    my $label   = $obj->{ order }->[ 0 ];
    my $d     = $obj->{ data }->{ $label };

    return $#$d + 1;

}

sub all_data {
    my $obj = shift;
    my @ret = ();

    for my $label (@{$obj->{ order }}){
	push(@ret, [ $label, @{$obj->{ data }->{ $label }} ]);
    }

    return \@ret;
}

1;


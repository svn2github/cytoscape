#!/usr/bin/perl -w

use strict;

package Plot_field;

sub new {
    my $class = shift;
    my($x1, $y1, $x2, $y2, $w, $h) = @_;
    
    if(!defined($w) || $w !~ /\d/){ $w = 70; }
    if(!defined($h) || $h !~ /\d/){ $h = 40; }

    my $field = {
	"x1" => $x1,
	"y1" => $y1,
	"x2" => $x2,
	"y2" => $y2,
	"h" => $h,
	"w" => $w,
	"Plot" => [],
	"String" => []
	};

    bless $field;

}

sub x1 { my $obj = shift; return $obj->{"x1"}; }
sub x2 { my $obj = shift; return $obj->{"x2"}; }
sub y1 { my $obj = shift; return $obj->{"y1"}; }
sub y2 { my $obj = shift; return $obj->{"y2"}; }
sub h { my $obj = shift; return $obj->{"h"}; }
sub w { my $obj = shift; return $obj->{"w"}; }
sub plot { my $obj = shift; return $obj->{"Plot"}; }
sub string { my $obj = shift; return $obj->{"String"}; }

sub clear { 
    my $obj = shift; 
    $obj->{"Plot"} = [];
    $obj->{"String"} = [];
}

sub round($){

    my $n = shift;
    my $nx10_r = int($n) * 10;
    my $nx10 = int($n * 10);
    my $th10 = $nx10 - $nx10_r;
    if($th10 < 5){ return int($n); }
    else { return int($n) + 1; }

}

sub plot_height {

    my $obj = shift;
    return ($obj->y2 - $obj->y1) / $obj->h;

}

sub plot_width {

    my $obj = shift;
    return ($obj->x2 - $obj->x1) / $obj->w;

}

sub add_plot {
    my $obj = shift;
    my($x, $y, $e) = @_;

    if(length($e) ne 1){
	die "Illegal plot : ($x, $y) = $e\n";
    }

    push(@{$obj->plot}, [ $x, $y, $e ]);

}

sub add_str {
    my $obj = shift;
    my($x, $y, $str, $mode) = @_;

    push(@{$obj->string}, [ $x, $y, $str, $mode ]);

}

sub add_ruler {
    my $obj = shift;

    my $lower_left = "(" . $obj->x1 . "," . $obj->y1 . ")";
    my $lower_right = "(" . $obj->x2 . "," . $obj->y1 . ")";
    my $upper_left = "(" . $obj->x1 . "," . $obj->y2 . ")";
    my $upper_right = "(" . $obj->x2 . "," . $obj->y2 . ")";

    $obj->add_str($obj->x1, $obj->y1, $lower_left, 1);
    $obj->add_str($obj->x2 - $obj->plot_width, $obj->y1,
		  $lower_right, -1);
    $obj->add_str($obj->x1, $obj->y2 - $obj->plot_height, 
		  $upper_left, 1);
    $obj->add_str($obj->x2 - $obj->plot_width, $obj->y2 - $obj->plot_height,
		  $upper_right, -1);

}

sub disp_field {
    my $obj = shift;
    $obj->add_ruler;

    my @field;
   
    foreach my $plot (@{$obj->plot}){
	my($x, $y, $e) = @$plot;
	
	my $p = round $obj->w * 
	    ($x - $obj->x1) / ($obj->x2 - $obj->x1);
	my $q = round $obj->h *
	    ($y - $obj->y1) / ($obj->y2 - $obj->y1);

	if(0 <= $p && $p < $obj->w &&
	   0 <= $q && $q < $obj->h){
	    $field[ $q ]->[ $p ] = $e;	    
	}

    }

    foreach my $string (@{$obj->string}){
	my($x, $y, $str, $mode) = @$string;

	my $p = round $obj->w * 
	    ($x - $obj->x1) / ($obj->x2 - $obj->x1);
	my $q = round $obj->h *
	    ($y - $obj->y1) / ($obj->y2 - $obj->y1);
	
	my $p_start = $p;
	if($mode == -1){
	    $p_start = $p - (length($str) - 1);
	}
	elsif($mode == 0){
	    $p_start = $p - ((length($str)+1) / 2 - 1);
	}
	
	for my $i (0..length($str) - 1){
	    if(0 <= $q && $q < $obj->h &&
	       0 <= $p && $p < $obj->w){
		$field[$q]->[$p_start + $i] = substr($str, $i, 1);
	    }
	}
    }


#    system("clear");
    for(my $q = $obj->h - 1;$q >= 0; $q -= 1){
	foreach my $p (0..$obj->w - 1){
	    if(defined($field[$q]->[$p])){
		print $field[$q]->[$p];
#		print "($p, $q) = $field[$q]->[$p]\n";
	    }
	    else { print "."; }
	}
	print "\n";
    }

}

1;

# package main;

#while(1){
#    print "Input decimal: ";
#    my $num = <STDIN>;
#    chomp ($num);
#    print Plot_field::round $num, "\n";
#
# }

# my $fld = new Plot_field 0,0,100,150;

# $fld->add_plot(20, 20, "x");
# $fld->add_plot(20, 50, "y");
# $fld->add_plot(0, 50, "z");
# $fld->add_str(50, 75, "Hello!!!", 0);
# $fld->disp_field;


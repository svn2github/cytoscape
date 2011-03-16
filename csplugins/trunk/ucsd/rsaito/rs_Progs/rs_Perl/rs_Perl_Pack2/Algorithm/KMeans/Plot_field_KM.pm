#!/usr/bin/perl -w

use strict;

package Plot_field_KM; # This declaration must come first.
use vars qw(@ISA);
use Plot_field; # This declaration must come after "package weighted_cube".

@ISA = qw(Plot_field);

sub new {
    my $class = shift;
    my($x1, $y1, $x2, $y2, $w, $h) = @_;

    if(!defined($w)){ $w = ""; }
    if(!defined($h)){ $h = ""; }

    my $fld_KM = new Plot_field $x1, $y1, $x2, $y2, $w, $h;
    bless $fld_KM;

}


sub plot_kmeans {
    my $fld = shift;
    my $points_belong = shift;
    my $target_position = shift;
    my $ref_position = shift;
    my $points = shift;
    my $clusters = shift;
    
    $fld->clear;
    foreach my $p (0..$#$points){
	my($x, $y) = @{$points->[$p]};
	my $belong_cluster;
	for($belong_cluster = 0;$belong_cluster < $clusters;
	    $belong_cluster ++){
	    if($points_belong->[$belong_cluster]->[$p]){ last; }
	}
	$fld->add_plot($x, $y, "$belong_cluster");
    }

    foreach my $c (0..$clusters - 1){
	if(!defined($target_position->[$c])){ next; }
	my($cx, $cy) = @{$target_position->[$c]};
	if(defined($cx) && $cx =~ /\d/ &&
	   defined($cy) && $cy =~ /\d/){
	    $fld->add_str($cx, $cy, "->" . $c . "<-", 0);
	}
    }

    foreach my $c (0..$clusters - 1){
	my($cx, $cy) = @{$ref_position->[$c]};
	$fld->add_str($cx, $cy, "[" . $c . "]", 0);
    }

    $fld->disp_field;

}


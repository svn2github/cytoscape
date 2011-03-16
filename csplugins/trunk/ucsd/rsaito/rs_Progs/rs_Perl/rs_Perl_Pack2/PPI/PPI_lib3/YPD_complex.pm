#!/usr/bin/perl -w

use strict;
package YPD_complex;
require Exporter;

@YPD_complex::ISA = qw(Exporter);
@YPD_complex::EXPORT = qw(read_YPD_complex read_YPD_complex2); 

sub read_YPD_complex($$$$$){

    my $filename = shift;
    my $form_complex_ref = shift;
    my $complex_component_ref = shift;
    my $component_combi_ref = shift;
    my $synonym_ref = shift;

    my($complex_name);
    my $protein;
    my(@tmp, @r);
    my($i, $j);

    local(*FH);

    open(FH, $filename) || die "Cannot open \"$filename\": $!";

    while(<FH>){
	chomp;
	@tmp = split(/\t/);
	@r = ();
	foreach(@tmp){
	    if(defined($synonym_ref->{ $_ })){ 
		push(@r, $synonym_ref->{ $_ });
	    }
	    else { push(@r, $_); }
	}
	$complex_name = shift(@r);
	$complex_component_ref->{ $complex_name } = [ @r ];
	foreach $protein (@r){ $form_complex_ref->{ $protein } = ""; }
	for $i (0..$#r){
	    for $j (0..$#r){
		$component_combi_ref->{ $r[$i] }->{ $r[$j] } = "";
	    }
	}
    }

    close FH;

}

sub read_YPD_complex2($$$$$$){

    my $filename = shift;
    my $form_complex_ref = shift;
    my $complex_component_ref = shift;
    my $component_combi_ref = shift;
    my $component_belong_ref = shift;
    my $synonym_ref = shift;

    my @belong_complex;
    my($each_complex, $each_component);

    read_YPD_complex($filename, $form_complex_ref,
		     $complex_component_ref, $component_combi_ref,
		     $synonym_ref);
    
    foreach $each_complex (keys(%$complex_component_ref)){
	foreach $each_component
	    (@{$complex_component_ref->{ $each_complex }}){
		push(@{$component_belong_ref->{ $each_component }},
		     $each_complex);
	    }
    }

}

sub judge_common_complex($$$){
    my($p1, $p2, $component_belong_ref) = @_;
    
    foreach my $element1 (@{$component_belong_ref->{$p1}}){
	foreach my $element2 (@{$component_belong_ref->{$p2}}){
	    if($element1 eq $element2){ return 1; }
	}
    }

    return 0;

}

# @{$component_belong_ref->{$p}} must NOT be redundant.
sub return_common_complexes($$$){

    my($p1, $p2, $component_belong_ref) = @_;
    my(%belong_complex, @common);
    
    foreach my $element (@{$component_belong_ref->{$p1}}){
	$belong_complex{ $element } = 1;
    }

    foreach my $element (@{$component_belong_ref->{$p2}}){
	if(defined($belong_complex{ $element })){
	    push(@common, $element);
	}
    }

    return @common;

}

1;


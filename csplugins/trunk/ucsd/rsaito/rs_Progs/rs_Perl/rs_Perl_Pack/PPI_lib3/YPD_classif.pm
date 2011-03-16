#!/usr/bin/perl -w

use strict;
package YPD_classif;
require Exporter;

@YPD_classif::ISA = qw(Exporter);
@YPD_classif::EXPORT = qw(read_YPD_synonym read_YPD_classification
			  read_YPD_class_names); 

sub del_redu2($){

   my($list_ref) = @_;
   my(%redu_check);

   undef(%redu_check);
   @redu_check{ @$list_ref } = "";
   @$list_ref = keys(%redu_check); 
  
}

sub read_YPD_synonym($){
    my($ypd_file) = @_;
    local(*FH);
    my(%synonym);
    my($i, @r);
    my($protein);

    open(FH, $ypd_file) || die "Cannot open \"$ypd_file\":$!";
    while(<FH>){
	chomp;
	@r = split(/\t/);
	$r[1] =~ s/ //g;
	$synonym{ $r[1] } = $r[1];
	for($i = 3; $i <= $#r; $i++){
	    if(defined($r[$i])){ 
		$r[$i] =~ s/ //g; 
		if(!defined($synonym{ $r[$i] }) ||
		   $synonym{ $r[$i] } ne $r[$i]){ 
		    $synonym{ $r[$i] } = $r[1];
		}
	    }
	}
    }
    close FH;
    return %synonym;
}

sub read_YPD_classification($){
    my($ypd_file) = @_;
    local(*FH);
    my(%classif);
    my($i, @r);
    my($protein);

    open(FH, $ypd_file) || die "Cannot open \"$ypd_file\":$!";
    while(<FH>){
	chomp;
	@r = split(/\t/);
	$r[1] =~ s/ //g;
	if(!defined($classif{ $r[1] })){ $classif{ $r[1] } = [ $r[0] ]; }
	else { push(@{$classif{ $r[1] }}, $r[0]); }
	for($i = 3; $i <= $#r; $i++){
	    if(defined($r[$i])){ 
		$r[$i] =~ s/ //g; 
		if(!defined($classif{ $r[$i] })){ 
		    $classif{ $r[$i] } = [ $r[0] ];
		}
		else { push(@{$classif{ $r[$i] }}, $r[0]); }
	    } 
	}
    }
    close FH;

    foreach $protein (keys(%classif)){
	del_redu2($classif{ $protein });
    }

    return %classif;

}

sub read_YPD_class_names($){

    my($ypd_file) = @_;
    local(*FH);
    my(@r);
    my(%names);
    
    open(FH, $ypd_file) || die "Cannot open \"$ypd_file\":$!";
    while(<FH>){
	chomp;
	@r = split(/\t/);
# 	$r[0] =~ s/ //g; # I cannot recall why this is necessary...
	$names{ $r[0] } = ""; 
    }
    close FH;

    return keys(%names);

}


1;

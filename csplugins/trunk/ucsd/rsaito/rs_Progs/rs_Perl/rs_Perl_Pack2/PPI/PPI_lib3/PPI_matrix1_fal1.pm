#!/usr/bin/perl -w

use strict;

package PPI_matrix1;

sub guilt_func($$$){

    my($object, $id, $ypd_class_ref) = @_;
    my($neibour, @neibours, @assoc_func);

    @neibours = $object->guilt_protein($id);

    foreach $neibour (@neibours){
	if($id eq $neibour){ next; }
	if(defined($ypd_class_ref->{ $neibour })){
	    push(@assoc_func, @{$ypd_class_ref->{ $neibour }});
	}
    }

    del_redu2(\@assoc_func);
    return @assoc_func;

}

sub repeated_func($$$){

    my($object, $id, $ypd_class_ref) = @_;
    my($neibour, @neibours, @assoc_func, @rep_func);
    my(%rep);

    @neibours = $object->guilt_protein($id);

    foreach $neibour (@neibours){
	if($id eq $neibour){ next; }
	if(defined($ypd_class_ref->{ $neibour })){
	    push(@assoc_func, @{$ypd_class_ref->{ $neibour }});
	}
    }

    undef(@rep_func);
    undef(%rep);

    foreach(@assoc_func){
	if(!defined($rep{ $_ })){ $rep{ $_ } = 1; }
	else { $rep{ $_ } ++; }
    }
    foreach(keys(%rep)){
	if($rep{ $_ } >= 2){ push(@rep_func, $_); }
    }

    return @rep_func;

}

1;

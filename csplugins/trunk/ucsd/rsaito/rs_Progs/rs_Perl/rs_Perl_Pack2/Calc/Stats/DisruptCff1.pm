#!/usr/bin/env perl

use strict;
use warnings;

package DisruptCff;

sub new($$$){
    my $class = shift;
    my($s1_ref, $s2_ref) = @_;

    my $dcff = {
	s1    => [],
	s2    => [],
	count => {
	    "00" => 0,
	    "01" => 0,
	    "10" => 0,
	    "11" => 0
	}
    };

    bless $dcff;

    $dcff->add($s1_ref, $s2_ref);

    return $dcff;

}

sub add($$$){

    my $object = shift;
    my($s1_ref, $s2_ref) = @_;
    
    if($#$s1_ref != $#$s2_ref){
	die "Length of two vectors not identical: ", 
	$#$s1_ref+1, " and ", $#$s2_ref+1, "\n";
    }
    
    push(@{$object->{ s1 }}, @$s1_ref);
    push(@{$object->{ s2 }}, @$s2_ref);
    
    for my $i (0..$#$s1_ref){
	my $b1 = $s1_ref->[$i];
	my $b2 = $s2_ref->[$i];

	if($b1 != 0 and $b1 != 1){
	    die "Illegal element in vector 1.";
	}
	if($b2 != 0 and $b2 != 1){
	    die "Illegal element in vector 2.";
	}

	my $b12 = $b1 . $b2;
	$object->{ count }->{ $b12 } ++;

    }

}

sub n($){ # Total number of samples

    my $object = shift;
    return 
	$object->c00 + $object->c01 + 
	$object->c10 + $object->c11;

}

sub c00($){ # True negatives
    my $object = shift;
    return $object->{ count }->{ "00" };
}
sub c01($){ # False negatives
    my $object = shift;
    return $object->{ count }->{ "01" };
}
sub c10($){ # False positives
    my $object = shift;
    return $object->{ count }->{ "10" };
}
sub c11($){ # True positives
    my $object = shift;
    return $object->{ count }->{ "11" };
}

sub spe($){ # Specificity

    my $obj = shift;
    return (1.0*$obj->c00 / ($obj->c00 + $obj->c10));

}

sub sen($){ # Sensitivity

    my $obj = shift;
    return (1.0*$obj->c11 / ($obj->c01 + $obj->c11));

}

sub mcc($){ # Matthews correlation coefficient

    my $obj = shift;
    return (1.0*$obj->c11*$obj->c00-$obj->c10*$obj->c01)
	  /(sqrt(($obj->c11+$obj->c10)*($obj->c11+$obj->c01)*
	         ($obj->c00+$obj->c10)*($obj->c00+$obj->c01)));

}

sub chi2($){ # Chi-square value (df = 1)

    my $obj = shift;
    return $obj->mcc ** 2 * $obj->n;

}

sub chi2prob($){

    my $obj = shift;
    my $chi2 = $obj->chi2;

    my $chisprob = Statistics::Distributions::chisqrprob(1, $chi2);

    return $chisprob;

}

1;



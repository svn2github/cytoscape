#!/usr/bin/perl -w

use strict;

sub factorial($);

sub factorial($){
     my $n = shift;
     if($n==1){ $n=1; }                 # formula (1) 
     else { $n=$n*factorial($n-1); }    # formula (2) 
     return $n;
}

my $a=factorial(4);
printf("%d\n",$a);


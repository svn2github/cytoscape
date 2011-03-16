#!/usr/bin/perl -w

use strict;
use vars qw(@fib_rec);

sub fibonatcci($);

sub fibonatcci($){
   my $n = shift;
   my $ret;

   if(defined($fib_rec[$n])){ return $fib_rec[$n]; }
   if($n == 1 || $n == 2){ $ret = 1; }
   else { $ret = fibonatcci($n - 1) + fibonatcci($n - 2); }

   $fib_rec[$n] = $ret;
   return $ret; 

}

printf("%d\n", fibonatcci(5));



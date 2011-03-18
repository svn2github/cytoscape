#!/usr/bin/env perl

use strict;
use warnings;

sub test_hash_ref {
   my($h1_ref, $h2_ref) = @_;
   print $h1_ref->{ "height" }, "\n";
   print $h2_ref->{ "weight" }, "\n";
}

my %c = ("height" =>160, "weight" => 55);
my %d = ("height" =>175, "weight" => 70);
test_hash_ref(\%c, \%d);

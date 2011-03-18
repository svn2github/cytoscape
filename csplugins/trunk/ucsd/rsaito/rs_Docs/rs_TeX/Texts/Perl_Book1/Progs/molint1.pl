#!/usr/bin/env perl

use strict;
use warnings;

my %pdi = (
  "Mcm1" => { "Swi4" => 0.5, "Clb2" => 0.5 },
  "Swi4" => { "Clb2" => 1 },
  "Leu3" => { "Leu1" => 0.3, "Bat1" => 0.3 }
  );

$pdi{ "Leu3" }->{"Ilv2"} = 0.3;

print join(",", keys(%{$pdi{"Leu3"}})), "\n";

for my $m1 (keys %pdi){
  for my $m2 (keys %{$pdi{$m1}}){
    print "$m1\t$m2\t$pdi{$m1}->{$m2}\n";
  }
}







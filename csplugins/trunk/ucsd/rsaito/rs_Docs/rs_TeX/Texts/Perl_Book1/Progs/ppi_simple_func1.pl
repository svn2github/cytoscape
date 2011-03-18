#!/usr/bin/env perl

use strict;
use warnings;

my $func_file = shift @ARGV;
my $ppi_file  = shift @ARGV;

local *FH;

my %prot_func;

open(FH, $func_file) || die "Cannot open \"$func_file\": $!";
while(<FH>){
  chomp;
  my($protein, $func) = split("\t");
  $prot_func{ $protein } = $func;  
}
close FH;

open(FH, $ppi_file) || die "Cannot open \"$ppi_file\": $!";
while(<FH>){
  chomp;
  my($p1, $p2) = split("\t");
  print join("\t", $p1, $p2, $prot_func{ $p1 }, $prot_func{ $p2 }), "\n";
}
close FH;



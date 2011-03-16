#!/usr/bin/perl -w

use strict;

my $filename = shift @ARGV;
local(*FH);

open(FH, $filename) || die "Cannot open \"$filename\": $!";
my @lines = <FH>;
close FH;

print join("", reverse(@lines));

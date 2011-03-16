#!/usr/bin/perl -w

use strict;
use Storable qw(store retrieve);

my %hash = ("Mon" => "Monday", "Tue" => "Tuesday", "Wed" => "Wednesday");
$hash{ "Week" }->{ "Japanese" } = "Shu";

store(\%hash, "store.bin");


#!/usr/bin/env perl

use strict;
use warnings;

use GenomeSingleBaseMap1;

my $map = new GenomeSingleBaseMap;
$map->add_map("chr10", "+", 105, 108);
$map->add_map("chr10", "+", 101, 106);
$map->add_map("chr10", "-", 106, 107);
$map->add_map("chr12", "-", 30, 50);

my @map = $map->query_region("chr10", 100, 110, "+");
print join("", @map), "\n";


#!/usr/bin/perl -w

use strict;
use Weighted_cube::weighted_cube;

my $cube = new weighted_cube 10, 20, 30, 1200;
print $cube->volume, "\n";
print $cube->density, "\n";

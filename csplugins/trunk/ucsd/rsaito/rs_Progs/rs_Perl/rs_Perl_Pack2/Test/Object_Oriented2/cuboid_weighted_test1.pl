#!/usr/bin/env perl

use strict;
use cuboid_weighted;

my $cuboid_weighted = new cuboid_weighted 10, 20, 30, 1200;
print $cuboid_weighted->volume, "\n";
print $cuboid_weighted->density, "\n";

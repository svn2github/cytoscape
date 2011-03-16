#!/usr/bin/env perl

use strict;
use warnings;

use cuboid;

my $cuboid = new cuboid 10, 20, 30;
print $cuboid->volume, "\n";

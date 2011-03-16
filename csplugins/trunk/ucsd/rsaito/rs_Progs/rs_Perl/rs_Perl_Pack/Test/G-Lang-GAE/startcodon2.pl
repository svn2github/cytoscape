#!/usr/bin/env perl

use strict;
use warnings;

use G;

my $gb = load("ecoli");
say $gb->startcodon($_) for $gb->cds();

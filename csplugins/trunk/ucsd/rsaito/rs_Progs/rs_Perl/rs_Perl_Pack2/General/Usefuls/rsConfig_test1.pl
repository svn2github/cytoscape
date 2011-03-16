#!/usr/bin/env perl

use strict;
use warnings;

use rsConfig;

# foreach(keys %::ENV){
# 	print $_, "\t", $::ENV{$_}, "\n"
# }

my %h = read_config("testconfig");

foreach(keys %h){
 	print $_, "\t", $h{$_}, "\n";
}

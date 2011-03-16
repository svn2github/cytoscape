#!/usr/bin/env perl
#

use strict;
use warnings;

use General::Usefuls::rsConfig;
use Genome::Genes::RefFlat1;
use histone_mods1;

my %h = read_config("Histone_mods_NPS.cnf");

for my $mtype (keys %h){
	print join("\t", $mtype, $h{ $mtype }), "\n";
}
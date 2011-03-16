#!/usr/bin/perl -w

use strict;

sub print_test($){
	print shift;
}

my $func_ref = \&print_test;
&$func_ref("Hello, world!\n");


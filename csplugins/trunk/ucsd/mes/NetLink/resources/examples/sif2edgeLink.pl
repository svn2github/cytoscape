#!/usr/bin/perl

open FILE, "$ARGV[0]" or die;
while(<FILE>) {
	if ( $_ =~ /^(\w+)\s+(\w+)\s+(\w+)\s*$/ ) {
		$a = $1;
		$e = $2;
		$b = $3;

		print "$a ($e) $b = $a-$b.sif\n";
	}
}
close FILE;

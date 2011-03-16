#!/usr/bin/env perl

use strict;
use warnings;

package rsConfig;

sub read_config($){

	if(!defined($::ENV{ PERL_RS_CONFIG })){
		die "Environment variable \"PERL_RS_CONFIG\" not properly set.\n";
	}
	my $config_dir = $::ENV{ PERL_RS_CONFIG };
	my $filename = shift;
	local *FH;
	
	my %config_info;
	
	my $filepath = "$config_dir/$filename";
	$filepath =~ s/\/\/+/\//g;
	
	open(FH, $filepath) or die "Cannot open \"$filepath\": $!";
	
	while(<FH>){
		chomp;
		my @r = split;
		if ($#r <= 0){ next; }
		my($varname, $varval) = @r;
		if (length($varname) <= 1 or substr($varname, 1, 1) eq '#'){
			next;
		}
		$varname =~ s/\s//g;
		$varval  =~ s/\s//g;
		$config_info{ $varname } = $varval;
	}
	
	close FH;

	return %config_info;
}

1;

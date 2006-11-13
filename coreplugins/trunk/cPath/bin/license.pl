#!/usr/bin/perl
use strict;
use File::Find;

# Read in Licence File
my $license = '';
open (LF, "< ../config/license.txt") || die "can't read license.txt:  $!";
while (<LF>) {
	$license .= $_;
}
close (LF);

# Recursively Find all Files in src
find (\&process_file, "../src");

# Process All .java Files
sub process_file {
	if (/java$/) { 
		my $curFile = $_;
		my $newFile = "$_.new";
		print ("... File:  $_ -->  $newFile\n");
		open(CUR_SOURCE, "< $_") || die "can't read $_";
		open(NEW_SOURCE, "> $newFile") || die "can't open $_";
		my $flag = 0;
		while (<CUR_SOURCE>) {
			# Strip out any exising license info
			if (/^package/) {
				$flag = 1;
				#  Add New License Info
				print NEW_SOURCE $license;
			}
			if ($flag == 1) {
				print NEW_SOURCE $_;
			}
		}
		close (CUR_SOURCE);
		close (NEW_SOURCE);
		# Rename to original .java file name
		rename ($newFile, $curFile);
	}
}

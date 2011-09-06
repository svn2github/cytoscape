#!/usr/bin/perl

use strict;
use warnings;
use Cwd;

use File::Find;
use File::Copy;

die "USAGE: $0 <property.id> <new.version>\n" if $#ARGV != 1;

my $property_name=$ARGV[0];
my $new_version=$ARGV[1];
my $file_pattern  = "pom.xml";

find(\&processFile, cwd);

sub processFile {

	my $file = $File::Find::name;

	return unless -f $file;
	return unless $file =~ /$file_pattern$/;

	open F, $file or print "couldn't open $file\n" && return;
	open NF, ">$file.new" or print "couldn't open $file.new\n" && return;

	while (<F>) {
		$_ =~ s/<$property_name>.+<\/$property_name>/<$property_name>$new_version<\/$property_name>/g;
		print NF $_;	
	}
	close F;
	close NF;

	move "$file.new", "$file";
}

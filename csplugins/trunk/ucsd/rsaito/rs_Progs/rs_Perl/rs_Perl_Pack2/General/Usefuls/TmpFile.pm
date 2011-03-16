#!/usr/bin/env perl

use strict;
use warnings;

package TmpFile;
use File::Temp qw(tempfile tempdir);

sub new($) {
	my $class = shift;
	my $text  = shift;
	my $mode  = shift;
	
   	my($tmp_fh, $tmp_filename) = tempfile();
	my $obj = {};

	$obj->{ filehandle } = $tmp_fh;	
	$obj->{ filename   } = $tmp_filename;
	
	if(defined($mode)){
		$text =~ s/[ \t]+/\t/g;
	}

	print $tmp_fh $text;
	
	close $tmp_fh;

	bless $obj;
	
}

sub filename($){
	my $obj = shift;
	return $obj->{ filename };
}

sub DESTROY($){
	
	my $obj = shift;
	unlink $obj->filename();

}

unless(caller){
	
	my $fileobj = new TmpFile <<EOF, "single-tab";
This is a                pen.
Hello, everyone!
EOF
	print $fileobj->filename(), "\n";
	
	local(*FH);
	open(FH, $fileobj->filename());
	while(<FH>){
		print $_;	
	}
	close FH;
}
	
1;





#!/usr/bin/env perl

use strict;
use warnings;

package Hash2;

sub new($$) {
	my $class = shift;
    my $val_type = shift;
	my $__h = {};
	
	if(!defined($val_type)){
		$val_type = "S";
	}

	$__h->{ val_type } = $val_type;
	$__h->{ hash     } = {};
	
	bless $__h;

}

sub _set_data($$$){
	my $obj = shift;
	my $key = shift;
	my $val = shift;
	
	$obj->{ hash }->{ $key } = $val;
	
}

sub _push_data($$$){
	my $obj = shift;
	my $key = shift;
	my $val = shift;
	
	push(@{$obj->{ hash }->{ $key }}, $val);	
		
}

sub val($$){
	my $obj = shift;
	my $key = shift;
	return $obj->{ hash }->{ $key };
}

sub get_val_type($){
	my $obj = shift;	
	return $obj->{ val_type };
}

sub read_file($$$){
	my $obj = shift;
	my $filename = shift;
	my $key_cols = shift;
	my $val_cols = shift;	
	
	local(*FH);
	open(FH, $filename) or die "Cannot open \"$filename\": $!";
	while(<FH>){
		chomp;
		my @r = split(/\t/);
		my @key_arr = @r[ @$key_cols ];
		my @val_arr = @r[ @$val_cols ];
		my $key_str = join("\t", @key_arr);
		my $val_str = join("\t", @val_arr);
		$obj->_set_data($key_str, $val_str) if $obj->get_val_type() eq "S";
		$obj->_push_data($key_str, $val_str) if $obj->get_val_type() eq "A";
	}
	close FH;
}

unless(caller){
	
	use General::Usefuls::TmpFile;

	my $fileobj = new TmpFile <<EOF, "single-tab";
11   C   D   Candy   Donuts
10   A   B   Apple   Banana
12   X   Y   XX      XY
EOF

	my $h = new Hash2 "S";
	$h->read_file($fileobj->filename(), [1,2], [0,3]);
	print $h->val("A\tB");
	print "\n";
	print $h->val("C\tD");
	print "\n";

	my $fileobj2 = new TmpFile <<EOF, "single-tab";
11   A   D   Candy   Donuts
10   A   B   Apple   Banana
12   X   Y   XX      XY
EOF

	my $h2 = new Hash2 "A";
	$h2->read_file($fileobj2->filename(), [1], [2,3]);
	print join(",", @{$h2->val("A")});
	print "\n";
	print join(",", @{$h2->val("X")});
	print "\n";
}

1;

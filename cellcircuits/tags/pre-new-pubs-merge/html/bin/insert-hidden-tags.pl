#!/usr/bin/perl -w
#
# Utility script to replace the HIDDEN TAGS field in
# index.html and about_cell_circuits.html
# with the necessary hidden fields
#

if(scalar(@ARGV) != 2) { die "$0: <template file> <output file>\n";}

my $template = shift @ARGV;
my $output = shift @ARGV;

open(T, $template) || die "Can't open $template\n";
open(OUT, ">$output") || die "Can't write to $output\n";

while(<T>)
{
    print OUT $_;

    if(/<!-- HIDDEN TAGS -->/)
    {
	print STDERR "$template: Inserting HIDDEN_TAGS.html\n";
	print OUT `cat HIDDEN_TAGS.html`;
    }
}

close T;

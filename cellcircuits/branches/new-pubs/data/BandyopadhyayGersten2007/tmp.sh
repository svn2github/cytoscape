#!/usr/bin/perl 

for my $i (1..45)
{
    print "svn delete $i.sif\n";
    system "svn delete $i.sif";
}

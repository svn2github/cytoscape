#!/usr/bin/perl -w

opendir(UTIL, "util") || die "Can't open util dir\n";

@files = readdir(UTIL);

my @update;

foreach $f (@files)
{
    if($f =~ /(.*)\.java$/)
    {
	push @update, $1;
    }

}


closedir UTIL;



foreach $u (@update)
{
    print "class $u\n";
    
}

my $regex = join("|", @update);

print $regex . "\n";

while(<>)
{
    if(/^import ([$regex]);/)
    {
	print "found: $1 $_\n";
    }
    
}

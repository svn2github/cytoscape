#!/usr/bin/perl -w

my %types;

while(<>)
{
    /^\S+\s+(\S+)\s+\S/;
    $types{$1} = 1;
}

foreach $key (keys %types)
{ 
    print $key . "\n";
}

#!/usr/bin/perl -w

@lines;

while(<>)
{
    push @lines, $_;;
}

@lines = reverse(@lines);

foreach $l (@lines)
{
    print $l;
}

#!/usr/bin/perl -w

$start=0;

while(<>)
{
     if(/^date/)
     {
         $start=1;
     }
     elsif(! /^====/ && $start)
     {
         print $_;
     }
}

if ($start==0) { exit(1); }

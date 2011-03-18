#!/usr/bin/perl -w

use strict;
local(*FILE);
my $seq = "";

open(FILE, $ARGV[0]) || die "Cannot open \"$ARGV[0]\": $!";
while(<FILE>){
    if($_ =~ /^LOCUS/){ $seq = ""; }
    elsif($_ =~ /^ *[0-9]+ [a-z]/){ 
            $_ =~ s/[^a-z]//g;
            $seq .= $_; # $seq = $seq . $_ ‚ÆˆÓ–¡‚Í“¯‚¶‚¾‚ªA‚æ‚è‘¬‚­ˆ—‚³‚ê‚é
    }
    elsif($_ =~ /^\/\//){
		#‚±‚±‚Íl‚¦‚Ü‚µ‚å‚¤
    }
}
close FILE;


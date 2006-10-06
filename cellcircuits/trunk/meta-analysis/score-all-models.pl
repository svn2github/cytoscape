#!/usr/bin/perl -w

use Publication;
use EdgeMapper;
use GeneNameMapper;


my $dataDir = "/var/www/html/search/data";

my @pubNames = qw(Begley2002_MCR
		  Bernard2005_PSB
		  de_Lichtenberg2005_Science
		  Gandhi2006_NG
		  Hartemink2002_PSB
		  Haugen2004_GB 
		  Ideker2002_BINF
		  Kelley2005_NBT 
		  Sharan2005_PNAS
		  Suthram2005_Nature
		  Yeang2005_GB);

my %pubs;

my $gMap = GeneNameMapper->new();
my $eMap = EdgeMapper->new();

foreach my $p (@pubNames)
{
    $pubs{$p} = Publication->new($p, $dataDir, $gMap, $eMap);

    print "\n### $p\n";
    print $pubs{$p}->print();
}

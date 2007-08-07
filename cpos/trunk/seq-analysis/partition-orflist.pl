#!/usr/bin/perl -w

#
# Script to do sequence analysis that distinguishes between 
#

use Funspec;
use SIF;
use FileUtil;

if(scalar(@ARGV) != 2) { die "$0: <orflist> <node attr file>\n" };
		     

my ($orflistFile, $attrFile) = @ARGV;

my ($cat2orf, $orf2cat) = readORFlist($orflistFile);

my $attrHash = parseNodeAttr($attrFile);

my $partitionedORFlist = partitionORFlist($cat2orf, $attrHash, "__NOATTR__");

my $base = removeSuffix(basename($orflistFile)) . "_" . removeSuffix(basename($attrFile));
my $outfile = $base . ".ORFlist";
open OUT, ">$outfile" || die "Can't open $outfile: $!\n";
writePartitionedORFlist($partitionedORFlist, \*OUT);
close OUT;

my $tupleFile = $base . ".tuple";
open TUPLE, ">$tupleFile" || die "Can't open $outfile: $!\n";
foreach my $key (sort keys %{$partitionedORFlist})
{
    printf TUPLE "%s\n", join(" ", map { $key . "_" . $_ } (sort keys %{$partitionedORFlist->{$key}}));
}
close TUPLE;

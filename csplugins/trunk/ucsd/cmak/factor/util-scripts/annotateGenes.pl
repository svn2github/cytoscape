#!/usr/bin/perl -w
#
# test script for reading all_orfs.noa -- maps ORF to common name
#
#

my $data = "/cellar/users/cmak/data/all_orfs.noa";

open(DATA, $data) || die "Cannot open data file: $data\n";

my %geneMap;

while(<DATA>)
{
    chomp;
    my @fields = split(/\s*=\s*/);
    $geneMap{$fields[0]} = $fields[1];
    $geneMap{$fields[1]} = $fields[0];
}

close DATA;

while(<>)
{
    chomp;
    
    if(defined($geneMap{$_}))
    {
	print "$_ " . $geneMap{$_} . "\n";
    }
    else
    {
	print STDERR "### no mapping for $_, discarding\n";
    }
}

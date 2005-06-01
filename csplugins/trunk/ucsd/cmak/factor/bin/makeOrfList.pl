#!/usr/bin/perl 

my $data = "/cellar/users/cmak/data/orf2name.noa";

open(DATA, $data) || die "Cannot open data file: $data\n";

my %geneMap;

while(<DATA>)
{
    chomp;
    my @fields = split(/\s*=\s*/);
#    $geneMap{$fields[0]} = $fields[1];
    $geneMap{$fields[1]} = $fields[0];
}

close DATA;


while(<>)
{
    chomp;
    my $name = $_;
    my $orf = $geneMap{$name};
    print "\"$orf\", // $name\n";
}

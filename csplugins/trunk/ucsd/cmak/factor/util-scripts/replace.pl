#!/usr/bin/perl -w

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
    
    my @fields = split(/\s/);

    for($x=0; $x <= $#fields; $x++)
    {
	$f = $fields[$x];
	if(defined($geneMap{$f}) && $geneMap{$f} =~ /^Y/i)
	{
	    #print "replacing $fields[$x] with " . $geneMap{$f} . "\n";
	    $fields[$x] = $geneMap{$f};
	}
	else
	{
#	    print STDERR "### no mapping for $f, discarding\n";
	}

    }
    print join(" ", @fields) . "\n";

}

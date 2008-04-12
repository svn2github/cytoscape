#!/usr/bin/perl -w

my %data;

my $sep = "::::";

print STDERR "Making synonym tab from db dump output\n";

while(<>)
{
    chomp;
    my ($sid, $gid, $symbol, $syn) = split(/\t/);

    my $k = join($sep, $sid, $gid, $symbol); 
    if($syn ne $symbol)
    {
	push @{$data{$k}}, $syn;
    }
    else
    {
	if(!exists($data{$k}))
	{
	    $data{$k} = [];
	}
    }
}

foreach my $key (sort keys %data)
{
    print join("\t", split(/$sep/, $key), @{$data{$key}}) . "\n";
}

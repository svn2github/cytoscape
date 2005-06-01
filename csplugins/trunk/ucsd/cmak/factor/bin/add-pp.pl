#!/usr/bin/perl -w

my $debug=2;
my %pp;

open(IN, "/cellar/users/cmak/data/pp.sif") || die "Cannot open PP file\n";

my %edgeId;

my $id=0;

sub makeKey($$)
{
    my $n1 = shift @_;
    my $n2 = shift @_;
    return $n1 . $n2;
}

while(<IN>)
{
    if(/(Y\w+)\s+pp\s+(Y\w+)/)
    {
	my $g1 = $1;
	my $g2 = $2;

	if(!exists $edgeId{makeKey($g1,$g2)} && !exists $edgeId{$g2. $g1})
	{
	    $edgeId{makeKey($g1,$g2)} = $id;
	    $edgeId{makeKey($g2,$g1)} = $id;
	    $id += 1;
	}

	push @{$pp{$g1}}, $g2;
	push @{$pp{$g2}}, $g1;
    }
}

close IN;

if($debug < 2)
{
    for $key ( keys %pp)
    {
	print $key . "=" . "\n";
	foreach $i (@{$pp{$key}})
	{
	    print "   $i " . $edgeId{$key . $i}   ."\n";
	}
    }

    exit;
}

sub edgeExists($$)
{
    my $n1 = shift @_;
    my $n2 = shift @_;

    if(exists $pp{$n1})
    {
	my @targets = @{$pp{$n1}};

	for (@targets)
	{
	    if($_ eq $n2)
	    {
		return 1;
	    }
	}
	
    }

    return 0;
}


my %nodeSet;
my %existingPPEdges;

while(<>)
{
    if(/(Y\w+)\s+pd\s+(Y\w+)/)
    {
	my $tf = $1;
	my $target = $2;

	$nodeSet{$tf} = 1;
	$nodeSet{$target} = 1;
    }

    elsif(/(Y\w+)\s+pp\s+(Y\w+)/)
    {
	my $tf = $1;
	my $target = $2;

	$existingPPEdges{$edgeId{makeKey($tf, $target)}} = 1;
    }


}

my @nodes = keys %nodeSet;



for(my $i=0; $i < scalar(@nodes); $i++)
{
    my $n = $nodes[$i];

    for(my $j=0; $j < scalar(@nodes); $j++)
    {
	if($i != $j)
	{
#	    print STDERR "Checking $nodes[$i] $nodes[$j]\n";
	    
	    if(edgeExists($n, $nodes[$j]))
	    {
		my $id = $edgeId{makeKey($n, $nodes[$j])};
		if(!exists $existingPPEdges{$id})
		{
		    $existingPPEdges{$id} = 1;
		    print("$n pp-nonpath $nodes[$j]\n");
		}
	    }
	}
    }
}

#!/usr/bin/perl -w

my %ko;
my %targets;

my %nodes;

open(MODEL, "yeang-all.sif") || die;

while(<MODEL>)
{
    chomp;
    my @f = split(/\s/);
    
    $nodes{$f[0]} = 1;
    $nodes{$f[2]} = 1;
}

while(<>)
{
    chomp;
    my @f = split(/\s/);
    $ko{$f[0]} = 1;
    $targets{$f[2]} = 1;

#    print "adding $f[0] $f[2]\n";
}

my $Kexist = 0;
my $Texist = 0;

foreach $k (keys %ko)
{
    if(exists $nodes{$k})
    {
	$Kexist += 1;
    }
}

foreach $k (keys %targets)
{
    if(exists $nodes{$k})
    {
	$Texist += 1;
    }
}


print "unqiue ko: " . scalar(keys(%ko)) . " $Kexist are in the IG\n";
print "unqiue targets: " . scalar(keys(%targets)) . " $Texist are in the IG\n";

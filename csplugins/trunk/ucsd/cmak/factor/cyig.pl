#!/usr/bin/perl -w

my $dir = "testOut";
my $run = shift @ARGV;

opendir DIR, $dir || die "cannot open $dir\n";

my @files = grep { /$run-.*\.sif$/ && -f "$dir/$_" } readdir(DIR);

my $cmd = "cytoscape.sh ";

my $base = "$dir/$run";

$cmd .= " -l embedded ";
$cmd .= " -n /cellar/users/cmak/data/orf2name+alias.noa ";
$cmd .= " -n ${base}_type.noa ";
$cmd .= " -j ${base}_dir.eda ";
$cmd .= " -j ${base}_sign.eda ";
$cmd .= " -j ${base}_model.eda ";

foreach $f (@files)
{
    $cmd .= " -i $dir/$f ";
#    print "found $f\n";
}

$cmd .= "&";

print "$cmd\n";

system($cmd);

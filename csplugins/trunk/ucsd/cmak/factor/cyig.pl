#!/usr/bin/perl -w

my $dir = shift @ARGV;
my $run = shift @ARGV;

opendir DIR, $dir || die "cannot open $dir\n";

my @files = grep { /$run-.*\.sif$/ && -f "$dir/$_" } readdir(DIR);

my $cmd = "cytoscape.sh ";

my $base = "$dir/$run";

$cmd .= " -l embedded ";
$cmd .= " -n /cellar/users/cmak/data/orf2name.noa ";
$cmd .= " -n /cellar/users/cmak/data/buffering/wt-mms-response.noa ";
$cmd .= " -n ${base}_type.noa ";
#$cmd .= " -n ${base}_ncount.noa ";
$cmd .= " -j ${base}_dir.eda ";
$cmd .= " -j ${base}_sign.eda ";
$cmd .= " -j ${base}_model.eda ";
$cmd .= " -j ${base}_path.eda ";
$cmd .= " -j /cellar/users/cmak/data/buffering/TFbuffering30nov2004.ea ";
$cmd .= " -j /cellar/users/cmak/data/location/plusMinusMMS.ea ";

foreach $f (@files)
{
    $cmd .= " -i $dir/$f ";
#    print "found $f\n";
}

$cmd .= "&";

print "$cmd\n";

system($cmd);

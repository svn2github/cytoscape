#!/usr/bin/perl -w

my $dir = shift @ARGV;
my $run = shift @ARGV;
my $model = shift @ARGV;


my @files;

my $cmd = "cytoscape.sh ";

my $datadir = "/cellar/users/cmak/data";

my $base = "$dir/$run";

$cmd .= " -l embedded ";
$cmd .= " -n $datadir/orf2name.noa ";
$cmd .= " -n $datadir/isTF.noa ";
$cmd .= " -n $datadir/buffering/wt-mms-response-fisher-0.0001.noa ";

$cmd .= " -n $datadir/metabolic-map/metabolic-path.noa ";
$cmd .= " -n $datadir/cell-cycle/cell-cycle-Jelinsky-Spellman.noa ";
#$cmd .= " -n $datadir/cell-cycle/cell-cycle.noa ";
#$cmd .= " -n $datadir/cell-cycle/Jelinsky-stat-vs-mms.noa ";
#$cmd .= " -n ${base}_ncount.noa ";
$cmd .= " -j $datadir/location/plusMinusMMS-27Feb05-p0.02.cond.eda ";


$cmd .= " -i bb+ppb.sif ";

$cmd .= "&";

print "$cmd\n";

system($cmd);

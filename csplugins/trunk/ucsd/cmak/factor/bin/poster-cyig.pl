#!/usr/bin/perl -w

my $dir = shift @ARGV;
my $run = shift @ARGV;
my $model = shift @ARGV;


my @files;

if($model eq "")
{
    
    opendir DIR, $dir || die "cannot open $dir\n";
    
    @files = grep { /$run.*\.sif$/ && -f "$dir/$_" } readdir(DIR);
    
    closedir DIR;
}
else
{
    @files = "${run}-${model}.sif";
}

my $cmd = "cytoscape.sh ";

my $datadir = "/cellar/users/cmak/data";

my $base = "$dir/$run";

#$cmd .= " -l embedded ";
$cmd .= " -n $datadir/orf2name+go.noa ";
#$cmd .= " -n $datadir/isTF.noa ";
$cmd .= " -n $datadir/buffering/wt-mms-response-fisher-0.0001.noa ";
$cmd .= " -n ${base}_type_aug.noa ";

$cmd .= " -n $datadir/metabolic-map/metabolic-path.noa ";
$cmd .= " -n $datadir/cell-cycle/cell-cycle-Jelinsky-Spellman.noa ";
#$cmd .= " -n $datadir/cell-cycle/cell-cycle.noa ";
#$cmd .= " -n $datadir/cell-cycle/Jelinsky-stat-vs-mms.noa ";
#$cmd .= " -n ${base}_ncount.noa ";
$cmd .= " -j ${base}_dir.eda ";
$cmd .= " -j ${base}_sign.eda ";
$cmd .= " -j ${base}_model.eda ";
$cmd .= " -j ${base}_path.eda ";
#$cmd .= " -j $datadir/buffering/TFbuffering30nov2004.ea ";
$cmd .= " -j $datadir/location/plusMinusMMS.ea ";

foreach $f (@files)
{
    $cmd .= " -i $dir/$f ";
#    print "found $f\n";
}

$cmd .= "&";

print "$cmd\n";

system($cmd);

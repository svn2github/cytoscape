#!/usr/bin/perl -w

my $dir = shift @ARGV;
my $run = shift @ARGV;
my $model = shift @ARGV;


my @files;

if($model eq "")
{
    
    opendir DIR, $dir || die "cannot open $dir\n";
    
    @files = grep { /$run-.*\.sif$/ && -f "$dir/$_" } readdir(DIR);
    
    closedir DIR;
}
else
{
    @files = "${run}-${model}.sif";
}

my $cmd = "cytoscape.sh ";

my $datadir = "/Users/cmak/data-lab";

my $base = "$dir/$run";

$cmd .= " -l embedded ";
$cmd .= " -n $datadir/data/orf2name.noa ";
$cmd .= " -n $datadir/data/isTF.noa ";
$cmd .= " -n $datadir/VERA_error_model_Qspline/EXP_Fisher_adj_p0.0001.na ";
#$cmd .= " -n $datadir/metabolic-map/metabolic-path.noa ";
#$cmd .= " -n $datadir/cell-cycle/cell-cycle-Jelinsky-Spellman.noa ";
#$cmd .= " -n $datadir/cell-cycle/cell-cycle.noa ";
#$cmd .= " -n $datadir/cell-cycle/Jelinsky-stat-vs-mms.noa ";
#$cmd .= " -n ${base}_ncount.noa ";
$cmd .= " -n ${base}_type.noa ";

$cmd .= " -j ${base}_dir.eda ";
$cmd .= " -j ${base}_sign.eda ";
$cmd .= " -j ${base}_model.eda ";
$cmd .= " -j ${base}_path.eda ";
$cmd .= " -j $datadir/data/location/TPM/BOTH_binding_0.226_0.02000048_0.0464_0.004520108_FINAL.tab.cond.ea ";
#$cmd .= " -j $datadir/data/location/TPM/BOTH_binding_0.2002_0.01_0.024_0.00200546_FINAL.tab.cond.ea ";

foreach $f (@files)
{
    $cmd .= " -i $dir/$f ";
#    print "found $f\n";
}

$cmd .= "&";

print "$cmd\n";

system($cmd);

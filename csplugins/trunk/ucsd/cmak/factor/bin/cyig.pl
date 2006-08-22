#!/usr/bin/perl -w

if(scalar(@ARGV) < 1)
{
    die "usage: $0 <output file> [model #]\n";
}

my $in = shift @ARGV;

my @f = split(/\//, $in);
my $run = pop @f;
my $dir = join("/", @f);

my $model = shift @ARGV;

printf("dir=%s, run=%s\n", $dir, $run);

#exit;

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

my $cmd = "/cellar/users/ktan/cytoscape/cytoscape.sh ";

my $datadir = "/cellar/users/cmak/data";

my $base = "$dir/$run";

$cmd .= " -l embedded ";
$cmd .= " -n $datadir/orf2name.noa ";
$cmd .= " -n $datadir/isTF.noa ";

## Node attribute file with "wild-type" expression logratios
#$cmd .= " -n $datadir/buffering/EXP_Fisher_p0.005.na ";
$cmd .= " -n /cellar/users/ktan/ThreeDrugs/Logratios_Pvals/New/MMS_WT.fisherCombined.p0.005.na";
#$cmd .= " -n $datadir/buffering/EXP_Fisher.na ";

$cmd .= " -j $datadir/location/TPM/BOTH_binding_0.1436_0.0010032486_0.0026_0.0001440665_FINAL.tab.cond.ea ";
$cmd .= " -j $datadir/location/TPM/BOTH_binding_0.1436_0.0010032486_0.0026_0.0001440665_FINAL.tab.color.ea ";

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


foreach $f (@files)
{
    $cmd .= " -i $dir/$f ";
#    print "found $f\n";
}

$cmd .= "&";

print "$cmd\n";

system($cmd);

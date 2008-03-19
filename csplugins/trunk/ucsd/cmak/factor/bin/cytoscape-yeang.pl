#!/usr/bin/perl -w
#
#
# Perl script to open all submodels in Cytoscape
#
# Note: command line arg format is for Cytoscape v2.1
#
# Later versions of Cytoscape use different command line switches
#
#

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

my $cmd = "/cellar/users/cmak/cytoscape-v2.1/cytoscape.sh ";

#my $datadir = "/cellar/users/cmak/data";
#$cmd .= " -l embedded ";
#$cmd .= " -n $datadir/orf2name.noa "; # maps ORF to gene name
#$cmd .= " -n $datadir/isTF.noa ";     # identifies TFs

my $base = "$dir/$run";
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

$cmd .= " &";

print "$cmd\n";

system($cmd);

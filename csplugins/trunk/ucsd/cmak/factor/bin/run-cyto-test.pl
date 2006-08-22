#!/usr/bin/perl -w

if(scalar(@ARGV) < 1)
{
    die "usage: $0 <output run label (see genprops.sh file)> [model numbers (optiona)]\n";
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

my $cmd = "cytoscape.sh ";

my $base = "$dir/$run";

$cmd .= " -l embedded ";

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

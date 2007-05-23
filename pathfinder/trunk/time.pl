#!/usr/bin/perl -w

use DirectedGraph;
use PPAwareGraph;

use SearchAlgorithm;
use DFS;
use DFSPathSearch;

use PathStateMachine;
use DepthLimitedPath;
use TemporalPath;
use MultiTemporalPath;

use TimeData;
use QpcrCopyNumberTimeData;
use QpcrCopyNumberMultiTimeData;
use MinProfileAnalyzer;
use Max2ProfileAnalyzer;
use AbsMaxProfileAnalyzer;

use PathFinder;

# Parse command line args
my @newArg;

my $EXPR_FORMAT = 0;
my $THRESH = "";

{local $arg;
 while (@ARGV)
 {
     $arg = shift;
     if($arg =~ /^-thresh$/) { $THRESH = shift @ARGV }
     elsif($arg =~ /^-e$/) { $EXPR_FORMAT = shift @ARGV }
     else { push @newArg, $arg }
 }
}

printf "### ARGS: \n   %s\n", join("\n   ", map {sprintf "[%s]", $_} @newArg);

if(scalar(@newArg != 3))
{
    die "$0: [-thresh <threshold>|min] -e [lrpv|qpcr|mqpcr] <sif> <timeseries lrpv> <output name>\n";
}

my $DEBUG = 0;
    
my ($network, $exprFile, $outName) = @newArg;

my $graph = PPAwareGraph->new($network);
$graph->print() if $DEBUG;

my $exprData;
if($EXPR_FORMAT eq "lrpv")
{
    $exprData = TimeData->new($exprFile);
}
elsif($EXPR_FORMAT eq "qpcr")
{
    $exprData = QpcrCopyNumberTimeData->new($exprFile);
}
elsif($EXPR_FORMAT eq "mqpcr")
{
    $exprData = QpcrCopyNumberMultiTimeData->new($exprFile);
}
else
{
    die "Unknown expression format: $EXPR_FORMAT. Use \"lrpv\" or \"qpcr\"\n";
}

printf("Read %d cols for %d genes\n", 
       scalar(@{$exprData->columnNames()}), 
       scalar(@{$exprData->ids()}) );

my ($tme, $psm);

if($THRESH =~ /^min=(.*)/)
{
    print STDERR "### TME using MinProfileAnalyzer\n";
    my $file = $1;

    $tme = $exprData->getAllTME(createDispatchHash($file,
						   MinProfileAnalyzer->new(),
						   AbsMaxProfileAnalyzer->new()),
				$outName . "-tme.na",
				$outName . "-ratio.na");
    $psm = MultiTemporalPath->new(1, $tme);
}
elsif($THRESH =~ /^max2=(.*)/)
{
    print STDERR "### TME using Max2ProfileAnalyzer\n";
    my $file = $1;

    $tme = $exprData->getAllTME(createDispatchHash($file, 
						   Max2ProfileAnalyzer->new([0..4],[5..9]),
						   AbsMaxProfileAnalyzer->new()),
				$outName . "-tme.na",
				$outName . "-ratio.na");
    $psm = MultiTemporalPath->new(1, $tme);
}
else
{
    print STDERR "### TME using threshold: $THRESH\n";
    $tme = $exprData->getAllTME($THRESH, 
			       $outName . "-tme.na",
			       $outName . "-ratio.na");
    $psm = TemporalPath->new(1, $tme);
}

if($DEBUG)
{
    foreach my $g (sort keys %{$tme})
    {
	print("$g $tme->{$g}\n");
    }
}

#PathStateMachine->DEBUG($PathStateMachine::TPAT);
PathStateMachine->DEBUG($PathStateMachine::NONE);

$psm->allowReuse(1);

$pf = PathFinder->new($graph, DFSPathSearch->new($graph, $psm));

$pf->runSearch();

my @edges = @{$psm->savedEdges()};

open(OUT, ">${outName}.sif") || die "Can't open $outName.sif\n";
map {print OUT $_ . "\n"} @edges;
close OUT;

sub createDispatchHash
{
    my ($file, $profileAnalyzer, $default) = @_;
    my %hash;
    my $set = readSet($file);
    map { $hash{$_} = $profileAnalyzer } keys %{$set};
    $hash{"__DEFAULT__"} = $default;
    return \%hash;
}

sub readSet
{
    my ($file) = @_;

    open(IN, $file) || die "Can't open $file: $!\n";
    my %set;
    my @data = <IN>;
    map { chomp; $set{$_}++ } @data;
    close IN;

    return \%set;
}

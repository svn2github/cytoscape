#!/usr/bin/perl -w

use DirectedGraph;
use PPAwareGraph;

use SearchAlgorithm;
use DFS;
use DFSPathSearch;

use PathStateMachine;
use DepthLimitedPath;
use TemporalPath;

use TimeData;
use QpcrCopyNumberTimeData;

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
    die "$0: [-thresh <threshold>] -e [lrpv|qpcr] <sif> <timeseries lrpv> <output name>\n";
}

my $DEBUG = 1;
    
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
else
{
    die "Unknown expression format: $EXPR_FORMAT. Use \"lrpv\" or \"qpcr\"\n";
}

printf("Read %d cols for %d genes\n", 
       scalar(@{$exprData->columnNames()}), 
       scalar(@{$exprData->ids()}) );


my $tme = $exprData->getAllTME($THRESH, 
			       $outName . "-tme.na",
			       $outName . "-ratio.na");

if($DEBUG)
{
    foreach my $g (sort keys %{$tme})
    {
	print("$g $tme->{$g}\n");
    }
}

my $psm = TemporalPath->new(1, $tme);
PathStateMachine->DEBUG($PathStateMachine::TPAT
			);

$psm->allowReuse(1);

$pf = PathFinder->new($graph, DFSPathSearch->new($graph, $psm));

$pf->runSearch();

my @edges = @{$psm->savedEdges()};

open(OUT, ">${outName}.sif") || die "Can't open $outName.sif\n";
map {print OUT $_ . "\n"} @edges;
close OUT;

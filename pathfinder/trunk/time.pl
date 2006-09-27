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

use PathFinder;

die "?0: <sif> <timeseries lrpv> <output name>\n" if(scalar(@ARGV != 3));

my $DEBUG = 0;
    
my ($network, $lrpv, $outName) = @ARGV;

my $graph = DirectedGraph->new($network);
$graph->print() if $DEBUG;


my $exprData = TimeData->new($lrpv);

printf("Read %d cols for %d genes\n", 
       scalar(@{$exprData->columnNames()}), 
       scalar(@{$exprData->ids()}) );


my $tme = $exprData->getAllTME(0.05, 
			       $outName . "-tme.na",
			       $outName . "-ratio.na");

if($DEBUG)
{
    while( ($g, $t)  = each %{$tme})
    {
	print("$g $t\n");
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

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

die "?0: <sif> <timeseries lrpv>\n" if(scalar(@ARGV != 2));

my $DEBUG = 0;
    
my ($network, $lrpv) = @ARGV;

my $graph = DirectedGraph->new($network);
$graph->print() if $DEBUG;


my $exprData = TimeData->new($lrpv);

printf("Read %d cols for %d genes\n", 
       scalar(@{$exprData->columnNames()}), 
       scalar(@{$exprData->ids()}) );


my $tme = $exprData->getAllTME(0.05);

if($DEBUG)
{
    while( ($g, $t)  = each %{$tme})
    {
	print("$g $t\n");
    }
}

my $psm = TemporalPath->new(1, $tme);
PathStateMachine->DEBUG($PathStateMachine::TPAT | 
			$PathStateMachine::INSP 
			);

$psm->allowReuse(1);

$pf = PathFinder->new($graph, DFSPathSearch->new($graph, $psm));

$pf->runSearch();


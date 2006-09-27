#!/usr/bin/perl -w

use DirectedGraph;
use PPAwareGraph;

use SearchAlgorithm;
use DFS;
use DFSPathSearch;

use PathStateMachine;
use DepthLimitedPath;

use PathFinder;

my $g = DirectedGraph->new("test.sif");
$g->print();

#my $pf = PathFinder->new($g, SearchAlgorithm->new($g));
#$pf->runSearch();

#$pf = PathFinder->new($g, DFS->new($g));
#$pf->runSearch();

#print "\n### Start PP aware\n\n";

#my $ppg = PPAwareGraph->new("test.sif");
#$ppg->print();

my $psm = DepthLimitedPath->new(2);
PathStateMachine->DEBUG(#$PathStateMachine::TPAT | 
			$PathStateMachine::DISC | 
			$PathStateMachine::FINI  
			#$PathStateMachine::INSP | 
			#$PathStateMachine::TSEA
			);

$psm->allowReuse(1);

$pf = PathFinder->new($g, DFSPathSearch->new($g, $psm));

#$pf->runSearch();


#print "### Searching only a and b\n";
$pf->runSearch();


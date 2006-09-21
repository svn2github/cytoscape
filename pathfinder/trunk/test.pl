#!/usr/bin/perl -w

use DirectedGraph;
use PPAwareGraph;
use SearchAlgorithm;
use DFS;
use DFSPathSearch;
use PathStateMachine;
use PathFinder;

my $g = DirectedGraph->new("test.sif");
$g->print();

my $pf = PathFinder->new($g, SearchAlgorithm->new($g));
$pf->runSearch();

$pf = PathFinder->new($g, DFS->new($g));
$pf->runSearch();

print "\n### Start PP aware\n\n";

my $ppg = PPAwareGraph->new("test.sif");
$ppg->print();

$pf = PathFinder->new($g, DFSPathSearch->new($g, PathStateMachine->new()));
#$pf->runSearch();


print "### Searching only a and b\n";
$pf->runSearch(qw(a b));


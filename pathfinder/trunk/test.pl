#!/usr/bin/perl -w

use DirectedGraph;
use PPAwareGraph;
use SearchAlgorithm;
use DFS;
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

$pf = PathFinder->new($ppg, DFS->new($ppg));
$pf->runSearch();


print "### Searching only a and b\n";
$pf->runSearch(qw(a b));


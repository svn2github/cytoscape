#!/usr/bin/perl -w

use DirectedGraph;
use PPAwareGraph;

use ConnectedComponents;

# Parse command line args
my @newArg;

my $DEBUG = 0;

{local $arg;
 while (@ARGV)
 {
     $arg = shift;
     if($arg =~ /^-v$/) { $DEBUG = 1 }
     else { push @newArg, $arg }
 }
}

printf "### ARGS: \n   %s\n", join("\n   ", map {sprintf "[%s]", $_} @newArg);

if(scalar(@newArg != 1))
{
    die "$0: <sif>\n";
}


    
my ($network, $outName) = @newArg;

my $graph = PPAwareGraph->new($network);
$graph->print() if $DEBUG;

my $cc = ConnectedComponents->new($graph);
my $components = $cc->search();

foreach my $id (sort {$a <=> $b} keys %{$components})
{
    printf "Component $id = (%s)\n", join(",", @{$components->{$id}});
}

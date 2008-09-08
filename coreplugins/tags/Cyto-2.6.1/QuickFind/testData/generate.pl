#!/usr/bin/perl

die "USAGE: $0 <num nodes> <num edges>\n"
if ( $ARGV[0] !~ /^\d+$/ || $ARGV[1] !~ /^\d+$/ );

$numNodes = shift;
$numEdges = shift;

while ( $numEdges-- > 0 ) {
	$a = int(rand($numNodes));
       	$b = int(rand($numNodes));
        print "$a pp $b\n";
}

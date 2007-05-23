package DFS;

use SearchAlgorithm;

@ISA = qw(SearchAlgorithm);

## 
## Constructor
##

sub new
{
    my ($caller, $graph) = @_;
    my $self = $caller->SUPER::new($graph);
    $self->{'WHITE'} = 0;
    $self->{'GREY'} = 1;
    $self->{'BLACK'} = 2;
    return $self;
}

## 
## Instance methods
##

#
# Main subroutine for Depth First Search
#
sub search
{
    my ($self, $startNode) = @_;

    my $visitor = sub {
	my ($msg, $node, $depth, $time) = @_;
	printf("%s %s [depth=%d], [t=%d]\n", $msg, $node, $depth, $time);
    };

    if($self->graph()->containsNode($startNode))
    {
	print "DFS: start at: $startNode\n";

	my (%color, %ft, %dt);
	foreach $n ($self->graph()->nodes())
	{
	    $color{$n} = $self->{'WHITE'};
	    $ft{$n} = 0;
	    $dt{$n} = 0;
	}
	
	my $time = 0;
	$self->dfsVisit($startNode, 0, \$time, \%color, \%dt, \%ft, $visitor);

	print "DFS: done\n";
    }
    else
    {
	print "DFS: node $startNode does not exist in graph\n";
    }
}


#
# Depth First Search helper routine
#
sub dfsVisit
{
    my ($self, $node, $depth, $time, $color, $dt, $ft, $visitor) = @_;

    $visitor->("discovery", $node, $depth, $$time);

    $color->{$node} = $self->{'GREY'};
    $dt->{$node} = $$time; # store the discovery time
    $$time += 1;

    my @neighbors = $self->graph()->getNeighbors($node);
    foreach $n (@neighbors)
    {
        if($color->{$n} != $self->{'GREY'})
        {
            $self->dfsVisit($n, $depth + 1, $time, $color, $dt, $ft, $visitor);
        }
    }
    
    $visitor->("finish", $node, $depth, $$time);
    $ft->{$node} = $$time; # store the finish time
}

1;

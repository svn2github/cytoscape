package DFSPathSearch;

use SearchAlgorithm;

@ISA = qw(SearchAlgorithm);

my $WHITE = 0;
my $GREY = 1;
my $BLACK = 2;

sub new
{
    my ($caller, $graph, $pathStateMachine) = @_;
    
    my $self = $caller->SUPER::new($graph);
    $self->psm($pathStateMachine);
    return $self;
}

SearchAlgorithm::_generateAccessors(qw(psm));

#
# Main subroutine for Depth First Search for paths
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
	    $color{$n} = $WHITE;
	    $ft{$n} = 0;
	    $dt{$n} = 0;
	}
	
	my $time = 0;
	if($self->psm()->inspectNeighbor($startNode))
	{
	    $self->psm()->startPath($startNode);
	    $self->psm()->pushPath($startNode);
	    $self->dfsVisit($startNode, 0, \$time, \%color, \%dt, \%ft);
	}

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
    my ($self, $node, $depth, $time, $color, $dt, $ft) = @_;

    my $psm = $self->psm();

    $psm->discoverNode($node, $depth, $$time);

    $color->{$node} = $GREY; # GREY means node is on current path
    $dt->{$node} = $$time; # store the discovery time
    $$time += 1;

    if($psm->terminatesPath($node, $depth))
    {
	$psm->endPath($node);
    }

    if(! $psm->terminatesSearch($node, $depth))
    {
	my @neighbors = $self->graph()->getNeighbors($node);
	foreach $n (@neighbors)
	{
	    if($color->{$n} == $WHITE && $psm->inspectNeighbor($n))
	    {
		$psm->pushPath($n);
		$self->dfsVisit($n, $depth + 1, $time, $color, $dt, $ft);
		$psm->popPath($n);
	    }
	}
    }
    
    if($psm->allowReuse())
    {
	# change color to WHITE so this node can be an 
	# intermediate vertex in other paths
	$color->{$node} = $WHITE;  
    }
    else
    {
	$color->{$node} = $BLACK;  
    }
    $psm->finishNode($node, $depth, $$time);
    
    $ft->{$node} = $$time; # store the finish time
}

1;

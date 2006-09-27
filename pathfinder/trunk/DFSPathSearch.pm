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

DFSPathSearch->_generateAccessors(qw(psm));

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
	#print "DFS: start at: $startNode\n";

	my (%color);
	foreach $n ($self->graph()->nodes())
	{
	    $color{$n} = $WHITE;
	}
	
	$self->psm()->resetTime();
	if($self->psm()->inspectStartNode($startNode))
	{
	    $self->psm()->startPath($startNode);
	    $self->dfsVisit($startNode, 0, \%color);
	}

	#print "DFS: done\n";
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
    my ($self, $node, $depth, $color) = @_;

    my $psm = $self->psm();

    $psm->discoverNode($node, $depth);

    $color->{$node} = $GREY; # GREY means node is on current path

    if($psm->terminatesPath($node, $depth))
    {
	$psm->endPath($node);
    }

    if(! $psm->terminatesSearch($node, $depth))
    {
	my @neighbors = $self->graph()->getNeighbors($node);
	foreach $n (@neighbors)
	{
	    my $edges = $self->graph()->getEdgeTypesBetween($node, $n);
	    foreach my $e (@{$edges})
	    {
		if($color->{$n} == $WHITE && $psm->inspectNeighbor($n, $e))
		{
		    $psm->pushPath($n, $e);
		    $self->dfsVisit($n, $depth + 1, $color);
		    $psm->popPath();
		}
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
    $psm->finishNode($node, $depth);
}

1;

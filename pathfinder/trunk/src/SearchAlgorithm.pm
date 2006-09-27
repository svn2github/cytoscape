package SearchAlgorithm;

use Object;

@ISA = qw(Object);

SearchAlgorithm->_generateAccessors(qw(graph));

## 
## Constructor
##

sub new
{
    my ($caller, $graph) = @_;
    my $self = $caller->SUPER::new();

    $self->graph($graph);
    return $self;
}

## 
## Instance methods
##

sub search
{
    my ($self, $startNode) = @_;
    
    if($self->graph()->containsNode($startNode))
    {
	print "Default search at: $startNode\n";
	print "Done\n";
    }
    else
    {
	print "Node $startNode does not exist in graph\n";
    }
    return;
}

sub getResults
{
    my ($self) = @_;
    return 1;
}

1;

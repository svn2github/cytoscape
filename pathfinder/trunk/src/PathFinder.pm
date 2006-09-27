package PathFinder;

use Object;
use DirectedGraph;
use SearchAlgorithm;

@ISA = qw(Object);

PathFinder->_generateAccessors(qw(graph searchAlgorithm));

## 
## Constructor
##

sub new
{
    my ($caller, $graph, $searchAlgorithm) = @_;
    my $self = $caller->SUPER::new($graph);
    
    $self->graph($graph);
    $self->searchAlgorithm($searchAlgorithm);
    return $self;
}

## 
## Instance methods
##

sub runSearch
{
    my $self = shift;
    
    my $alg = $self->searchAlgorithm();
    my @startNodes;
    if(@_)
    {
	@startNodes = @_;
    }
    else
    {
	@startNodes = $self->graph()->nodes();
    }
    
    foreach my $node (@startNodes)
    {
	$alg->search($node);
    }
}

1;

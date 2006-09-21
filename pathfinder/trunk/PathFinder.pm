package PathFinder;

use DirectedGraph;
use SearchAlgorithm;

sub new
{
    my ($caller, $graph, $searchAlgorithm) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);

    $self->graph($graph);
    $self->searchAlgorithm($searchAlgorithm);
    return $self;
}

# generate accessor methods
for my $field (qw(graph searchAlgorithm))
{
    my $slot = __PACKAGE__ . "::$field";
    no strict "refs";
    *$field = sub {
	my $self = shift;
	$self->{$slot} = shift if @_;
	return $self->{$slot};
    }
}

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

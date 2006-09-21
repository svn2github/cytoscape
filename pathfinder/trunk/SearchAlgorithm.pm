package SearchAlgorithm;

## 
## Constructor
##

sub new
{
    my ($caller, $graph) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);

    $self->graph($graph);
    return $self;
}

sub _generateAccessors
{
    my @fields = @_;
# generate accessor methods
    for my $field (@fields)
    {
	my $slot = __PACKAGE__ . "::$field";
	no strict "refs";
	*$field = sub {
	    my $self = shift;
	    $self->{$slot} = shift if @_;
	    return $self->{$slot};
	}
    }
}

_generateAccessors(qw(graph));

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

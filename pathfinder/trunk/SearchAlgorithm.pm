package SearchAlgorithm;

sub new
{
    my ($caller, $graph) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);

    $self->graph($graph);
    return $self;
}


# generate accessor methods
for my $field (qw(graph))
{
    my $slot = __PACKAGE__ . "::$field";
    no strict "refs";
    *$field = sub {
	my $self = shift;
	$self->{$slot} = shift if @_;
	return $self->{$slot};
    }
}


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

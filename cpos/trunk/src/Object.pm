package Object;

sub new
{
    my ($caller) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);
    
    return $self;
}


sub _generateAccessors
{
    my ($caller, @fields) = @_;
    my $class = ref($caller) || $caller;

    # generate accessor methods
    for my $field (@fields)
    {
	my $slot = $field;
	my $field = $class . "::" . $field;
	no strict "refs";
	*$field = sub {
	    my $self = shift;
	    $self->{$slot} = shift if @_;
	    return $self->{$slot};
	}
    }
}


sub _generateReadOnlyAccessors
{
    my ($caller, @fields) = @_;
    my $class = ref($caller) || $caller;

    # generate accessor methods
    for my $field (@fields)
    {
	my $slot = $field;
	my $field = $class . "::" . $field;
	no strict "refs";
	*$field = sub {
	    my $self = shift;
	    return $self->{$slot};
	}
    }
}

1;


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


sub _generateReadOnlyAccessors
{
    my @fields = @_;
    # generate accessor methods
    for my $field (@fields)
    {
	my $slot = __PACKAGE__ . "::$field";
	no strict "refs";
	*$field = sub {
	    my $self = shift;
	    return $self->{$slot};
	}
    }
}

1;


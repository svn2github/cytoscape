package DirectedGraph;

my $DEBUG = 1;

## 
## Constructor
##

sub new
{
    my ($caller, $sif) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);

    $self->_nodeHash({});
    $self->alist({});

    if($sif) {$self->populateFromSIF($sif)};
    
    return $self;
}

# generate accessor methods (get only, do not allow set) 
for my $field (qw(alist _nodeHash))
{
    my $slot = __PACKAGE__ . "::$field";
    no strict "refs";
    *$field = sub {
	my $self = shift;
	$self->{$slot} = shift if @_;
	return $self->{$slot};
    }
}

## 
## Instance methods
##

sub nodes
{
    my $self = shift;
    return keys %{$self->_nodeHash()};
	
}

sub containsNode
{
    my ($self, $node) = @_;
    return exists($self->_nodeHash()->{$node});
}

# return: an array of edge types between n1 and n2
sub getEdgeTypesBetween
{
    my ($self, $n1, $n2) = @_;
    
    return ( $self->alist()->{$n1}{$n2} );
}

#
# WARNING: Ignore edge types for now
#
sub getNeighbors
{
    my ($self, $node) = @_;
    
    return(keys %{$self->alist()->{$node}} );
}

sub populateFromSIF
{
    my ($self, $file) = @_;

    open (IN, $file) || die "populateFromSIF: can't open $file\n";

    my $alist = $self->alist();

    my ($n1, $n2, $edge);
    my $Ne = 0;
    while(<IN>)
    {
        if(/^(\S+)\s+(\S+)\s+(\S+)/)
        {
            $n1 = $1;
            $edge = $2;
            $n2 = $3;

	    $self->_nodeHash()->{$n1}++;
	    $self->_nodeHash()->{$n2}++;

            push @{$self->alist()->{$n1}{$n2}}, $edge;
	    $Ne++;
        }
        elsif(/^(\S+)/)
        {
	    $self->_nodeHash()->{$1}++;
	}
    }

    printf STDERR ("Created graph from '$file'. [N]=%d, [E]=%d\n", 
		   scalar($self->nodes()), 
		   $Ne) if $DEBUG;
}


sub print
{
    my $self = shift;

    my %alist = %{$self->alist()};

    foreach $n1 (keys %alist)
    {
	foreach $n2 (keys %{$alist{$n1}})
	{
	    foreach $edgeType (@{$alist{$n1}{$n2}})
	    {
		printf ("%s %s %s\n", $n1, $edgeType, $n2);
	    }
	}
    }
}

1;

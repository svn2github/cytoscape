package DirectedGraph;

sub new
{
    my ($caller, $sif) = @_;
    my $class = ref($caller) || $caller;
    my $self = { 
	adjlist => {}   # adjacent list
    };
    bless($self, $class);

    if($sif) {$self->populateFromSIF($sif)};
    
    return $self;
}

sub adjlist
{
    my ($self) = @_;
    return $self->{adjlist};
}

sub nodes
{
    my ($self) = @_;
    return keys %{$self->adjlist};
}

sub populateFromSIF
{
    my ($self, $file) = @_;

    open (IN, $file) || die "populateFromSIF: can't open $file\n";

    my ($n1, $n2, $edge);
    while(<IN>)
    {
        if(/^(\S+)\s+(\S+)\s+(\S+)/)
        {
            $n1 = $1;
            $edge = $2;
            $n2 = $3;

            push @{$self->adjlist->{$n1}{$n2}}, $edge;
        }
        elsif(/^(\S+)/)
        {
            if( !exists($self->adjlist->{$1}) )
            {
		$self->adjlist->{$1} = [];
            }
        }
    }
}


sub print
{
    my $self = shift;

    my %adjlist = %{$self->adjlist};

    foreach $n1 (keys %adjlist)
    {
	foreach $n2 (keys %{$adjlist{$n1}})
	{
	    foreach $edgeType (@{$adjlist{$n1}{$n2}})
	    {
		printf ("%s %s %s\n", $n1, $edgeType, $n2);
	    }
	}
    }
}

1;

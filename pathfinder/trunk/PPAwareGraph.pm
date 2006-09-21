package PPAwareGraph;

@ISA = qw(DirectedGraph);

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

	    if(uc($edge) eq "PP")
	    {
		push @{$self->alist()->{$n1}{$n2}}, $edge;
		$Ne++;
		push @{$self->alist()->{$n2}{$n1}}, $edge;
		$Ne++;
	    }
	    else
	    {
		push @{$self->alist()->{$n1}{$n2}}, $edge;
		$Ne++;
	    }
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

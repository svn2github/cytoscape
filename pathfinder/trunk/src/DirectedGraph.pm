package DirectedGraph;

use Object;

@ISA = qw(Object);

DirectedGraph->_generateAccessors(qw(alist incidentList _nodeHash));

my $DEBUG = 1;

## 
## Constructor
##
sub new
{
    my ($caller, $sif) = @_;
    my $self = $caller->SUPER::new();

    $self->_nodeHash({});
    $self->alist({});
    $self->incidentList({});

    if($sif) {$self->populateFromSIF($sif)};
    
    return $self;
}

## 
## Instance methods
##

sub nodes
{
    my ($self) = @_;
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

sub edgeExists
{
    my ($self, $source, $target) = @_;

    return exists($self->alist()->{$source}{$target});
}

#
# Get neighbors that can be reach from $node
#
sub getNeighbors
{
    my ($self, $node) = @_;
    
    return(keys %{$self->alist()->{$node}} );
}

#
# Get sources of incident edges on $node
#
sub getIncidentNeighbors
{
    my ($self, $node) = @_;
    
    return(keys %{$self->incidentList()->{$node}} );
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
            push @{$self->incidentList()->{$n2}{$n1}}, $edge;
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
    my ($self) = @_;
    my $nodesPrinted = _printList($self->alist());
    
    foreach my $n ($self->nodes())
    {
	if(!exists($nodesPrinted->{$n}))
	{
	    print "$n\n";
	}
    }
}

sub printIncomingEdges
{
    my ($self) = @_;
    my $nodesPrinted = _printList($self->incidentList());
}


sub _printList
{
    my ($adjacencyList) = @_;

    my %alist = %{$adjacencyList};

    my %nodesPrinted;
    foreach $n1 (keys %alist)
    {
	foreach $n2 (keys %{$alist{$n1}})
	{
	    foreach $edgeType (@{$alist{$n1}{$n2}})
	    {
		printf ("%s %s %s\n", $n1, $edgeType, $n2);
		$nodesPrinted{$n1}++;
		$nodesPrinted{$n2}++;
	    }
	}
    }
    return \%nodesPrinted;
}

1;

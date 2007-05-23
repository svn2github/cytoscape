package MultiTemporalPath;

use TemporalPath;

@ISA = qw(TemporalPath);

#MultiTemporalPath->_generateAccessors(qw());

sub new
{
    my ($caller, $maxDepth, $tmeData, $outputFile) = @_;
    
    my $self = $caller->SUPER::new($maxDepth, $tmeData, $outputFile);

    return $self;
}

sub inspectNeighbor
{
    my ($self, $node) = @_;

    my @path = @{$self->path()};
    my $tme = $self->tmeData();

    my $curEnd = $path[$#path];
 
    my (@nodeTME, @endTME);
    if (exists($tme->{$node}))
    {
	@nodeTME = @{$tme->{$node}};
	@endTME = @{$tme->{$curEnd}};
	
	foreach my $n (@nodeTME)
	{
	    foreach my $e (@endTME)
	    {
		if($n >= $e)
		{
		    printf("  INSP OK: %s[%s] >= %s[%s]\n", 
			   $node, $n,
			   $curEnd, $e)
			if $PathStateMachine::DEBUG & $PathStateMachine::INSP;
		    return 1;
		}
	    }
	}
    }
    return 0;
}

sub inspectStartNode
{
    my ($self, $node) = @_;

    if(exists($self->tmeData()->{$node}))
    {
	printf("  INSP OK: %s[%s] StartNode\n", 
	       $node, 
	       join(",", $self->tmeData()->{$node}))
	    if $PathStateMachine::DEBUG & $PathStateMachine::INSP;
	return 1;
    }
    return 0;
}

1;

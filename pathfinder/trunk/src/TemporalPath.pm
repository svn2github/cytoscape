package TemporalPath;

use PathStateMachine;

@ISA = qw(PathStateMachine);

TemporalPath->_generateAccessors(qw(maxDepth tmeData savedEdges));

sub new
{
    my ($caller, $maxDepth, $tmeData, $outputFile) = @_;
    
    my $self = $caller->SUPER::new();
    $self->maxDepth($maxDepth);
    $self->tmeData($tmeData);
    $self->savedEdges([]);
    return $self;
}

sub inspectNeighbor
{
    my ($self, $node) = @_;

    my @path = @{$self->path()};
    my $tme = $self->tmeData();

    my $curEnd = $path[$#path];
    
    if (exists($tme->{$node}) &&
	$tme->{$node} >= $tme->{$curEnd})
    {
	printf("  INSP OK: %s[%s] >= %s[%s]\n", $node, $tme->{$node},
	       $curEnd, $tme->{$curEnd})
	    if $PathStateMachine::DEBUG & $PathStateMachine::INSP;
	return 1;
    }
    return 0;
}


sub inspectStartNode
{
    my ($self, $node) = @_;

    if(exists($self->tmeData()->{$node}))
    {
	printf("  INSP OK: %s[%s] StartNode\n", $node, $self->tmeData()->{$node})
	    if $PathStateMachine::DEBUG & $PathStateMachine::INSP;
	return 1;
    }
    return 0;
}


sub terminatesPath
{
    my ($self, $node, $depth) = @_;
    
    my $t = ($depth >= 1);
    if($t)
    {
	$self->savePath();
	printf "  TPath: %s terminate=$depth\n", $self->printPath()
	    if $PathStateMachine::DEBUG & $PathStateMachine::TPAT;
    }    
    return($t);
}


sub terminatesSearch
{
    my ($self, $node, $depth) = @_;

    my $t = ($depth >= $self->maxDepth());
    if($t)
    {
	print "  TSearch: $node terminate=$depth\n" 
	    if $PathStateMachine::DEBUG & $PathStateMachine::TSEA;
    }    
    return($t);
}

sub savePath
{
    my ($self) = @_;
    
    my @path = @{$self->path()};
    my @edges = @{$self->edgeTypes()};

    my $savedEdges = $self->savedEdges();

    my $source = $path[0];
    foreach my $x (1..$#path)
    {
	push(@{$savedEdges}, 
	     sprintf("%s %s %s", 
		     $source, 
		     $edges[$x-1],
		     $path[$x]));
    }
}

1;

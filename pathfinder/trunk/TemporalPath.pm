package TemporalPath;

use PathStateMachine;

@ISA = qw(PathStateMachine);

TemporalPath->_generateAccessors(qw(path maxDepth tmeData));

sub new
{
    my ($caller, $maxDepth, $tmeData) = @_;
    
    my $self = $caller->SUPER::new();
    $self->maxDepth($maxDepth);
    $self->path([]);
    $self->tmeData($tmeData);
    return $self;
}


sub startPath
{
    my ($self, $node, @extra) = @_;
    $self->path([]);
    
    printf "  Star: at $node path=%s\n", $self->printPath() 
	if $PathStateMachine::DEBUG & $PathStateMachine::STAR;
}


sub pushPath
{
    my ($self, $node, @extra) = @_;
    push @{$self->path()}, $node;
    print "  Push: $node\n" 
	if $PathStateMachine::DEBUG & $PathStateMachine::PUSH;
}


sub popPath
{
    my ($self, $node, @extra) = @_;
    pop @{$self->path()};
    print "  Pop: $node\n" 
	if $PathStateMachine::DEBUG & $PathStateMachine::POP;
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
	printf "  TPath: %s terminate=$depth\n", join(".", @{$self->path()})
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

sub printPath
{
    my ($self) = @_;

    my $str = "";
    foreach my $p (@{$self->path()})
    {
	
	$str .= $p . ".";
    }
    return $str;
}
1;

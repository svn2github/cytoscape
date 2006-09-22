package DepthLimitedPath;

use PathStateMachine;
use Object;

@ISA = qw(PathStateMachine);

DepthLimitedPath->_generateAccessors(qw(path maxDepth));

sub new
{
    my ($caller, $maxDepth) = @_;
    
    my $self = $caller->SUPER::new();
    $self->maxDepth($maxDepth);
    $self->path([]);
    return $self;
}


sub startPath
{
    my ($self, $node, @extra) = @_;
    $self->path([]);
    print "  Star: at $node\n" 
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


1;

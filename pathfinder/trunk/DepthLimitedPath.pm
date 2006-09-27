package DepthLimitedPath;

use PathStateMachine;
use Object;

@ISA = qw(PathStateMachine);

DepthLimitedPath->_generateAccessors(qw(maxDepth));

sub new
{
    my ($caller, $maxDepth) = @_;
    
    my $self = $caller->SUPER::new();
    $self->maxDepth($maxDepth);
    return $self;
}

sub terminatesPath
{
    my ($self, $node, $depth) = @_;
    
    my $t = ($depth >= 1);
    if($t)
    {
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


1;

package PathStateMachine;

use Object;

@ISA = qw(Object);

## Create DEBUG variables
my $c=0;
foreach $event (qw(DISC FINI STAR ENDP INSP PUSH POP TPAT TSEA))
{
    my $str = "\$$event = 1 << $c";
    eval($str);
    #print "$str: code = " . (1 << $c) . "\n";
    $c++;
}

$DEBUG = $DISC | $FINI | $STAR | $ENDP | $INSP | $PUSH | $POP | $TPAT | $TSEA;
$NONE = 0;

sub DEBUG
{
    my $self = shift;
    $DEBUG = shift if @_;
    return $DEBUG;
}

## 
## Constructor
##

sub new
{
    my ($caller) = @_;
    my $self = $caller->SUPER::new();
    
    $self->{time} = 0;
    $self->ft({});    
    $self->dt({});
    $self->path([]);
    $self->edgeTypes([]);
    $self->allowReuse(0);
    return $self;
}


PathStateMachine->_generateAccessors(qw(allowReuse path edgeTypes dt ft));


## 
## Instance methods
##

sub resetTime
{
    my ($self) = @_;
    $self->{time} = 0;
    $self->dt({});
    $self->ft({});
}

sub discoverNode
{
    my ($self, $node, $depth) = @_;
    printf("  Disc: $node d=%d t=%d\n", $depth, $self->{time})
	if $DEBUG & $DISC;

    $self->dt()->{$node} = $self->{time}; # store the discovery time
    $self->{time} += 1;

}

sub finishNode
{
    my ($self, $node, $depth) = @_;
    printf("  Fini: $node d=%d t=%d\n", $depth, $self->{time})
	if $DEBUG & $FINI;

    $self->ft()->{$node} = $self->{time}; # store the finish time
}


sub startPath
{
    my ($self, $node, @extra) = @_;
    $self->path([$node]);
    
    printf "  Star: at $node path=%s\n", $self->printPath() 
	if $DEBUG & $STAR;
}

sub pushPath
{
    my ($self, $node, $edge, @extra) = @_;
    push @{$self->path()}, $node;
    push @{$self->edgeTypes()}, $edge;
    print "  Push: $node $edge\n" if $DEBUG & $PUSH;
}


sub popPath
{
    my ($self, $node, @extra) = @_;
    pop @{$self->path()};
    pop @{$self->edgeTypes()};
    print "  Pop\n" if $DEBUG & $POP;
}


sub endPath
{
    my ($self, $node, @extra) = @_;
    print "  Endp: at $node\n" if $DEBUG & $ENDP;
}


sub inspectStartNode
{
    my ($self, $node) = @_;

    print "  Insp: $node start\n" if $DEBUG & $INSP;
    return 1;
}

sub inspectNeighbor
{
    my ($self, $node, $edge, @extra) = @_;
    my $ok = 1;
    print "  Insp: $node $edge ok=$ok\n" if $DEBUG & $INSP;

    return $ok;
}


sub terminatesPath
{
    my ($self, $node, $depth) = @_;
    my $terminates = 0;
    if($terminates)
    {
	print "  TPath: $node terminate=$terminates\n" if $DEBUG & $TPAT;
    }

    return $terminates;
}


sub terminatesSearch
{
    my ($self, $node, $depth) = @_;
    my $terminates = 0;
    if($terminates)
    {
	print "  TSearch: $node terminate=$terminates\n" if $DEBUG & $TSEA;
    }

    return $terminates;
}

sub printPath
{
    my ($self) = @_;

    my @path = @{$self->path()};
    my @edges = @{$self->edgeTypes()};

    my $str = $path[0];
    foreach my $x (1..$#path)
    {
	
	$str .= ".[" . $path[$x] . "," . $edges[$x-1] . "]";
    }
    return $str;
}

1;

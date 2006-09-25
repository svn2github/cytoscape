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
    
    $self->allowReuse(0);
    return $self;
}


PathStateMachine->_generateAccessors(qw(allowReuse));


## 
## Instance methods
##

sub discoverNode
{
    my ($self, $node, $depth, $time) = @_;
    printf "  Disc: $node d=%d t=%d\n", $depth, $time if $DEBUG & $DISC;
}

sub finishNode
{
    my ($self, $node, $depth, $time) = @_;
    printf "  Fini: $node d=%d t=%d\n", $depth, $time if $DEBUG & $FINI;
}


sub startPath
{
    my ($self, $node, @extra) = @_;
    print "  Star: at $node\n" if $DEBUG & $STAR;
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
    my ($self, $node, @extra) = @_;
    my $ok = 1;
    print "  Insp: $node ok=$ok\n" if $DEBUG & $INSP;

    return $ok;
}

sub pushPath
{
    my ($self, $node, @extra) = @_;
    print "  Push: $node\n" if $DEBUG & $PUSH;
}


sub popPath
{
    my ($self, $node, @extra) = @_;
    print "  Pop: $node\n" if $DEBUG & $POP;
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


1;

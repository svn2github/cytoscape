package PathStateMachine;

## Create DEBUG variables
my $c=0;
foreach $event (qw(DISC FINI STAR ENDP INSP EXTE TERM))
{
    my $str = "\$$event = 1 << $c";
    eval($str);
    #print "$str: code = " . (1 << $c) . "\n";
    $c++;
}

my $DEBUG = $DISC | $FINI | $STAR | $ENDP | $INSP | $EXTE | $TERM;

## 
## Constructor
##

sub new
{
    my ($caller) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);

    return $self;
}

# generate accessor methods
for my $field (qw())
{
    my $slot = __PACKAGE__ . "::$field";
    no strict "refs";
    *$field = sub {
	my $self = shift;
	$self->{$slot} = shift if @_;
	return $self->{$slot};
    }
}


## 
## Instance methods
##

sub discoverNode
{
    my ($self, $node, @extra) = @_;
    printf "DISC: $node %s\n", join(".", @extra) if $DEBUG & $DISC;
}

sub finishNode
{
    my ($self, $node, @extra) = @_;
    printf "FINI: $node %s\n", join(".", @extra) if $DEBUG & $FINI;
}


sub startPath
{
    my ($self, $node, @extra) = @_;
    print "STAR: at $node\n" if $DEBUG & $STAR;
}


sub endPath
{
    my ($self, $node, @extra) = @_;
    print "ENDP: at $node\n" if $DEBUG & $ENDP;
}


sub inspectNeighbor
{
    my ($self, $node, @extra) = @_;
    my $ok = 1;
    print "INSP: $node $ok\n" if $DEBUG & $INSP;

    return $ok;
}

sub extendPath
{
    my ($self, $node, @extra) = @_;
    print "EXTE: $node\n" if $DEBUG & $EXTE;
}

sub terminatesPath
{
    my ($self, $node, @extra) = @_;
    my $terminates = 0;
    print "TERM: $node $terminates\n" if $DEBUG & $TERM;

    return $terminates;
}

1;

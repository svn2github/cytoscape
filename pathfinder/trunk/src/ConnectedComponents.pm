package ConnectedComponents;

use DFS;

@ISA = qw(DFS);

sub new
{
    my ($caller, $graph) = @_;
    return $caller->SUPER::new($graph);
}

#
# Main subroutine for ConnectedComponents
#
sub search
{
    my ($self) = @_;

    my $id = 0;
    my %components;

    my $visitor = sub {
	my ($msg, $node, $depth, $time) = @_;
	#printf("%s %s [depth=%d], [t=%d]\n", $msg, $node, $depth, $time);
	
	if($msg eq "discovery")
	{
	    if($time == 0)
	    {
		$id += 1;
		push @{$components{$id}}, $node;
	    }
	    else
	    {
		push @{$components{$id}}, $node;
	    }
	}
    };

    my (%color, %ft, %dt);
    foreach my $n ($self->graph()->nodes())
    {
	$color{$n} = $self->{'WHITE'};
	$ft{$n} = 0;
	$dt{$n} = 0;
    }
    
    foreach my $n ($self->graph()->nodes())
    {
	next if($color{$n} != $self->{'WHITE'});
	print "### $n [start DFS]\n";
	
	my $time = 0;
	$self->dfsVisit($n, 0, \$time, \%color, \%dt, \%ft, $visitor);

	print "    $n [done]\n";
    }
    return \%components;
}

return 1;


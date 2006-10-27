package YeastHumanGeneMapper;

use GOGeneMap;

sub new
{
    my ($caller, $cacheDir) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);
    

    $self->{human} = GOGeneMap->new($cacheDir . "/human-genes-in-GO-DB.txt");
    $self->{yeast} = GOGeneMap->new($cacheDir . "/yeast-genes-in-GO-DB.txt");

    return $self;
}

sub getHumanMap
{
    my ($self) = @_;
    return $self->{human};
}


sub getYeastMap
{
    my ($self) = @_;
    return $self->{yeast};
}

#
# map human symbol to GO db internal id
# map yeast ORF to GO db internal id
#
sub mapName
{
    my ($self, $gene, $org) = @_;

    if($org =~ /homo sapiens/i)
    {
	my $map = $self->{human};
	if($map->existsSymbol($gene))
	{
	    return $map->symbol2go($gene);
	}
	elsif($gene =~ /(.*)-\d+/ && $map->existsSymbol($1))
	{
	    return $map->symbol2go($1);
	}
    }
    elsif($org =~ /saccharomyces cerevisiae/i)
    {
	if($self->{yeast}->existsXref($gene))
	{
	    return $self->{yeast}->xref2go($gene);
	}
    }
    #print STDERR __PACKAGE__ . ": invalid organism/gene [$org/$gene]\n";
    return undef;
}

1;

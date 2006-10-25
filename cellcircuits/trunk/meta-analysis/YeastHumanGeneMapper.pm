package YeastHumanGeneMapper;

use GOGeneMap;

sub new
{
#    my ($caller, $orgs) = @_;
    my ($caller) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);
    
    #foreach my $org (@{$orgs})
    #{
    #	my $key = makeKey($org);
    #	$self->{$key} = GOGeneMap->new("../db/dump/$key-in-GO-DB.txt");
    #}

    $self->{human} = GOGeneMap->new("../db/dump/human-genes-in-GO-DB.txt");
    $self->{yeast} = GOGeneMap->new("../db/dump/yeast-genes-in-GO-DB.txt");

    return $self;
}

my %keyCache;

sub makeKey
{
    my ($org) = @_;
    
    if(exists($keyCache{$org}))
    {
	return $keyCache{$org};
    }
    else
    {
	$org =~ s/\s+/-/g;
	my $k = lc($org);
	$keyCache{$org} = $k;
	return $k;
    }
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
# map human uniprot ids to GO db internal id
# map yeast ORF to GO db internal id
#
sub mapName
{
    my ($self, $gene, $org) = @_;

    if($org =~ /homo sapiens/i)
    {
	my $map = $self->{human};
	if($map->existsXref($gene))
	{
	    return $map->xref2go($gene);
	}
	elsif($gene =~ /(.*)-\d+/ && $map->existsXref($1))
	{
	    return $map->xref2go($1);
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

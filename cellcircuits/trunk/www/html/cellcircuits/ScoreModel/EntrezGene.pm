package scoremodel::EntrezGene;

sub new
{
    my ($caller, $gene_info_file) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);
    
    $self->parseInfoFile($gene_info_file);

    return $self;
}

sub parseInfoFile
{
    my ($self, $file) = @_;
    
    open(IN, $file) || die "Can't open $file\n";
    while(<IN>)
    {
	next if (/^\#/);
	chomp;
	my ($taxon, $geneid, $symbol, $locus, $syns, @rest) = split(/\t/);
	
	next if($geneid eq "" || $symbol eq "");
	$self->{gid2symbol}{$geneid} = $symbol;
	$self->{gid2synonyms}{$geneid} = $syns;
	$self->{symbol2gid}{$symbol} = $geneid;
    }

    printf STDERR "EntrezGene: read %d gene ids\n", scalar(keys %{$self->{gid2symbol}});
}

sub gid2symbol
{
    my ($self, $gid) = @_;

    return $self->{gid2symbol}{$gid};
}


sub gid2synonyms
{
    my ($self, $gid) = @_;

    return $self->{gid2synonyms}{$gid};
}


sub symbol2gid
{
    my ($self, $symbol) = @_;

    return $self->{symbol2gid}{$symbol};
}

sub existsGid
{
    my ($self, $gid) = @_;

    return exists $self->{gid2symbol}{$gid};
}

1;

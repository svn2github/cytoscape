package ScoreModel::GeneNameMapper;

@ISA = qw(ScoreModel::Object);

ScoreModel::GeneNameMapper->_generateAccessors();

sub new
{
    my ($caller) = @_;
    my $self = $caller->SUPER::new();
    $self->{yeastGeneMap} = readYeastTable();

    return $self;
}

#
# Convert a gene name to ORF
#
sub mapName
{
    my ($self, $g, $org) = @_;

    if(lc($org) eq "saccharomyces cerevisiae")
    {
	if(exists($self->{yeastGeneMap}->{$g}))
	{
	    my @orfs = split(/,/, $self->{yeastGeneMap}->{$g});
	    
	    return $orfs[0];
	}
    }

    return $g;
}



sub readYeastTable
{
    my ($self) = @_;
    my $data = "/cellar/users/cmak/data/name+alias2orf.noa";

    open(DATA, $data) || die "Cannot open data file: $data\n";
    my %geneMap;

    while(<DATA>)
    {
        chomp;
        my @fields = split(/\s*=\s*/);

        # input file is: [name|alias] = orf
        # if ambiguous, there will be a comma-separated list of orfs
        if(scalar(@fields) > 1)
        {
            $geneMap{$fields[0]} = $fields[1];
        }

    }

    close DATA;

    return \%geneMap;
}

1;

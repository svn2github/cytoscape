package ScoreModel::IPITable;

sub new
{
    my ($caller, $file) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);
    
    $self->{entrez2uniprot} = parseFile($file);

    return $self;
}

sub add
{
    my ($hash, $uniprot, $eString) = @_;
    
    my @eIDs = split(/;/, $eString);
    
    foreach $id (@eIDs)
    {
	if($id =~ /(.*),(.*)/)
	{
	    push @{$hash->{$1}}, $uniprot;
	}
    }
}

sub parseFile
{
    my ($file) = @_;

    my %data;
    open(IN, $file) || die __PACKAGE__ . ": Can't open $file\n";
    while(<IN>)
    {
	chomp;
	my @F = split(/\t/);
	if($F[0] eq "SP" || $F[0] eq "TR")
	{
	    my ($uniprot, $eString) = @F[1,11];
	    
	    next if (!defined($uniprot));
	    next if (!defined($eString));
	    next if ($uniprot eq "" || $eString eq "");
	    #print "db=$F[0] uni=$uniprot, ent=$eString\n";
	    
	    add(\%data, $uniprot, $eString);
	}

	my ($uni1, $uni2, $eString) = @F[3,4,11];
	
	next if (!defined($eString));
	next if ($eString eq "");
	if(defined($uni1) && $uni1 ne "")
	{
	    my @u1 = split(/;/, $uni1);
	    map {add(\%data, $_, $eString)} @u1;
	}
	if(defined($uni2) && $uni2 ne "")
	{
	    my @u2 = split(/;/, $uni2);
	    map {add(\%data, $_, $eString)} @u2;
	}
    }
    close IN;

    printf STDERR "### $file: read %d mappings\n", scalar(keys %data);
    
    return \%data;
}

# returns an array of uniprot ids mapped to the input entrez gene id
sub get
{
    my ($self, $entrezId) = @_;

    return $self->{entrez2uniprot}->{$entrezId};
}

# check if a mapping exists for an entrez gene id
sub exists
{
    my ($self, $entrezId) = @_;
    
    return exists($self->{entrez2uniprot}->{$entrezId});
}

1;

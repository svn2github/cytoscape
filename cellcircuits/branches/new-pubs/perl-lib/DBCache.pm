package DBCache;

sub new
{
    my ($caller, $cacheDir) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);
    
    $self->parseSpecies($cacheDir . "/species.txt");
    $self->parseUnknownTerms($cacheDir . "/unknown-terms.txt");

    return $self;
}

sub parseSpecies
{
    my ($self, $file) = @_;
    
    open(IN, $file) || die "Can't open $file\n";
    while(<IN>)
    {
	chomp;
	next if ($. == 0);

	my ($id, $name) = split(/\t/);
	
	next if($id eq "" || $name eq "");
	$self->{speciesId2name}{$id} = $name;
	$self->{name2speciesId}{$name} = $id;
    }

    printf STDERR "Species: read %d species\n", scalar(keys %{$self->{speciesId2name}});
}


sub speciesId2name
{
    my ($self, $sid) = @_;

    return $self->{specesId2name}{$sid};
}


sub name2speciesId
{
    my ($self, $name) = @_;

    return $self->{name2speciesId}{$name};
}

sub existsSpecies
{
    my ($self, $name) = @_;

    return exists $self->{name2speciesId}{$name};
}

sub parseUnknownTerms
{
    my ($self, $file) = @_;
    
    open(IN, $file) || die "Can't open $file\n";
    while(<IN>)
    {
	chomp;
	next if ($. == 0);

	my ($id, $name) = split(/\t/);
	
	next if($id eq "" || $name eq "");
	$self->{termId2name}{$id} = $name;
	$self->{name2termId}{$name} = $id;
    }

    printf STDERR "UnknownTerms: read %d terms\n", scalar(keys %{$self->{termId2name}});
}


sub termId2name
{
    my ($self, $id) = @_;

    return $self->{termId2name}{$id};
}


sub name2termId
{
    my ($self, $name) = @_;

    return $self->{name2termId}{$name};
}

sub existsTerm
{
    my ($self, $name) = @_;

    return exists $self->{name2termId}{$name};
}

1;

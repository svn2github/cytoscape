package ScoreModel::HumanUniprot2GO;

#
# Translate UniProt ID's into GO database internal ids
# 

sub new
{
    my ($caller, $file) = @_;
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);
    
    $self->parseFile($file);

    return $self;
}



sub parseFile
{
    my ($self, $file) = @_;

    my %uni2name;
    my %uni2go;
    open(IN, $file) || die __PACKAGE __ . ": Can't open $file\n";
    while(<IN>)
    {
	next if(/^\#/);
	chomp;
	my ($go, $name, $species, $uni, $db) = split(/\t/);

	$uni2name{$uni} = $name;
	$uni2go{$uni} = $go;
    }
    close IN;

    $self->{uniprot2symbol} = \%uni2name;
    $self->{uniprot2go} = \%uni2go;

    printf STDERR "### %s: read %d mappings\n", $file, scalar(keys %uni2name);
}



sub exists
{
    my ($self, $id) = @_;
    return exists($self->{uniprot2go}->{$id});
}


sub uniprot2symbol
{
    my ($self, $id) = @_;
    return $self->{uniprot2symbol}->{$id};
}


sub uniprot2go
{
    my ($self, $id) = @_;
    return $self->{uniprot2go}->{$id};
}

1;

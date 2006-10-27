package GOGeneMap;

#
# Translate External ID's into GO database internal ids
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

    my %xref2symbol;
    my %xref2go;
    my %symbol2go;
    my $x = 0;
    open(IN, $file) || die __PACKAGE__ . ": Can't open $file\n";
    while(<IN>)
    {
	next if(/^\#/);
	chomp;
	my ($go, $symbol, $species, $xref, $db) = split(/\t/);
	
	$xref2symbol{$xref} = $symbol;
	$xref2go{$xref} = $go;
	$symbol2go{$symbol} = $go;
	$x++;
    }
    close IN;

    $self->{xref2symbol} = \%xref2symbol;
    $self->{xref2go} = \%xref2go;
    $self->{symbol2go} = \%symbol2go;

    printf STDERR "### %s: read %d mappings\n", $file, $x;
}



sub existsXref
{
    my ($self, $id) = @_;
    return exists($self->{xref2go}->{$id});
}


sub existsSymbol
{
    my ($self, $id) = @_;
    return exists($self->{symbol2go}->{$id});
}


sub xref2symbol
{
    my ($self, $id) = @_;
    return $self->{xref2symbol}->{$id};
}


sub xref2go
{
    my ($self, $id) = @_;
    return $self->{xref2go}->{$id};
}


sub symbol2go
{
    my ($self, $id) = @_;
    return $self->{symbol2go}->{$id};
}


1;

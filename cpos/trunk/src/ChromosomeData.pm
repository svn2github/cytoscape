package ChromosomeData;

use Object;
our @ISA = qw(Object);

ChromosomeData->_generateAccessors(qw(data));

sub new
{
    my ($caller, $file) = @_;

    my $self = $caller->SUPER::new();

    $self->data({});
    $self->readFile($file);
    
    return $self;
}

sub getCentromerePosition
{
    my ($self, $chr) = @_;
    return -1 if (!exists($self->data()->{$chr}));
    return $self->data()->{$chr}{cen};
}

sub getSize
{
    my ($self, $chr) = @_;
    return -1 if (!exists($self->data()->{$chr}));
    return $self->data()->{$chr}{size};
}

sub readFile
{
    my ($self, $file) = @_;

    open(IN, $file) || die "Can't open $file: $!\n";
    $self->data({});
    my $data = $self->data();

    while(<IN>)
    {
	chomp;
	next if (/^#/);
	my @F = split(/\t/);
	die "Error in chromosome file: $_\n" if (scalar(@F) < 2);
	
	my ($chr, $size, $cenStart, $cenEnd) = @F;
	$data->{$chr}{size} = $size;
	if(defined($cenStart) && defined($cenEnd))
	{
	    $data->{$chr}{cen} = POSIX::floor(($cenStart + $cenEnd)/2);
	}
	elsif(defined($cenStart))
	{
	    $data->{$chr}{cen} = $cenStart;
	}
	else
	{
	    $data->{$chr}{cen} = "";
	}
    }
    close IN;
}

return 1;

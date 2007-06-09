package Util;

require Exporter;
our @ISA = qw(Exporter);

#symbols to export by default
our @EXPORT = qw(
		 min
		 which
		 keysSortedByValueArraySize
		 );

#symbols to export on request
our @EXPORT_OK = qw(); 

sub which
{
    my ($arrayRef, $functionRef) = @_;

    my @inds;
    for(my $x=0; $x < scalar(@{$arrayRef}); $x++)
    {
	push @inds, $x if($functionRef->($arrayRef->[$x]));
    }
    return @inds;
}


sub min
{
    return undef if (scalar(@_) == 0);
    my $x = shift @_;
    foreach (@_)
    {
	if($_ < $x)
	{
	    $x = $_;
	}
    }
    return $x;
}


sub keysSortedByValueArraySize
{
    my ($h, $ascending) = @_;
    if(defined($ascending) && $ascending)
    {
	return sort { scalar(@{$h->{$a}}) <=> scalar(@{$h->{$b}}) } keys %{$h};
    }
    return sort { scalar(@{$h->{$b}}) <=> scalar(@{$h->{$a}}) } keys %{$h};
}

1;

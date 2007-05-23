package MinProfileAnalyzer;

use Object;

@ISA = qw(Object);

MinProfileAnalyzer->_generateAccessors();

sub new
{
    my ($caller) = @_;
    my $self = $caller->SUPER::new($file);
    return $self;
}

sub analyze
{
    my ($self, $valueArray) = @_;

    my $min = undef;
    my $ind = -1;
    my $m;
    for my $i (0..(scalar(@{$valueArray})-1))
    {
	$m = $valueArray->[$i];
	if(!defined($min) || $m < $min) 
	{ 
	    $min = $m;
	    $ind = $i;
	}
    }
    print STDERR "### MinPA: returning $ind\n";
    return ([$ind]);
}



1;

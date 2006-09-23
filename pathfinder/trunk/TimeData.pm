package TimeData;

use PVLR;

@ISA = qw(PVLR);

sub new
{
    my ($caller, $file, $idHash) = @_;
    my $self = $caller->SUPER::new($file, $isHash);
    return $self;
}


sub getTimeOfMaxSigExpression
{
    my ($self, $gene) = @_;

    my @names = @{$self->columnNames()};

    die "$gene does not exist" if (! exists($self->ids()->{$gene}));
    my $row = $self->ids()->{$gene};
    my @ratios;
    map {push @ratios, $self->ratios()->{$_}->[$row]} @names;

    my @pv;
    map {push @pv, $self->pvalues()->{$_}->[$row]} @names;

    printf "$gene: [%s]\n", join(",", @ratios);
    printf "$gene: [%s]\n", join(",", @pv);
}

1;

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
    my ($self, $gene, $pval_thresh) = @_;

    my ($ratios, $pvals) = $self->getGeneData($gene);
    
    my $max = -1;
    my $maxInd = -1;
    
    foreach my $i (0..(scalar(@{$ratios}) - 1))
    {
	if(($pvals->[$i] <= $pval_thresh) && 
	   (abs($ratios->[$i]) > $max))
	{
	    $max = abs($ratios->[$i]);
	    $maxInd = $i;
	}
    }
    return $maxInd;
}


sub getTimeOfFirstSigExpression
{
    my ($self, $gene, $pval_thresh) = @_;

    my ($ratios, $pvals) = $self->getGeneData($gene);

    foreach my $i (0..(scalar(@{$ratios}) - 1))
    {

	if($pvals->[$i] <= $pval_thresh)
	{
	    return $i;
	}
    }
    return -1;
}

# return a hash of all genes with a significant time of max
# expression mapped to their TME.
sub getAllTME
{
    my ($self, $pval_thresh) = @_;

    my %data;
    my $tme;
    foreach my $id (@{$self->ids()})
    {
	my $tme = $self->getTimeOfMaxSigExpression($id, $pval_thresh);
	if ($tme >= 0)
	{
	    $data{$id} = $tme;   
	}
    }
    return \%data;
}

1;

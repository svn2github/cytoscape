package TimeData;

use PVLR;

@ISA = qw(PVLR);

sub new
{
    my ($caller, $file, $idHash) = @_;
    my $self = $caller->SUPER::new($file, $idHash);
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
    my ($self, $pval_thresh, $tmeOutputFile, $ratioOutputFile) = @_;

    my $out = 0;
    if(defined($tmeOutputFile))
    {
	open(OUT, ">$tmeOutputFile") || die "Can't open $tmeOutputFile\n";
	print OUT "TimeOfMaxSigExpression (class=java.lang.Integer)\n";
	$out = 1
    }

    if(defined($ratioOutputFile))
    {
	open(OUT2, ">$ratioOutputFile") || die "Can't open $ratioOutputFile\n";
	print OUT2 "MaxSigExpression (class=java.lang.Double)\n";
	$out2 = 1
    }


    my %data;
    my $tme;
    foreach my $id (@{$self->ids()})
    {
	my $tme = $self->getTimeOfMaxSigExpression($id, $pval_thresh);
	my $ratio = $self->getRatio($id, $self->columnNames()->[$tme]);
	if ($tme >= 0)
	{
	    $data{$id} = $tme;   
	    printf OUT ("%s = %s\n", $id, $tme) if($out);
	    printf OUT2 ("%s = %s\n", $id, $ratio) if($out2);

	}
    }
    close OUT if($out);
    close OUT2 if($out2);

    return \%data;
}

1;

package QpcrCopyNumberMultiTimeData;

use QpcrCopyNumberTimeData;

@ISA = qw(QpcrCopyNumberTimeData);

#QpcrCopyNumberMultiTimeData->_generateAccessors(qw());

# Instead of a single time of maximum expression, a MultiTimeData
# object can have multiple times of maximum expression.
#
# This is motivated by the "cycling" genes observed in Tim's
# LPS and PMA qpcr data.
#
# Input: a gene identifier and a reference to a function.
#     The function must take an expression value and the temporal index
#     and return true if that expr value should be considered a "maxima"
#     or false if not.
# Return: an array of times
#
sub getTimeOfMaxSigExpression
{
    my ($self, $gene, $profileAnalyzer) = @_;

    my $vals = $self->getGeneData($gene);
    
    my $ref2indexArray = $profileAnalyzer->analyze($vals);

    return $ref2indexArray;
}

# return a hash of all genes with a significant time of max
# expression mapped to their TME.
sub getAllTME
{
    my ($self, $tmeDispatch, $tmeOutputFile, $ratioOutputFile) = @_;

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
    my ($tme, $profileAnalyzer, @vals);
    foreach my $id (@{$self->ids()})
    {
	if(exists($tmeDispatch->{$id}))
	{
	    print STDERR "*** $id found in dispatch\n";
	    $profileAnalyzer = $tmeDispatch->{$id};
	}
	else
	{
	    print STDERR "### $id is default\n";
	    $profileAnalyzer = $tmeDispatch->{"__DEFAULT__"};
	}

	$tme = $self->getTimeOfMaxSigExpression($id, $profileAnalyzer);
	if (scalar(@{$tme}) > 0)
	{
	    @vals = ();
	    @vals = map { $self->getValue($id, $self->columnNames()->[$_]) } @{$tme};
	    $data{$id} = $tme;   
	    printf OUT ("%s = (%s)\n", $id, join("::", @{$tme})) if($out);
	    printf OUT2 ("%s = (%s)\n", $id, join("::", @vals)) if($out2);

	}
    }
    close OUT if($out);
    close OUT2 if($out2);

    return \%data;
}

1;

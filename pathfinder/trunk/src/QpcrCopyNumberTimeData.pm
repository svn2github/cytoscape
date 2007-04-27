package QpcrCopyNumberTimeData;

use Object;

@ISA = qw(Object);

QpcrCopyNumberTimeData->_generateAccessors(qw(columnNames descriptions idTable values));

sub new
{
    my ($caller, $file) = @_;
    my $self = $caller->SUPER::new();

    $self->columnNames([]);
    $self->idTable({});
    $self->descriptions([]);
    $self->values({});

    $self->readFile($file);

    return $self;
}


sub readFile
{
    my ($self, $file) = @_;
    open (IN, $file) || die "Can't open $file: $!\n";

    my $names = $self->columnNames();
    my $idTable = $self->idTable();
    my $descriptions = $self->descriptions();
    my $vals = $self->values();

    my @l;
    my ($Nnames, $Ni);
    my $count = 0;
    while(<IN>)
    {
	chomp;
	@l = split("\t");
	$id   = shift(@l);
	$desc = shift(@l);
	if($. == 1) 
	{
	    $Nnames = scalar(@l);
	    for my $i (0..$#l) 
	    {
		push @{ $names }, $l[$i];
	    }
	    printf STDERR "### [$file] cols: %s\n", join(",", @{$self->columnNames()});
	} 
	else 
	{
	    $Ni = scalar(@l);
	    die "$file wrong num fields. Line:$. $Ni $Nnames $_\n" if ($Ni != $Nnames);

	    $idTable->{$id} = $count++;
	    push @{ $descriptions }, $desc;
	    for my $i (0..($Ni-1)) 
	    {
		push @{ $vals->{$names->[$i]} }, $l[$i];
	    }
	}
    }
    close IN;
}

sub getTimeOfMaxSigExpression
{
    my ($self, $gene, $thresh) = @_;

    my ($vals) = $self->getGeneData($gene);
    
    my $max = -1;
    my $maxInd = -1;
    
    foreach my $i (0..(scalar(@{$vals}) - 1))
    {
	my $x = $vals->[$i];
	next if ($thresh ne "" && abs($x) < $thresh);
	if(abs($x) > $max)
	{
	    $max = abs($x);
	    $maxInd = $i;
	}
    }
    return $maxInd;
}

sub getGeneData
{
    my ($self, $id) = @_;

    my @names = @{$self->columnNames()};

    die "$id does not exist" if (! exists($self->idTable()->{$id}));
    my $row = $self->idTable()->{$id};
    my @vals;
    map {push @vals, $self->values()->{$_}->[$row]} @names;

    return (\@vals);
}

sub index2colName
{
    my ($self, $i) = @_;

    return "" if ($i < 0 || $i > scalar(@{$self->columnNames()}));
    return $self->columnNames()->[$i];
}


sub ids
{
    my ($self) = @_;
    my @x = keys(%{$self->idTable()});
    return( \@x );
}

sub getValue
{
    my ($self, $id, $columnName) = @_;

    my $row = $self->idTable()->{$id};
    return $self->values()->{$columnName}->[$row];
}


sub getTimeOfFirstSigExpression
{
    my ($self, $gene, $thresh) = @_;

    my ($vals) = $self->getGeneData($gene);

    foreach my $i (0..(scalar(@{$vals}) - 1))
    {
	if($vals->[$i] > $thresh)
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
    my ($self, $thresh, $tmeOutputFile, $ratioOutputFile) = @_;

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
	my $tme = $self->getTimeOfMaxSigExpression($id, $thresh);
	my $val = $self->getValue($id, $self->columnNames()->[$tme]);
	if ($tme >= 0)
	{
	    $data{$id} = $tme;   
	    printf OUT ("%s = %s\n", $id, $tme) if($out);
	    printf OUT2 ("%s = %s\n", $id, $val) if($out2);

	}
    }
    close OUT if($out);
    close OUT2 if($out2);

    return \%data;
}

1;

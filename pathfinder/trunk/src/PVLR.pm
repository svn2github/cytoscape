package PVLR;

use Object;

@ISA = qw(Object);

PVLR->_generateAccessors(qw(columnNames idTable descriptions ratios pvalues));

sub new
{
    my ($caller, $file, $idHash) = @_;
    my $self = $caller->SUPER::new();

    $self->columnNames([]);
    $self->idTable({});
    $self->descriptions([]);
    $self->ratios({});
    $self->pvalues({});

    if(defined($file)) { $self->readLogratiosPvalues($file, $idHash) };
    return $self;
}

sub idsInOrder
{
    my ($self) = @_;
    
    my $x = $self->idTable();
    return( sort {$x->{$a} <=> $x->{$b}} keys( %{$x} ) );
}


sub ids
{
    my ($self) = @_;
    my @x = keys(%{$self->idTable()});
    return( \@x );
}

sub getRatio
{
    my ($self, $id, $columnName) = @_;

    my $row = $self->idTable()->{$id};
    return $self->ratios()->{$columnName}->[$row];
}


sub getGeneData
{
    my ($self, $id) = @_;

    my @names = @{$self->columnNames()};

    die "$gene does not exist" if (! exists($self->idTable()->{$id}));
    my $row = $self->idTable()->{$id};
    my @ratios;
    map {push @ratios, $self->ratios()->{$_}->[$row]} @names;

    my @pv;
    map {push @pv, $self->pvalues()->{$_}->[$row]} @names;

    return (\@ratios, \@pv);
}

sub makeORFlistByRowByPvalue
{
    my $self = shift;
    return $self->_makeORFlist(1, 1, @_);
}


sub makeORFlistByRowByRatio
{
    my $self = shift;
    return $self->_makeORFlist(0, 1, @_);
}



sub makeORFlistByColumnByPvalue
{
    my $self = shift;
    return $self->_makeORFlist(1, 0, @_);
}


sub makeORFlistByColumnByRatio
{
    my $self = shift;
    return $self->_makeORFlist(0, 0, @_);
}


sub _makeORFlist
{
    my ($self, $byPvalue, $byRow, $function, $validColumns) = @_;

    my $data;
    if($byPvalue)
    {
	$data = $self->pvalues();
    }
    else
    {
	$data = $self->ratios();
    }

    my %results;
    my $ids = $self->ids();
    my $idTable = $self->idTable();
    my $row;
    my @names = @{$self->columnNames()};

    if(defined($validColumns))
    {
	@names = @names[@{$validColumns}];
    }


    foreach my $i (@{$ids})
    {
	$row = $idTable->{$i};
	
	foreach (@names)
	{
	    if($function->($data->{$_}->[$row]))
	    {
		if($byRow)
		{
		    push @{$results{$i}}, $_;
		}
		else
		{
		    push @{$results{$_}}, $i;
		}
	    }
	}
    }
    
    return \%results;
}

#############################################
##
## Initial copy from Chris Workman's utilities.pm module 1/6/06
##
## Input: $file -  A file containing pvalues
##        $idHash - A hash of acceptable row ids
##
## Populates instance variables: 
##         $names - a reference to a list of column names
##         $idTable - a reference to a hash of row ids mapped to their row 
##         $descriptions - a reference to a list of row descriptions 
##         $pvals - a reference to a hash of pvalues.
##                      Keys = column names 
##                      Values = array of pvalues 
##         $ratios - a reference to a hash of ratios.
##                      Keys = column names 
##                      Values = array of ratios 
###########################################
sub readLogratiosPvalues 
{
    my ($self, $file, $idHash) = @_;
    my (@l, $id, $desc, $i, $p, $r, $n, $mid_n);

    my($n_ids) = scalar(keys %{ $idHash });

    my $names = $self->columnNames();
    my $idTable = $self->idTable();
    my $descriptions = $self->descriptions();
    my $pvals = $self->pvalues();
    my $ratios = $self->ratios();

    my $count = 0;
    open(BLA, $file);
    while(<BLA>){
	chomp;
	@l    = split(/\t/);
	$id   = shift(@l);
	$desc = shift(@l);
	if($. == 1) {
	    $n = scalar(@l);
	    $mid_n = $n/2;
	    #printf STDERR "MID $mid_n\n";
	    for $i (0..$#l) {
		push @{ $names }, $l[$i];
	    }
	    printf STDERR "### [$file] cols: %s\n", join(",", @{$self->columnNames()});
	} else {
	    $n_i = scalar(@l);
	    die "$file line:$. $n_i $n $_\n" if ($n_i != $n);

	    if( ($n_ids == 0) || (exists $idHash->{$id})) {

		#print "$id\n";
		$idTable->{$id} = $count++;
		push @{ $descriptions }, $desc;
		for $i (0..($mid_n-1)) {
		    $r = $l[$i];
		    ## need to do this to get decreasing sort to work
		    ##if($r eq "NA") { $r = 0; }
		    $r = filter_real_number($r, 0);
		    push @{ $ratios->{$names->[$i]} }, $r;
		}
		for $i ($mid_n..$#l) {
		    $p = $l[$i];
		    ## need to do this to get decreasing sort to work
		    ##if($p eq "NA") { $p = 1; }
		    $p = filter_real_number($p, 1);
		    push @{ $pvals->{$names->[$i]} }, $p;
		}
	    }
	}
    }
    close(BLA);

    my @subset = @{ $names }[$mid_n..(scalar(@{$names}) - 1)];

    $self->columnNames(\@subset);
}


#############################################
##
sub filter_real_number 
{
    my($r, $alt) = @_;
    if( ($r eq "NA") || ($r eq "Inf") || ($r eq "-Inf") || ($r eq "NaN")) 
    { $r = $alt; }
    return($r);
}


return 1;

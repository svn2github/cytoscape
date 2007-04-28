package GeneSet;

use Object;
use POSIX;
use SGD;

our @ISA = qw(Object);

GeneSet->_generateAccessors(qw(orfs
			       analyzedOrfs
			       name
			       dataFile
			       midPoints 
			       chromosomes 
			       minTelomereDist 
			       centromereDist));

sub new
{
    my ($caller, $name, $orfArray) = @_;

    my $self = $caller->SUPER::new();

    my %tmp;
    map { $tmp{$_}++ } @{$orfArray};

    my @uniqueOrfs = sort keys %tmp; 
    $self->orfs(\@uniqueOrfs);

    $self->analyzedOrfs([]);
    $self->name($name);
    $self->dataFile(undef);
    $self->midPoints([]);
    $self->chromosomes([]);
    $self->minTelomereDist([]);
    $self->centromereDist([]);

    return $self;
}

sub analyze
{
    my ($self, $geneData, $chrData) = @_;

    my $chrIndex = $geneData->indexOfField("chromosome");
    my $startIndex = $geneData->indexOfField("startCoordinate");
    my $stopIndex = $geneData->indexOfField("stopCoordinate");

    my @orfs = @{$self->orfs()};
    foreach my $i (0..$#orfs)
    {
	my $orf = $orfs[$i];

	if(!$geneData->featureExists($orf))
	{
	    printf STDERR "   $orf does not exist in feature table\n";
	    next;
	}
	$chr = $geneData->getByIndex($orf, $chrIndex);
	next if ($chr eq 17); # discard mitochondrial genes

	push @{$self->analyzedOrfs()}, $orf;
    }

    @orfs = @{$self->analyzedOrfs()};
    foreach my $i (0..$#orfs)
    {
	my $orf = $orfs[$i];
	$start = $geneData->getByIndex($orf, $startIndex);
	$stop = $geneData->getByIndex($orf, $stopIndex);
	$mid = POSIX::floor(($stop + $start)/2);
	$chr = $geneData->getByIndex($orf, $chrIndex);
	$cen = $chrData->getCentromerePosition($chr);
	$chrSize = $chrData->getSize($chr);
    
	$self->midPoints()->[$i] = $mid;
	$self->chromosomes()->[$i] = $chr;
	$self->minTelomereDist()->[$i] = min($mid, $chrSize - $mid);
	$self->centromereDist()->[$i] = abs($cen - $mid);
    }
}

sub printData
{
    my ($self, $file) = @_;

    my $OUT = *STDOUT;
    
    if(defined($file) && $file ne "")
    {
	open(X, ">$file") || die "Can't open $file: $!\n";
	$OUT = *X;
    }

    print $OUT join("\t", qw(orf chr mid mtd cd)) . "\n";

    my @orfs = @{$self->analyzedOrfs()};
    foreach my $i (0..$#orfs)
    {
	printf $OUT ("%s\n", join("\t", 
			    $orfs[$i],
			    $self->chromosomes()->[$i],
			    $self->midPoints()->[$i],
			    $self->minTelomereDist()->[$i],
			    $self->centromereDist()->[$i]));

    }
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

return 1;

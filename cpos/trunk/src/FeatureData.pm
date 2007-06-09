package FeatureData;

use Object;
our @ISA = qw(Object);

FeatureData->_generateAccessors(qw(fields 
				   field2index
				   data));

use Constant ID_FIELD=>"featureName";

my %DISPATCH =  (sgd => \&_newSGD,
		 boyer => \&_newBoyer);

sub typeExists
{
    my ($type) = @_;
    return exists($DISPATCH{$type});
}

sub create
{
    my ($type, $file) = @_;

    if(typeExists($type))
    {
	$DISPATCH{$type}->($file);
    }
}


#
# Factory method to create an SGD FeatureData object
#
sub _newSGD
{
    my ($file) = @_;

    #geneName eg CIT2
    #featureName eg YCR005C

    my $sgd = FeatureData->new(qw(primarySGDID 
				  featureType
				  featureQualifier
				  featureName
				  geneName
				  alias
				  parentFeatureName
				  secondardSGDID
				  chromosome
				  startCoordinate
				  stopCoordinate
				  strand
				  geneticPosition
				  coordinateVersion
				  sequenceVersion
				  description));


    $sgd->readFile($file, "featureName");
    return $sgd;
}


sub _newBoyer
{
    my ($file) = @_;

    my $fd = FeatureData->new(qw(featureName chromosome startCoordinate stopCoordinate));

    $fd->readFile($file, "featureName");
    return $fd;
}

#
# Constructor.
# Consider creating new factory methods for each type of feature file.
#
sub new
{
    my ($caller, @fieldNames) = @_;

    my $self = $caller->SUPER::new();

    $self->fields(\@fieldNames);
    $self->field2index({});
    $self->data({});

    my $x = 0;
    map { $self->field2index()->{$_} = $x++ } @fieldNames;

    return $self;
}

sub featureExists
{
    my ($self, $id) = @_;
    return exists($self->data()->{$id});
}

sub getAllIDs
{
    my ($self) = @_;

    return keys(%{$self->data()});
}

sub get
{
    my ($self, $id, $field) = @_;

    my $i = $self->indexOfField($field);
#    return "" if (!exists($self->data()->{$id}) || $i < 0);
    return $self->data()->{$id}->[$i];
}


sub getByIndex
{
    my ($self, $id, @inds) = @_;

    #printf STDERR "###getting %s for %s\n", join(",", @inds), $id;

#    return "" if (!exists($self->data()->{$id}) || $fieldIndex < 0);
    return @{$self->data()->{$id}}[@inds];
}


sub indexOfField
{
    my ($self, $f) = @_;
    return -1 if (!exists($self->field2index()->{$f}));
    return $self->field2index()->{$f};
}

sub readFile
{
    my ($self, $file, $idField) = @_;

    open(IN, $file) || die "Can't open $file: $!\n";
    my $data = $self->data();
    my $nameIndex = $self->indexOfField($idField);
    while(<IN>)
    {
	next if (/^\#/);
	chomp;
	my @f = split(/\t/);
	next if $f[$nameIndex] eq "";
	if(exists($data->{$f[$nameIndex]}))
	{
	    printf STDERR ("### Duplicate name in SGD_features.tab: %s\n", 
			   $f[$nameIndex]);
	}
	$data->{$f[$nameIndex]} = \@f;
    }
}

return 1;

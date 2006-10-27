#!/usr/bin/perl -I../perl-lib -w

use Publication;
use EdgeMapper;
use GeneNameMapper;

my $usage = qq(
  usage: $0 <data dir> <db cache dir> <pub> <pubs>+
    [-a] score all vs all models
	       );

my $DOALL = 0;
my @args;
while(@ARGV) {
    $arg = shift;
    if($arg =~ /-a/){ $DOALL = 1; printf STDERR "Scoring all vs all\n"; }
    else{ push @args, $arg; }
}

if(@args < 3) { die $usage; }

my $DATA_DIR = shift @args;
my $CACHE_DIR = shift @args;
my @PUB_NAMES = @args;
my $QUERY_PUB;

print STDERR "DATA_DIR = $DATA_DIR\n";
print STDERR "CACHE_DIR = $CACHE_DIR\n";
printf STDERR "PUB_NAMES = [%s]\n", join(",", @PUB_NAMES);

if(!$DOALL)
{
    $QUERY_PUB = $args[0];

    print STDERR "QUERY_PUB = $QUERY_PUB\n";
}

my %modelName2ID = %{readModelTable($CACHE_DIR . "/model.txt")};

my %pubs;

my $gMap = GeneNameMapper->new();
my $eMap = EdgeMapper->new();

my @allSIFs;

my %genes;


my @querySIFinds;

##
## Read in all of the SIF files
##
my ($p, $pub, $sif, $org, $genes);
foreach $p (@PUB_NAMES)
{
    $pub = Publication->new($p, $DATA_DIR, $gMap, $eMap);
    $pubs{$p} = $pub;

#    print "\n### $p\n";
#    print $pub->print();
    
    if(!$DOALL && ($QUERY_PUB eq $p))
    {
	@querySIFinds = scalar(@allSIFs)..(scalar(values(%{$pub->sifs()})) - 1);
	printf STDERR ("QUERY_PUB = %d models [%d..%d];\n", 
		       scalar(@querySIFinds),
		       $querySIFinds[0],
		       $querySIFinds[-1],
		       );
    }

    push @allSIFs,  values(%{$pub->sifs()});

    # use a hash-slice to add all of the genes from a sif
    # to a master list containing all genes (indexed by organism)
    foreach $sif (values(%{$pub->sifs()}))
    {
	while(($org, $genes) = each %{$sif->org2genes()})
	{
	    @{$genes{$org}}{ keys %{$genes} } = ();
	}
    }
}

print STDERR  "Read " . scalar @allSIFs . " sifs\n";

printf STDERR  "Orgs: %s\n", join(", ", keys %genes); 
my $yeast = "Saccharomyces cerevisiae";

##
## Compute gene overlaps for yeast genes only
##
if(exists($genes{$yeast}))
{
    my ($size, $numbers, $names) = members_to_numbers($genes{$yeast});
    my (@sifNames, @vectors);
    my $modelName;
    
    # first, create a bit-vector representation of the genes in each sif
    # also, associate each vector with its database model_id
    foreach $sif (@allSIFs)
    {
	if(exists($sif->org2genes()->{$yeast}))
	{
	    $modelName = join(":", $sif->pub(), $sif->name());
	    if(exists($modelName2ID{$modelName}))
	    {
		push @sifNames, $modelName2ID{$modelName};
		push @vectors, hash_set_to_bit_vector($sif->org2genes()->{$yeast},
						      $numbers);
	    }
	    else
	    {
		print STDERR "WARNING: read SIF not in database: $modelName\n";
	    }
	}
    }

    

    # compute all intersections
    # save pairs of models that have more than 3 common genes
    my $intersection = 0;

    my @outer;
    if($DOALL) { @outer = 0..$#vectors; }
    else { @outer = @querySIFinds; }
    
    for my $i (@outer)
    {
	for my $j (($i+1)..$#vectors)
	{
	    $intersection = $vectors[$i] & $vectors[$j];
	    if(count_bit_vector_members($intersection) > 3)
	    {
		printf("insert into model_similarity (model_id_a, model_id_b, gene_score) values (%s, %s, %d);\n", 
		       $sifNames[$i], 
		       $sifNames[$j],
		       count_bit_vector_members($intersection));
	    }
	}
    }
}

sub readModelTable
{
    my ($file) = @_;

    open(IN, $file) || die "Can't read $file\n";
    my %model;
    my @f;
    while(<IN>)
    {
	chomp;
	($id, $pub, $name) = split();
	$model{join(":", $pub, $name)} = $id;
    }
    close IN;
    return \%model;
}

## Set methods
## From Mastering Algorithms With Perl pp 208-209
sub members_to_numbers
{
    my (@names, $name);
    my (%numbers, $number);

    $number = 0;
    while(my $set = shift @_)
    {
	while( defined($name = each %$set))
	{
	    unless( exists $numbers{$name})
	    {
		$numbers{$name} = $number;
		$names[$number] = $name;
		$number++;
	    }
	}
    }
    return ($number, \%numbers, \@names);
}

sub hash_set_to_bit_vector
{
    my ($hash, $numbers) = @_;
    my ($name, $vector);
    $vector = '';

    while(defined($name = each %{$hash}))
    {
	vec($vector, $numbers->{$name}, 1) = 1;
    }
    return $vector;
}

sub bit_vector_to_hash_set
{
    my ($vector, $names) = @_;
    my ($number, %hash_set);

    foreach $number (0..$#{$names})
    {
	$hash_set {$names->[$number]} = undef
	    if vec($vector, $number, 1);
    }
    return \%hash_set;
}

sub count_bit_vector_members
{
    return unpack "%32b*", $_[0];
}

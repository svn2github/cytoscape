#!/usr/bin/perl -w

use Publication;
use EdgeMapper;
use GeneNameMapper;


my $dataDir = "/var/www/html/search/data";

my @pubNames = qw(Begley2002_MCR
		  Bernard2005_PSB
		  de_Lichtenberg2005_Science
		  Gandhi2006_NG
		  Hartemink2002_PSB
		  Haugen2004_GB 
		  Ideker2002_BINF
		  Kelley2005_NBT 
		  Sharan2005_PNAS
		  Suthram2005_Nature
		  Yeang2005_GB);


#my @pubNames = qw( Yeang2005_GB );
#my @pubNames = qw( TestPub );

my %pubs;

my $gMap = GeneNameMapper->new();
my $eMap = EdgeMapper->new();

my @allSIFs;

my %genes;

##
## Read in all of the SIF files
##
my ($p, $pub, $sif, $org2genes, $org, $genes);
foreach $p (@pubNames)
{
    $pub = Publication->new($p, $dataDir, $gMap, $eMap);
    $pubs{$p} = $pub;

#    print "\n### $p\n";
#    print $pub->print();

    push @allSIFs,  values(%{$pub->sifs()});
    
    foreach $sif (values(%{$pub->sifs()}))
    {
	$org2genes = $sif->org2genes();
	while(($org, $genes) = each %{$org2genes})
	{
	    @{$genes{$org}}{ keys %{$genes} } = ();
	}
    }
}

print STDERR  "Read " . scalar @allSIFs . " sifs\n";

printf STDERR  "Orgs: %s\n", join(", ", keys %genes); 
my $yeast = "Saccharomyces cerevisiae";

my %modelName2ID = %{readModelTable("/cellar/users/cmak/sql-out/model-table/model.txt")};

##
## Compute gene overlaps for yeast genes only
##
if(exists($genes{$yeast}))
{
    my ($size, $numbers, $names) = members_to_numbers($genes{$yeast});
    my (@sifNames, @vectors);
    my $modelName;
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

    my $intersection = 0;
    for my $i (0..$#vectors)
    {
	for my $j (($i+1)..$#vectors)
	{
	    $intersection = $vectors[$i] & $vectors[$j];
	    if(count_bit_vector_members($intersection) > 3)
	    {
		printf("insert into model_similarity (model_id_a, model_id_b, gene_score) values (%s, %s, %d);\n", $sifNames[$i], $sifNames[$j],
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

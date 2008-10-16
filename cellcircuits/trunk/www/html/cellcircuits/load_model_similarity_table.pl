#!/usr/bin/perl -w
use lib './ScoreModel';
#use lib '/cellar/users/pwang/cc_cgi'; # for DB connection
use lib '../../../cgi-bin/cellcircuits'; # for DB connection

use ScoreModel::Publication;
use ScoreModel::EdgeMapper;
use ScoreModel::GeneNameMapper;

use CCDB::DB;

my $dbh = CCDB::DB::getDB();

my $usage=<<USG;
	usage: $0 pub_id
USG

die $usage if (@ARGV != 1);

my $pub_id = shift @ARGV;

#print "pub_id = $pub_id\n";

#my $dataDir = "/cellar/users/pwang/cc_data";
my @pubNames = ($pub_id); # qw(Begley2002_MCR);

my %pubs;

my $gMap = ScoreModel::GeneNameMapper->new();
my $eMap = ScoreModel::EdgeMapper->new();

my @allSIFs;

my %genes;

##
## Read in all of the SIF files
##
my ($p, $pub, $sif, $org2genes, $org, $genes);
foreach $p (@pubNames)
{
    #$pub = Publication->new($p, $dataDir, $gMap, $eMap);
	$pub = ScoreModel::Publication->new($pub_id, $gMap, $eMap, $dbh);
	
	$pubs{$p} = $pub;

    #print "\n### $p\n";
    #print $pub->print();

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

print   "<br>Executing Load_model_similarity_table.pl $pub_id...<br>\n";
print   "Read " . scalar @allSIFs . " sif files<br>\n";
#printf   "Orgs: %s<br>\n", join(", ", keys %genes); 
my $yeast = "saccharomyces cerevisiae";

#my %modelName2ID = %{readModelTable("/cellar/users/pwang/score_model/model.txt")};
my %modelName2ID = %{readModelTable($dbh, $pub_id)}; # get data from DB

#printHash(\%modelName2ID);

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
			#print "modelName = $modelName\n";
			if(exists($modelName2ID{$modelName}))
			{
				push @sifNames, $modelName2ID{$modelName};
				push @vectors, hash_set_to_bit_vector($sif->org2genes()->{$yeast},
								  $numbers);
			}
			else
			{
				print "ERROR: SIF not in database: $modelName\n";
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
				my $model_id_a = $sifNames[$i];
				my $model_id_b = $sifNames[$j];
				my $value = count_bit_vector_members($intersection);
				my $dbQuery = "insert into model_similarity (model_id_a, model_id_b, gene_score) values ($model_id_a, $model_id_b, $value)";
					   
				#print $dbQuery, "\n";
				#load table model_similarity
				my $load_model_similarity_sth = $dbh->prepare($dbQuery);
				$load_model_similarity_sth->execute();
			}
		}
    }
}


sub readModelTable
{
    my ($dbh, $pub_id) = @_;

    my %model;
   
	my $get_model_sth = $dbh->prepare("select * from model where pub = ?");
	$get_model_sth->bind_param(1, $pub_id);
	$get_model_sth->execute();
	
	while (my $ref = $get_model_sth->fetchrow_hashref()) {	
		my $id = $ref->{'id'}; 
		my $pub = $ref->{'pub'};
		my $name = $ref->{'name'};
		$model{join(":", $pub, $name)} = $id;
	}

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


# print hash table, for debug only
sub printHash  {
    my %theHash = %{$_[0]};

	my @keys = keys %theHash;
	my $count = @keys;
	print "Number of items in hash= $count\n";	
	
	while ( my ($key, $value) = each(%theHash) ) {
		if (ref($value) eq 'HASH') {
			print "$key\n";
			foreach my $k (keys %$value) {
					print "\t".$k."=>".$value->{$k}."\n";
			}
			print "\n";	
		}
		else {
			print "$key => $value\n";
		}
	}	
}


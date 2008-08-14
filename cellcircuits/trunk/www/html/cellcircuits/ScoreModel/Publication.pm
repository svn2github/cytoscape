package ScoreModel::Publication;

use ScoreModel::Object;
use ScoreModel::MultiOrganismSIF;

@ISA = qw(ScoreModel::Object);

ScoreModel::Publication->_generateAccessors(qw(sifOrgMap sifs name));

sub new
{
    my ($caller, $name, $geneNameMapper, $edgeMapper, $dbh) = @_; #name is publication_id
    my $self = $caller->SUPER::new();

    $self->sifOrgMap({});
    $self->name($name);
    $self->sifs({});

    if($self->readSifList($name, $dbh))
    {
		#print $self->print();
		#printHash($self->sifOrgMap());
		$self->parseSIFs($dbh, $name, $geneNameMapper, $edgeMapper);
    }
    return $self;
}


# Replace the old one, get the data from DB, instead of sifList file
sub readSifList {
	#print "Entering publication:readSifList() ...\n";
    my ($self, $pub_id,$dbh) = @_;
	my %sifListMap =();

	my $get_model_sth = $dbh->prepare("select distinct species,network_file_id from network_file_info where publication_id = ?");
	$get_model_sth->bind_param(1, $pub_id);
	$get_model_sth->execute();

	while (my $ref = $get_model_sth->fetchrow_hashref()) {	
		#my $id = $ref->{'model_id'}; # model.id
		#my $name = $ref->{'name'}; # model.name
		my $network_file_id = $ref->{'network_file_id'};
		my $species = $ref->{'species'};
		my @orgs = split ",", $species;
		
		$self->sifOrgMap()->{$network_file_id} = \@orgs;
		#print "network_file_id = $network_file_id\n";
		#print "orgs = @orgs\n";		
	}
	
	return 1;
}


sub parseSIFs
{
    my ($self, $dbh, $pub_id, $geneNameMapper, $edgeMapper) = @_;
    #my $dir = $self->dataDir();

    my ($network_file_id, $orgs, $name);
    while(($network_file_id, $orgs) = each(%{$self->sifOrgMap()}))
    {
		#print "parseSIFs:  $network_file_id,", @{$orgs},"\n";
		my $sif = ScoreModel::MultiOrganismSIF->new($dbh, $network_file_id, $orgs, $geneNameMapper, $edgeMapper);

		#my $sif = MultiOrganismSIF->new(join("/", $dir, "sif", $file),
		#			$orgs,
		#			$geneNameMapper,
		#			$edgeMapper);

		#if($file =~ /(.*)\.sif$/)
		#{
		#	$sif->name($1);
		#}
		#else
		#{
		#	$sif->name($file);
		#}
		
		$sif->pub($self->name()); # name is pub_id
		#$self->sifs()->{$file} = $sif;
		#my $model_name = $sif->name();
		#print "model_name =", $model_name,"\n";
		$self->sifs()->{$network_file_id} = $sif;
    }
}

sub print
{
    my ($self) = @_;

    my $str = "";
    my $sifs = $self->sifs();
    my ($sifSummary);
    foreach my $file (sort keys %{$self->sifOrgMap()})
    {
		$sifSummary = "";
		if(exists($sifs->{$file}))
		{
			$sifSummary = $sifs->{$file}->printSummary();
		}
		$str .= sprintf("%s: %s\n", $file, $sifSummary);
    }
    return $str;
}



1;

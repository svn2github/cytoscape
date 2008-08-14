package ScoreModel::GOGeneMap;

#
# Translate External ID's into GO database internal ids
# 

sub new
{
    my ($caller, $dbh, $species) = @_; # species = 'human' or 'yeast'
    my $class = ref($caller) || $caller;
    my $self = bless({}, $class);
    
    $self->parseFile($dbh, $species);

    return $self;
}

sub parseFile # get the data from DB, not file now
{
    my ($self, $dbh, $species) = @_;

	my $dbQuery = "";
	if ($species eq "human") {
		$dbQuery = "
			 select
			 gene_product.id as id,
			 gene_product.symbol as symbol,
			 gene_product.species_id as sid,
			 dbxref.xref_key as xref_key,
			 dbxref.xref_dbname as xref_dbname
			from gene_product, dbxref, species
			where
			 gene_product.dbxref_id = dbxref.id AND
			 gene_product.species_id = species.id AND
			 species.genus = 'Homo' AND
			 species.species = 'sapiens' ";
	}
	elsif ($species eq "yeast") {
		$dbQuery = "select
			 gene_product.id as id,
			 gene_product.symbol as symbol,
			 gene_product.species_id as sid,
			 gene_product_synonym.product_synonym as xref_key,
			 dbxref.xref_dbname as xref_dbname
			from gene_product, gene_product_synonym, dbxref, species
			where
			 gene_product.dbxref_id = dbxref.id AND
			 gene_product.species_id = species.id AND
			 gene_product.id = gene_product_synonym.gene_product_id AND
			 species.genus = 'Saccharomyces' AND
			 species.species = 'cerevisiae' ";
	}

	my $get_data_sth = $dbh->prepare($dbQuery);
	$get_data_sth->execute();

    my %xref2symbol;
    my %xref2go;
    my %symbol2go;
    my $x = 0;

	while (my $ref = $get_data_sth->fetchrow_hashref()) {	
		my ($go, $symbol, $species, $xref, $db);
		$go = $ref->{'id'};
		$symbol = $ref->{'symbol'};
		$species = $ref->{'sid'};
		$xref = $ref->{'xref_key'};
		$db = $ref->{'xref_dbname'};

		$xref2symbol{$xref} = $symbol;
		$xref2go{$xref} = $go;
		$symbol2go{$symbol} = $go;
		$x++;

	}

    $self->{xref2symbol} = \%xref2symbol;
    $self->{xref2go} = \%xref2go;
    $self->{symbol2go} = \%symbol2go;

    printf STDERR "### %s: read %d mappings\n", $species, $x;
}



sub existsXref
{
    my ($self, $id) = @_;
    return exists($self->{xref2go}->{$id});
}


sub existsSymbol
{
    my ($self, $id) = @_;
    return exists($self->{symbol2go}->{$id});
}


sub xref2symbol
{
    my ($self, $id) = @_;
    return $self->{xref2symbol}->{$id};
}


sub xref2go
{
    my ($self, $id) = @_;
    return $self->{xref2go}->{$id};
}


sub symbol2go
{
    my ($self, $id) = @_;
    return $self->{symbol2go}->{$id};
}


1;

#! /usr/bin/perl
use strict;

use lib '.'; # for the lib ScoreModel
#use lib '/cellar/users/pwang/cc_cgi'; # for DB connection
use lib '../../../cgi-bin/cellcircuits'; # for DB connection

use CCDB::DB;

my $usage=<<USG;

\tusage: $0 <pub_id>

USG
die $usage if(@ARGV != 1);

my $pub_id = shift @ARGV;

my $dbh = CCDB::DB::getDB();

my $start = time;

# get the species from DB for the given publication
my $speciesRef = getSpecies($dbh, $pub_id);
my @species = @$speciesRef;

print "Species for this publication\n";
foreach my $theSpecies (@species) {
	print "\t",$theSpecies, "\n";

}

#my @species = ();
#push (@species, 'Saccharomyces cerevisiae');
#push (@species, 'Drosophila melanogaster');
#push (@species, 'Caenorhabditis elegans');
#push (@species, 'Homo sapiens');
#push (@species, 'Plasmodium falciparum');

# run PHP script "getGeneProductID" to generate a temperatory file "_tmpGeneProductIDs.txt"
#my $cmd = "php getGeneProductID.php $pub_id";
#exec $cmd;

# open the file to get the gene_product_ids
#open(GENE_PRODUCT_IDS, "./_tmpGeneProductIDs.txt") || die("Could not open file _tmpGeneProductIDs.txt!\n");
#my @gene_product_ids = ();
#while( defined(my $line = <GENE_PRODUCT_IDS>)) {
#	chomp($line);
#	push (@gene_product_ids, $line);
#}
#close(GENE_PRODUCT_IDS);

# build a hash map for all the tacc, queried from gene_product_ids
#my %tacc_map;
#foreach my $gene_product_id (@gene_product_ids) {
	#print "gene_product_id = $gene_product_id\n";
#	my @tacc = get_tacc($dbh, $gene_product_id);
	#print "tacc= @tacc\n";

#	foreach my $tmpItem (@tacc) {
		#print "tmpItem = $tmpItem\n";
#		$tacc_map{$tmpItem} = "A";
#	}
#} 

#while ( my ($key, $value) = each(%tacc_map) ) {
#	print "key = $key\n";
#}


open F, "> gwt_file.txt" or die "Cannot open gwt_file.txt: $!\n";

foreach my $one_species (@species) {
	print "Exporting gene_with_terms data for $one_species\n";
	
	my ($genus, $species) = split " ", $one_species;
	
	my $get_terms = qq{
	SELECT DISTINCT
	 term.id             as tid,
	 term.acc            as tacc,
	 term.name           as tname,
	 term.term_type      as ttype,
	 species.id          as sid
	FROM gene_product, association, graph_path, term, species
	WHERE
	 gene_product.species_id     = species.id AND
	 association.gene_product_id = gene_product.id AND
	 graph_path.term2_id         = association.term_id AND
	 graph_path.term1_id         = term.id AND
	 species.genus               = ? AND
	 species.species             = ? AND
	 term.acc like 'GO:%' 
	 ORDER BY term.acc 
	};

	
	my $get_terms_STH = $dbh->prepare($get_terms);
	my $species_id_term_id = {};		
		my ($genus,$species) = split(/\s+/,$one_species);

		$get_terms_STH->bind_param(1,$genus,  {TYPE=>12});

		$get_terms_STH->bind_param(2,$species,{TYPE=>12});
				
      	#printf  "   " . $dbh->{Statement} . "\n";

		$get_terms_STH->execute();

		while(my $Ref = $get_terms_STH->fetchrow_hashref())
		{			
			## continue if duplicate
			if(exists $species_id_term_id->{$Ref->{sid}}{$Ref->{tid}})
			{
				next;
			}
		
			$species_id_term_id->{$Ref->{sid}}{$Ref->{tid}}++;
			my $term_id    = $Ref->{tid};
			my $term_acc   = $Ref->{tacc};
			my $term_name  = $Ref->{tname};
			my $term_type  = $Ref->{ttype};
			my $species_id = $Ref->{sid};
		
			#if (exists $tacc_map{$term_acc}) {
				my $genes_with_term  = get_genes_beneath($dbh, $genus,$species,$term_id);
				my $n_genes_beneath = scalar(keys %{$genes_with_term});
			
				printf F "$term_acc\t$n_genes_beneath\t$one_species\n",;
			#}
		}
}

close F;

print "\nTime elapsed: = ", (time - $start)," seconds\n";

sub get_genes_beneath
{
    my ($dbh,$genus, $species, $term_id) = @_;

	my $get_genes_at_or_beneath_term_by_id = qq{
	SELECT DISTINCT
	 gene_product.id     as gene_product_id,
	 gene_product.symbol as symbol,
	 term.acc            as term_acc,
	 term.name           as term_name,
	 term.term_type      as term_type,
	 species.genus       as genus,
	 species.species     as species
	FROM gene_product, association, graph_path, term, species
	WHERE
	 gene_product.species_id     = species.id AND
	 association.gene_product_id = gene_product.id AND
	 graph_path.term2_id         = association.term_id AND
	 graph_path.term1_id         = term.id AND
	 species.genus               = ? AND
	 species.species             = ? AND
	 term.id                     = ?
	};
	
	my $get_genes_at_or_beneath_term_by_id_STH =
		$dbh->prepare($get_genes_at_or_beneath_term_by_id);

    $get_genes_at_or_beneath_term_by_id_STH->bind_param(1, $genus,   {TYPE=>12});
    $get_genes_at_or_beneath_term_by_id_STH->bind_param(2, $species, {TYPE=>12});
    $get_genes_at_or_beneath_term_by_id_STH->bind_param(3, $term_id, {TYPE=>4});
    $get_genes_at_or_beneath_term_by_id_STH->execute();

    my $genes_with_term = ();
    while(my $Ref = $get_genes_at_or_beneath_term_by_id_STH->fetchrow_hashref)
    {
		unless(exists $genes_with_term->{ $Ref->{gene_product_id} })
		{
			$genes_with_term->{ $Ref->{gene_product_id} } = $Ref->{symbol};
		}
    }
    
    return $genes_with_term;
}


sub getSpecies($dbh, $pub_id) {
    my ($dbh, $pub_id) = @_;

	my $dbQuery = "SELECT distinct species FROM network_file_info where publication_id = $pub_id";
	my $get_species_STH = $dbh->prepare($dbQuery);
    $get_species_STH->execute();

    my %species;
	while (my $ref = $get_species_STH->fetchrow_hashref()) {
		my $species = $ref->{'species'}; 		
		my @tmpItems = split(/,/,$species);
		my $count = @tmpItems;
		
		foreach my $item (@tmpItems) {
			$species{$item} = "A";
		}
	}
	
	my @species = ();
	
	while( my ($k, $v) = each %species ) {
		push (@species, ucfirst($k));
    }

	return \@species;
}


sub get_tacc {
	my ($dbh, $gene_product_id) = @_;
	
	my @acc = ();
	
	my $dbQuery = "SELECT term.acc as acc FROM association, term WHERE association.term_id = term.id AND association.gene_product_id = $gene_product_id";
	#print "dbQuery = \n$dbQuery\n";
	
	my $sth = $dbh->prepare($dbQuery);
			
    $sth->execute();

	while (my $ref = $sth->fetchrow_hashref()) {		
		push (@acc, $ref->{'acc'}); 		
	}
	return @acc;
}


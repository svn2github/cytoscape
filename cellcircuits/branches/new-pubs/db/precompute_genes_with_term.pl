#!/usr/bin/perl -w

use strict;

my $db       = 'cellcircuits_dev';
die "usage: $0 <db-name>\nexample: $0 $db" if(@ARGV != 1);
$db = shift;

use DBI;

my $server   = 'localhost';
my $username = 'mdaly';
my $password = 'mdalysql';
my $dbh = DBI->connect("dbi:mysql:$db", $username, $password);

my @species = (
	       'Saccharomyces cerevisiae',
	       'Drosophila melanogaster',
	       'Caenorhabditis elegans',
	       'Homo sapiens',
	       'Plasmodium falciparum',
	       );

my $get_genes_at_or_beneath_term_by_name = qq{
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
 term.name                   = ?
};
my $get_genes_at_or_beneath_term_by_name_STH =
    $dbh->prepare($get_genes_at_or_beneath_term_by_name);

my $get_genes_at_or_beneath_term_by_acc = qq{
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
 term.acc                     = ?
};
my $get_genes_at_or_beneath_term_by_acc_STH =
    $dbh->prepare($get_genes_at_or_beneath_term_by_acc);

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
 species.species             = ?
};
my $get_terms_STH = $dbh->prepare($get_terms);

my $species_id_term_id = {};
print "species_id\tgenus\tspecies\tterm_id\tterm_type\tterm_acc\tterm_name\t"
    . "n_genes_beneath_term\tgene_product_ids_of_genes_beneath_term\n";
foreach my $org (@species){
    my ($genus,$species) = split(/\s+/,$org);
    $get_terms_STH->bind_param(1,$genus,  {TYPE=>12});
    $get_terms_STH->bind_param(2,$species,{TYPE=>12});
    $get_terms_STH->execute();

    while(my $Ref = $get_terms_STH->fetchrow_hashref())
    {
	
	next if($Ref->{tacc} eq "all");
	
	## continue if duplicate
	if(exists $species_id_term_id->{$Ref->{sid}}{$Ref->{tid}})
	{
	    #print STDERR "duplicate...  moving on...\n";
	    next;
	}

	$species_id_term_id->{$Ref->{sid}}{$Ref->{tid}}++;
	my $term_id    = $Ref->{tid};
	my $term_acc   = $Ref->{tacc};
	die "ERROR: tacc doesnt match regex\n" if($term_acc !~ /^GO:\d{7}/);
	my $term_name  = $Ref->{tname};
	my $term_type  = $Ref->{ttype};
	my $species_id = $Ref->{sid};

	my $genes_with_term  = get_genes_beneath($get_genes_at_or_beneath_term_by_id_STH,
						 $genus,$species,$term_id);

	printf "$species_id\t$genus\t$species\t$term_id\t$term_type\t$term_acc\t$term_name\t%s\t%s\n", 
	scalar(keys %{$genes_with_term}), join "\t", keys %{$genes_with_term};

    }

}


sub get_genes_beneath
{
    my ($sth,$genus, $species, $term_id) = @_;

    $sth->bind_param(1, $genus,   {TYPE=>12});
    $sth->bind_param(2, $species, {TYPE=>12});
    $sth->bind_param(3, $term_id, {TYPE=>4});
    $sth->execute();

    my $genes_with_term = ();
    while(my $Ref = $sth->fetchrow_hashref)
    {
	
	unless(exists $genes_with_term->{ $Ref->{gene_product_id} })
	{

	    $genes_with_term->{ $Ref->{gene_product_id} } = $Ref->{symbol};

	}

    }
    
    return $genes_with_term;
}

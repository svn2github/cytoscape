package CCDB::Sql;

require Exporter;

our @ISA       = qw(Exporter);
our @EXPORT    = qw($get_gene_product_id_from_em_symbol
		    $get_gene_product_id_from_re_symbol
		    $get_gene_product_id_from_em_synonym
		    $get_gene_product_id_from_re_synonym
		    $get_from_fulltext_term_name
		    $get_from_term_accession
		    $get_from_gene_product_id
		    $get_from_eid 
		    $get_from_model_id
		    $get_model_like
		    $get_species
		    $get_species_cache
		    ); #symbols to export by default
our @EXPORT_OK = qw(); #symbols to export on request
our $VERSION   = 1.00;

####################### INPUT := gene name (symbol or synonym) #######################

my $gid_from_sym = qq{
SELECT
 gene_product.id          as gid,
 gene_product.symbol      as symbol,
 gene_product.species_id  as sid
FROM
 gene_product, gene_model
WHERE
 gene_product.id = gene_model.gene_product_id AND
 gene_product.symbol
};
our $get_gene_product_id_from_em_symbol = $gid_from_sym . "= ?";
our $get_gene_product_id_from_re_symbol = $gid_from_sym . "regexp ?";

my $gid_from_syn = qq{
SELECT
 gene_product_synonym.gene_product_id         as gid,
 gene_product_synonym.product_synonym         as synonym,
 gene_product_synonym.symbol                  as symbol,
 gene_product_synonym.species_id              as sid
FROM
 gene_product_synonym, gene_model
WHERE
 gene_product_synonym.gene_product_id = gene_model.gene_product_id AND
 gene_product_synonym.product_synonym 
};
our $get_gene_product_id_from_em_synonym = $gid_from_syn . "= ?";
our $get_gene_product_id_from_re_synonym = $gid_from_syn . "REGEXP ?";

our $get_model_like = qq{
SELECT
 model_id_a,
 model_id_b,
 gene_score
FROM
 model_similarity
WHERE
 model_id_a = ? OR
 model_id_b = ?
ORDER BY gene_score DESC
LIMIT 19
};

my $obj_data_fields = qq{
SELECT DISTINCT
 enrichment.id                         as e_id,
 enrichment.n_genes_in_model_with_term as e_n,
 enrichment.n_genes_in_model           as e_k,
 enrichment.n_genes_with_term          as e_m,
 enrichment.n_genes_in_GO              as e_N,
 enrichment.pval                       as e_pval,
 enrichment.gene_ids                   as e_gids,
 enrichment.species_id                 as sid,
 model.id                              as mid,
 model.pub                             as mpub,
 model.name                            as mname,
 term.id                               as tid,
 term.acc                              as tacc,
 term.name                             as tname,
 term.term_type                        as ttype
};

my $obj_data_from_where = $obj_data_fields . qq{
FROM 
 enrichment, model, term
WHERE
 enrichment.model_id   = model.id AND
 enrichment.term_id    = term.id AND
};

our $get_from_fulltext_term_name  = $obj_data_from_where . " enrichment.pval < ? AND MATCH(term.name) AGAINST( ? IN BOOLEAN MODE)";
our $get_from_term_accession      = $obj_data_from_where  . " enrichment.pval < ? AND term.acc = ?";

our $get_from_eid                 = $obj_data_from_where . "enrichment.id = ?";
our $get_from_model_id            = $obj_data_from_where . "enrichment.model_id = ? ORDER BY enrichment.pval LIMIT ?";


my $obj_data_from_where_byGene = $obj_data_fields . qq{
FROM 
 gene_model, enrichment, model, term
WHERE
 gene_model.model_id   = enrichment.model_id AND
 enrichment.model_id   = model.id AND
 enrichment.term_id    = term.id AND
};

our $get_from_gene_product_id     = $obj_data_from_where_byGene . " enrichment.pval < ? AND gene_model.gene_product_id = ?";

our $get_species_cache = qq{
SELECT id, genus, species 
FROM species
WHERE 
  (genus = 'Saccharomyces' and species = 'cerevisiae') OR
  (genus = 'Plasmodium' and species = 'falciparum') OR
  (genus = 'Drosophila' and species = 'melanogaster') OR
  (genus = 'Homo' and species = 'sapiens') OR
  (genus = 'Caenorhabditis' and species = 'elegans')
};


our $get_species = qq{
SELECT id, genus, species 
FROM species
WHERE 
  species.id = ?
};


#######                               #######
#######   BELOW ARE EXAMPLE QUERIES   #######
#######                               #######

#SELECT DISTINCT enrichment.id as e_id, enrichment.pval as e_pval, species.id as sid, species.genus as genus, species.species as species, model.id as mid, model.pub as mpub, model.name as mname, term.id as tid, term.acc as tacc, term.name as tname, term.term_type as ttype FROM gene_product, species, model, gene_model, enrichment, term WHERE gene_product.id = gene_model.gene_product_id AND gene_model.model_id = model.id AND model.id = enrichment.model_id AND enrichment.term_id = term.id AND enrichment.species_id = species.id AND enrichment.pval  < 1e-4 AND MATCH(term.name) AGAINST('"DNA binding" replication' IN BOOLEAN MODE);


# this gives models that have genes that dont have an associated function...
# essentially it gives genes that are in models and are in the gene_product table
# but have no entries in the enrichment table
my $left_join_testing = qq{
SELECT *
FROM
 gene_model 
  JOIN 
 model ON (model.id = gene_model.model_id)
  JOIN
 gene_product ON (gene_model.gene_product_id = gene_product.id)
WHERE model.id in (68,86);
};

1;

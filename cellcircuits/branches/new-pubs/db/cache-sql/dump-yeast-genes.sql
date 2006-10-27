select
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
 species.species = 'cerevisiae'

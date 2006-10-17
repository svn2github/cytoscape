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
 species.species = 'sapiens'
into outfile "/cellar/users/cmak/sql-out/human-genes.txt";

select *
from gene_product, dbxref
where
  gene_product.dbxref_id = dbxref.id AND
  dbxref.xref_key = 'Q16584';


select *
from gene_product, dbxref
where
  gene_product.dbxref_id = dbxref.id AND
  dbxref.xref_key = 'XP_042066';

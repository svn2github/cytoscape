alter table gene_product_synonym
add column symbol varchar(128),
add column species_id int(11);

update gene_product_synonym, gene_product  
set 
  gene_product_synonym.symbol = gene_product.symbol, 
  gene_product_synonym.species_id = gene_product.species_id 
where gene_product_synonym.gene_product_id = gene_product.id;

create index gps_symbol on gene_product_synonym(symbol);
create index gps_species_id on gene_product_synonym(species_id);

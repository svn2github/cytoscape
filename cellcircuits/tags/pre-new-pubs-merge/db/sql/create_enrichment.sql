DROP TABLE IF EXISTS enrichment;
CREATE TABLE enrichment (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	model_id			int NOT NULL,
	species_id			int NOT NULL,
	term_id				int NOT NULL,
	n_genes_in_model_with_term	int,
	n_genes_in_model		int,
	n_genes_with_term		int,
	n_genes_in_GO			int,
	pval				real,
	gene_ids			text,
	UNIQUE INDEX e0 (id,model_id,species_id,term_id),
	UNIQUE INDEX e1 (model_id,species_id,term_id),
	INDEX e2 (species_id,term_id),
	INDEX e3 (term_id)
);

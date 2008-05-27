USE DATABASE cellcircuits;

DROP TABLE IF EXISTS model;
DROP TABLE IF EXISTS gene_model;
DROP TABLE IF EXISTS model_similarity;
DROP TABLE IF EXISTS enrichment;

CREATE TABLE model (
        id      int NOT NULL PRIMARY KEY AUTO_INCREMENT,
        pub     varchar(255) NOT NULL,
        name    varchar(255) NOT NULL,
	UNIQUE INDEX model_idx (id,pub),
	INDEX m0 (pub)
);

CREATE TABLE gene_model (
        id              int NOT NULL PRIMARY KEY AUTO_INCREMENT,
        model_id        int,
        gene_product_id int,
	UNIQUE INDEX gm0 (id,model_id,gene_product_id),
	UNIQUE INDEX gm1 (model_id,gene_product_id),
	INDEX gm2 (gene_product_id)
);

CREATE TABLE model_similarity (
        id      	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
        model_id_a     	int NOT NULL,
        model_id_b     	int NOT NULL,
	gene_score     	int NOT NULL,
	INDEX ms1 (model_id_a),	
	INDEX ms2 (model_id_b),
	UNIQUE INDEX ms3 (model_id_a, model_id_b),
	FOREIGN KEY (model_id_a) REFERENCES model(id),
	FOREIGN KEY (model_id_b) REFERENCES model(id)
);

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

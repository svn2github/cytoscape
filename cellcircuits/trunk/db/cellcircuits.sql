USE cellcircuits_dev;

DROP TABLE IF EXISTS submission_data;
DROP TABLE IF EXISTS raw_data;
DROP TABLE IF EXISTS cover_image_files;
DROP TABLE IF EXISTS publications;
DROP TABLE IF EXISTS pdf_files;
DROP TABLE IF EXISTS file_info;
DROP TABLE IF EXISTS network_files;
DROP TABLE IF EXISTS image_files;
DROP TABLE IF EXISTS legends;
DROP TABLE IF EXISTS model;
DROP TABLE IF EXISTS gene_model;
DROP TABLE IF EXISTS model_similarity;
DROP TABLE IF EXISTS enrichment;


CREATE TABLE submission_data (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	pmid			int, -- PuMed id
	contact_person	varchar(100),
	email			varchar(100),
	comment			text,
	cover_image_id	int,
	pdf_file_id		int,
	zip_file_id		int
);

CREATE TABLE raw_data (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(100),
	data			blob
);

CREATE TABLE cover_image_files (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(100),
	data			blob
);

CREATE TABLE pdf_files (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(100),
	data			blob
);

CREATE TABLE publications (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	pmid			int, -- PubMed ID
	status_load		boolean,
	cover_image_id	int,	
	pdf_file_id		int,
	journal_name	varchar(100),
	authors			varchar(120),
	vol				varchar(20),
	issue			varchar(10),
	page_start		varchar(8),
	page_end		varchar(8),
	year			int,
	month			int
);

CREATE TABLE file_info (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	pub_id			int,
	network_file_id		int,
	network_type		varchar(10),
	large_image_id		int,
	thm_image_id		int
);

CREATE TABLE network_files (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(99),
	data			blob
);

CREATE TABLE image_files (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(99),
	is_thm			boolean,
	data			blob
);

CREATE TABLE legends (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	pub_id			int,
	file_name		varchar(40),
	data			blob
);

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
	model_id		int NOT NULL,
	species_id		int NOT NULL,
	term_id			int NOT NULL,
	n_genes_in_model_with_term	int,
	n_genes_in_model	int,
	n_genes_with_term	int,
	n_genes_in_GO		int,
	pval				real,
	gene_ids			text,
	UNIQUE INDEX e0 (id,model_id,species_id,term_id),
	UNIQUE INDEX e1 (model_id,species_id,term_id),
	INDEX e2 (species_id,term_id),
	INDEX e3 (term_id)
);

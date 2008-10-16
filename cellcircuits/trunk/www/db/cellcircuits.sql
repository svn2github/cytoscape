DROP TABLE IF EXISTS submission_data;
DROP TABLE IF EXISTS raw_files;
DROP TABLE IF EXISTS publications;
DROP TABLE IF EXISTS supplement_material_files;
DROP TABLE IF EXISTS cover_image_files;
DROP TABLE IF EXISTS pdf_files;
DROP TABLE IF EXISTS network_file_info;
DROP TABLE IF EXISTS network_files;
DROP TABLE IF EXISTS network_image_files;
DROP TABLE IF EXISTS network_thum_image_files;
DROP TABLE IF EXISTS legend_files;
DROP TABLE IF EXISTS usagelog;
DROP TABLE IF EXISTS model;
DROP TABLE IF EXISTS gene_model;
DROP TABLE IF EXISTS model_similarity;
DROP TABLE IF EXISTS enrichment;

-- data collected from submission form
CREATE TABLE submission_data (
	raw_data_auto_id	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	pmid				int, -- PuMed id
	pubmed_xml_record	text,
	contact_person		varchar(100),
	email				varchar(100),
	data_file_id		int default -1, -- .zip file
	comment				text default "",
	status				varchar(15) default 'new',
	time_stamp			timestamp default CURRENT_TIMESTAMP
);

-- All data files (.zip) submitted by end users
CREATE TABLE raw_files (
	raw_file_auto_id	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(200),
	file_type		varchar(100),
	data			longblob
);

-- data transferd from submission data
CREATE TABLE publications (
	publication_auto_id	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	rawdata_id			int	default -1,
	pmid				int, -- PubMed ID
	pubmed_xml_record	text,
	pubmed_html_full		text, -- converted HTML text from XML
	pubmed_html_medium		text, -- converted HTML text from XML
	pubmed_html_short		text, -- converted HTML text from XML
	pubmed_html_advsearch   text, -- for advanced search page
	pub_url				varchar(200  ) default '',	
	supplement_file_id	int default -1,
	supplement_url	varchar(150) default 'none',
	-- network_image_format varchar(10), -- sif, jpg, png
	cover_image_id		int,	
	pdf_file_id			int default -1,
	time_stamp			timestamp default CURRENT_TIMESTAMP	
);

-- data extracted from the zip file submitted (from table raw_files)
CREATE TABLE supplement_material_files (
	id	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(200),
	file_type		varchar(100),
	data			longblob
);


CREATE TABLE cover_image_files (
	cover_image_file_auto_id	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(200),
	file_type		varchar(100),
	data			mediumblob
);

CREATE TABLE pdf_files (
	pdf_file_auto_id	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(200),
	file_type		varchar(100),
	data			longblob
);

CREATE TABLE network_file_info ( 
	id	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	publication_id		int,
	-- genus				varchar(50) default 'unknown', -- 'Saccharomyces'
	species				varchar(100) default 'unknown', -- 'Saccharomyces cerevisiae, Homo sapients'
	network_type		varchar(10) default 'sif',
	network_file_id		int,
	image_file_id		int,
	thum_image_file_id		int
);

CREATE TABLE network_files (
	id	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(200),
	file_type		varchar(100),
	data			longblob
);

CREATE TABLE network_image_files (
	id	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(200),
	file_type		varchar(100),
	data			longblob
);

CREATE TABLE network_thum_image_files (
	id	int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	file_name		varchar(200),
	file_type		varchar(100),
	data			longblob
);


CREATE TABLE legend_files (
	id				int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	pub_id			int,
	file_name		varchar(200),
	file_type		varchar(100) default 'unknown',
	data			longblob
);

CREATE TABLE usagelog (
  id int not null PRIMARY KEY auto_increment,
  remote_host varchar(60) default NULL,
  ip_address varchar(20) default NULL,
  query_str varchar(120),
  refer_page varchar(99),
  sysdat date default NULL
);


-- Original four tables 

CREATE TABLE model (
 	id      int NOT NULL PRIMARY KEY AUTO_INCREMENT,
 	pub     varchar(255) NOT NULL, -- value will be pmid from table publications, which is unique, 
 									-- to replace old manually made value, such as 'Begley2002_MCR'
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

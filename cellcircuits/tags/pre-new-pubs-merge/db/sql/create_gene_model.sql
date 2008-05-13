DROP TABLE IF EXISTS gene_model;
CREATE TABLE gene_model (
        id              int NOT NULL PRIMARY KEY AUTO_INCREMENT,
        model_id        int,
        gene_product_id int,
	UNIQUE INDEX gm0 (id,model_id,gene_product_id),
	UNIQUE INDEX gm1 (model_id,gene_product_id),
	INDEX gm2 (gene_product_id)
);

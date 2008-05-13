DROP TABLE IF EXISTS model;
CREATE TABLE model (
        id      int NOT NULL PRIMARY KEY AUTO_INCREMENT,
        pub     varchar(255) NOT NULL,
        name    varchar(255) NOT NULL,
	UNIQUE INDEX model_idx (id,pub),
	INDEX m0 (pub)
);

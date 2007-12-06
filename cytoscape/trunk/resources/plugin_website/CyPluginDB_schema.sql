DROP DATABASE IF EXISTS cyplugindb;
CREATE DATABASE cyplugindb;

USE cyplugindb;

CREATE TABLE authors (
  author_auto_id int(11) NOT NULL auto_increment,
  names varchar(150) default NULL,
  email varchar(90) default NULL,
  affiliation varchar(150) default NULL,
  affiliationURL varchar(200) default NULL,
  PRIMARY KEY  (author_auto_id)
);



CREATE TABLE categories (
  category_id int(11) NOT NULL,
  name varchar(50) NOT NULL,
  description varchar(100) default NULL,
  PRIMARY KEY  (category_id)
);

INSERT INTO categories VALUES 
(1,'Core','Cytoscape core'),
(2,'Analysis','Used for analyzing existing networks'),
(3,'Network and Attribute I/O','Used for importing networks and attributes in different file formats'),
(4,'Network Inference','Used for inferring new networks'),
(5,'Functional Enrichment','Used for functional enrichment of networks'),
(6,'Communication/Scripting','Used for communicating with or scripting Cytoscape'),
(7,'Other','None of the above');


CREATE TABLE plugin_author (
  plugin_version_id int(11) default NULL,
  author_id int(11) default NULL,
  authorship_seq int(11) default NULL
);


CREATE TABLE plugin_files (
  plugin_file_auto_id int(11) NOT NULL auto_increment,
  file_data longblob,
  file_type enum('jar','zip') default NULL,
  file_name varchar(100) default NULL,
  md5 varchar(40),
  PRIMARY KEY  (plugin_file_auto_id)
);


CREATE TABLE plugin_list (
  plugin_auto_id int(11) NOT NULL auto_increment,
  name varchar(100) NOT NULL,
  unique_id	int,
  description text,
  license text,
  license_required varchar(3) default NULL,
  project_url varchar(100) default NULL,
  category_id int(11) default NULL,
  sysdat date default NULL,
  PRIMARY KEY  (plugin_auto_id)
);


CREATE TABLE plugin_version (
  version_auto_id int(11) NOT NULL auto_increment,
  plugin_id int(11) default NULL,
  plugin_file_id int(11) default NULL,
  cy_version set('2.0','2.1','2.2','2.3','2.4','2.5','2.5.1','2.5.2','2.6','2.6.1','2.6.2') default NULL,
  version double default '0.1',
  release_date date default NULL,
  release_note text,
  release_note_url varchar(100) default NULL,
  minimum_java_version varchar(10),
  comment text,
  jar_url varchar(100) default NULL,
  source_url varchar(100) default NULL,
  status varchar(20) default NULL,
  reference text,
  sysdat date default NULL,
  PRIMARY KEY  (version_auto_id)
);


CREATE TABLE usagelog (
  log_auto_id int not null auto_increment,
  plugin_version_id int(11) NOT NULL,
  remote_host varchar(60) default NULL,
  ip_address varchar(20) default NULL,
  refer_page varchar(99),
  sysdat date default NULL,
  PRIMARY KEY  (log_auto_id)
);


CREATE TABLE contacts (
  contact_auto_id int not null auto_increment,
  name varchar(99),
  email varchar(99),
  plugin_version_id int,   
  sysdat date default NULL,
  PRIMARY KEY  (contact_auto_id)
);

CREATE TABLE theme_list (
  theme_auto_id int(11) NOT NULL auto_increment,
  name varchar(100) NOT NULL,
  unique_id int,
  description text,
  sysdat date,
  PRIMARY KEY (theme_auto_id)
);

 

CREATE TABLE theme_version (
  version_auto_id int(11) NOT NULL auto_increment,
  theme_id int(11) NOT NULL,
  cy_version set('2.6','2.7 ','2.8') default NULL,
  version double default '0.1',
  release_date date default NULL,
  sysdat date default NULL,
  PRIMARY KEY   (version_auto_id)
);

 

CREATE TABLE theme_plugin (
  theme_plugin_auto_id int(11) NOT NULL auto_increment,
  theme_version_id int(11),
  plugin_version_id int(11),
  theme_only ENUM('true', 'false')
);

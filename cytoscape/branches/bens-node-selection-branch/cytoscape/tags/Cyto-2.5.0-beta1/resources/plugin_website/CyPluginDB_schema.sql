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
(1,'Core Plugins','Cytoscape core'),
(2,'Analysis Plugins','Used for analyzing existing networks'),
(3,'Network and Attribute I/O Plugins','Used for importing networks and attributes in different file formats'),
(4,'Network Inference Plugins','Used for inferring new networks'),
(5,'Functional Enrichment Plugins','Used for functional enrichment of networks'),
(6,'Communication/Scripting Plugins','Used for communicating with or scripting Cytoscape'),
(7,'Other Plugins','None of the above');


CREATE TABLE plugin_author (
  plugin_version_id int(11) default NULL,
  author_id int(11) default NULL,
  authorship_seq int(11) default NULL
);


CREATE TABLE plugin_files (
  plugin_file_auto_id int(11) NOT NULL auto_increment,
  file_data mediumblob,
  file_type enum('jar','zip') default NULL,
  file_name varchar(100) default NULL,
  PRIMARY KEY  (plugin_file_auto_id)
);


CREATE TABLE plugin_list (
  plugin_auto_id int(11) NOT NULL auto_increment,
  name varchar(100) NOT NULL,
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
  cy_version set('2.0','2.1','2.2','2.3','2.4','2.5') default NULL,
  version double default '0.1',
  release_date date default NULL,
  release_note text,
  release_note_url varchar(100) default NULL,
  comment text,
  jar_url varchar(100) default NULL,
  source_url varchar(100) default NULL,
  status varchar(20) default NULL,
  reference text,
  sysdat date default NULL,
  PRIMARY KEY  (version_auto_id)
);


CREATE TABLE usagelog (
  plugin_version_id int(11) NOT NULL,
  remote_host varchar(60) default NULL,
  ip_address varchar(20) default NULL,
  sysdat date default NULL,
  PRIMARY KEY  (plugin_version_id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


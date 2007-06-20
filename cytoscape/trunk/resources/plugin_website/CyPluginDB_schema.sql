/*
    Database schema for CyPluginDB    
*/

drop database if exists cyplugindb;
create database cyplugindb;

use cyplugindb; 

create table categories (
	category_id	int not null primary key,
    name      		varchar(50) not null,
    description     varchar(100)    
);

create table plugin_list (
	plugin_auto_id	int not null primary key auto_increment,
  name     		varchar(100) not null,
	description		text,
	license			text,
	license_required	varchar(3),	
	project_url		varchar(100),
	category_id		int,
    sysdat          Date
);

create table plugin_version (
	version_auto_id	int not null primary key auto_increment,
    plugin_id       int,
    plugin_file_id	int,
		cytoscape_version_id	int not null,	
		version			double default 1.0,
		release_date	Date,
		release_note	text,
		release_note_url varchar(100),	
		comment			text,
		jar_url			varchar(100),
		source_url		varchar(100),
		status			varchar(20),
		reference		text,
    sysdat          Date
);

create table cytoscape_version (
	cytoscape_version_auto_id int primary key auto_increment,
	cytoscape_version 	varchar(50)
);

create table plugin_files (
	plugin_file_auto_id	int not null primary key auto_increment,
	file_data		mediumblob, -- up to 16M
	file_type		enum('jar', 'zip'),
	file_name 	varchar(100),
);

create table authors (
	author_auto_id	int not null primary key auto_increment,
    names      		varchar(150),
    email      		varchar(90),
    affiliation		varchar(150),
    affiliationURL	varchar(200)
);

create table plugin_author (
	plugin_version_id	int,
	author_id		int,
	authorship_seq	int
);

create table usagelog (
	plugin_version_id	int not null primary key,
	remote_host 	varchar(60),
	ip_address		varchar(20),
    sysdat          Date
);

grant select, insert, update on cyplugindb.* to cytouser identified by 'cytouser';
grant select, insert, update, delete on cyplugindb.* to cytostaff identified by 'cytostaff';

insert into cytoscape_version (cytoscape_version)
	values('2.0'), ('2.1'), ('2.2'), ('2.3'), ('2.4'), ('2.5');

insert into categories (category_id, name, description) 
	values (1, 'Core Plugins', 'Cytoscape core'),
         (2, 'Analysis Plugins', 'Used for analyzing existing networks'),
         (3, 'Network and Attribute I/O Plugins', 'Used for importing networks and attributes in different file formats'),
         (4, 'Network Inference Plugins', 'Used for inferring new networks'),
         (5, 'Functional Enrichment Plugins', 'Used for functional enrichment of networks'),
         (6, 'Communication/Scripting Plugins', 'Used for communicating with or scripting Cytoscape'),
         (7, 'Other Plugins', 'None of the above');
            
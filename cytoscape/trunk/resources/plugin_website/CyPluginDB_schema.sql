/*
    Database schema for CyPluginDB    
*/

-- drop database cyplugindb;
-- create database cyplugindb;

use cyplugindb; 

drop table if exists categories;
drop table if exists plugin_list;
drop table if exists plugin_version;
drop table if exists authors;
drop table if exists plugin_files;
drop table if exists plugin_author;
drop table if exists usagelog;

create table categories (
	category_id	int not null primary key,
    name      		varchar(50) not null,
    description     varchar(100)    
);

create table plugin_list (
	plugin_auto_id	int not null primary key auto_increment,
    name     		varchar(100) not null,
	description		text,
	license_brief	varchar(200),
	license_detail	text,
	project_url		varchar(100),
	category_id		int,
    sysdat          Date
);

create table plugin_version (
	version_auto_id	int not null primary key auto_increment,
    plugin_id       int,
    plugin_file_id	int,
	version			varchar(20) default 'unknown',
	release_date	Date,
	release_note	text,
	release_note_url varchar(100),	
	comment			text,
	jar_url			varchar(100),
	source_url		varchar(100),	
	cy_version		set('2.0','2.1','2.2','2.3','2.4','2.5'),
	reference		text,
    sysdat          Date
);

create table plugin_files (
	plugin_file_auto_id	int not null primary key auto_increment,
	file_data		mediumblob, -- up to 16M
	file_type		varchar(50),
	file_name 		varchar(50)
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

grant select, insert on cyplugindb.* to cytouser identified by 'cytouser';
grant all on cyplugindb.* to cytoastaff identified by 'cytostaff';

insert into categories (category_id, name, description) 
            values (1, 'Analysis Plugins', 'Used for analyzing existing networks');
insert into categories (category_id,name, description)
            values (2, 'Network and Attribute I/O Plugins', 'Used for importing networks and attributes in different file formats');
insert into categories (category_id,name, description) 
            values (3, 'Network Inference Plugins', 'Used for inferring new networks');
insert into categories (category_id,name, description) 
            values (4, 'Functional Enrichment Plugins', 'Used for functional enrichment of networks');
insert into categories (category_id,name, description) 
            values (5, 'Communication/Scripting Plugins', 'Used for communicating with or scripting Cytoscape');

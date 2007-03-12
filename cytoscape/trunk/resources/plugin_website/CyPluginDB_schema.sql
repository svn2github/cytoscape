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
	version			varchar(20),
	os				set('windows','unix','mac','other'),
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
	blobType		varchar(50),
	blobTitle 		varchar(50)
);

create table authors (
	author_auto_id	int not null primary key auto_increment,
    last_name      	varchar(50),
    first_name      varchar(50),
    middle_name		varchar(50),
    lab				varchar(100),
    lab_url		varchar(100),
    dept			varchar(100),
    dept_url		varchar(100),    
	institution		varchar(200),
    institution_url	varchar(100),
    email      		varchar(50)	
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

grant select on cyplugindb.* to cytoscapeuser identified by 'cytoscapeuser';

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

-- plugin_list
insert into plugin_list (name, description, project_url, category_id) 
            values ('Cerebral Plugin', 'Cerebral (Cell Region-Based Rendering And Layout) is an open-source Java plugin for the Cytoscape biomolecular interaction viewer. Given an interaction network and subcellular localization annotation, Cerebral automatically generates a view of the network in the style of traditional pathway diagrams, providing an intuitive interface for the exploration of a biological pathway or system. The molecules are separated into layers according to their subcellular localization. Potential products or outcomes of the pathway can be shown at the bottom of the view, clustered according to any molecular attribute data - protein function - for example. Cerebral scales well to networks containing thousands of nodes.',
            'http://www.pathogenomics.ca/cerebral/', 1);
insert into plugin_list (name, description, project_url, category_id, license_brief) 
            values ('Hyperbolic Focus', 'Hyperbolic Focus Layout plugin. This plug-in was created by Robert Ikeda, who was supported (partially) by the PRIME Program funded by NSF (OISE 0407508) and Calit2',
            null, 1,'Licensed under GNU Public License');

-- plugin_version          
insert into plugin_version (plugin_id, version, os, release_date,release_note, release_note_url,comment,source_url,jar_url, cy_version)
            values (1,'1.0', 'windows,mac', '2007-02-20',null,'http://www.pathogenomics.ca/cerebral/','not tested on 2.0','http://www.source.com','http://jar_url.edu/','2.3,2.4');
insert into plugin_version (plugin_id, version, os, release_date,release_note, release_note_url,comment,source_url, cy_version)
            values (2,'1.0', null, null,null,null,null,null,'2.4');
          
-- authors  
insert into authors (last_name, first_name, middle_name, lab, lab_url, dept, dept_url, institution, institution_url)
            values ('Gardy','Jennifer', null, null,null, null, null,'University of British Columbia', 'http://ubc.eu');            
insert into authors (last_name, first_name, middle_name, lab, lab_url, dept, dept_url, institution, institution_url)
            values ('Barsky','Aaron', null, null,null, null, null,'University of British Columbia', 'http://ubc.eu');
insert into authors (last_name, first_name, middle_name, lab, lab_url, dept, dept_url, institution, institution_url)
            values (null,null, null, 'Ideker Lab','http://chianti.ucsd.edu/idekerlab/', null, null,'UCSD', null);
            
-- plugin_author  
insert into plugin_author (plugin_version_id, author_id, authorship_seq)
            values (1,1,1);
insert into plugin_author (plugin_version_id, author_id, authorship_seq)
            values (1,2,2);
insert into plugin_author (plugin_version_id, author_id, authorship_seq)
            values (2,3,0);
            
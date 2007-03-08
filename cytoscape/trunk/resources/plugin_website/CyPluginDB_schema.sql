/*
    Database schema for CyPluginDB    
*/

-- drop database cyplugindb;
-- create database cyplugindb;

use cyplugindb; 

drop table if exists categories;
drop table if exists pluginlist;
drop table if exists plugins;
drop table if exists authors;
drop table if exists usagelog;

create table categories (
	id				int not null primary key auto_increment,
    name      		varchar(50) not null,
    description     varchar(100)    
);

create table pluginlist (
	id				int not null primary key auto_increment,
    pluginName      varchar(100) not null,
	description		text,
	licenseInfo		text,
	projectURL		varchar(100),
	categoryID		int,
    sysdat          Date
);

create table plugins (
	id				int not null primary key auto_increment,
    pluginID        int,
	version			varchar(20),
	os				set('windows','unix','mac','other'),
	releaseDate		Date,
	releaseNote		text,
	releaseNoteURL	varchar(100),	
	comment			text,
	jar				BLOB,
	jarURL			varchar(100),
	sourceURL		varchar(100),	
	cyVersion		set('2.0','2.1','2.2','2.3','2.4','2.5'),
	reference		text,
    sysdat          Date
);

create table authors (
	id				int not null primary key auto_increment,
	pluginID		int,
    lastName      	varchar(50),
    firstName      	varchar(50),
    middleName		varchar(50),
    authorshipSeq	int,
    groupDept		varchar(100),
    groupDeptURL	varchar(100),
	institution		text,
    institutionURL	varchar(100),
    email      		varchar(50)	
);

create table usagelog (
	pluginid		int not null primary key,
	remoteHost 	varchar(40),
	ipAddress		varchar(20),
    sysdat          Date
);

grant select on cyplugindb.* to cytoscapeuser identified by 'cytoscapeuser';

insert into categories (name, description) 
            values ('Analysis Plugins', 'Used for analyzing existing networks');
insert into categories (name, description)
            values ('Network and Attribute I/O Plugins', 'Used for importing networks and attributes in different file formats');
insert into categories (name, description) 
            values ('Network Inference Plugins', 'Used for inferring new networks');
insert into categories (name, description) 
            values ('Functional Enrichment Plugins', 'Used for functional enrichment of networks');
insert into categories (name, description) 
            values ('Communication/Scripting Plugins', 'Used for communicating with or scripting Cytoscape');

-- pluginlist
insert into pluginlist (pluginName, description, projectURL, categoryID) 
            values ('Cerebral Plugin', 'Cerebral (Cell Region-Based Rendering And Layout) is an open-source Java plugin for the Cytoscape biomolecular interaction viewer. Given an interaction network and subcellular localization annotation, Cerebral automatically generates a view of the network in the style of traditional pathway diagrams, providing an intuitive interface for the exploration of a biological pathway or system. The molecules are separated into layers according to their subcellular localization. Potential products or outcomes of the pathway can be shown at the bottom of the view, clustered according to any molecular attribute data - protein function - for example. Cerebral scales well to networks containing thousands of nodes.',
            'http://www.pathogenomics.ca/cerebral/', 1);

-- plugins          
insert into plugins (pluginID, version, os, releaseDate,releaseNote, releaseNoteURL,comment,jarURL,sourceURL, cyVersion)
            values (1,'1.0', 'windows,mac', '2007-02-20',null,'http://www.pathogenomics.ca/cerebral/','not tested on 2.0', 'http://asdf.com','http://www.source.com','2.3,2.4');
          
-- authors  
insert into authors (pluginID, authorshipSeq, lastName, firstname, middlename, groupDept, groupDeptURL, institution, institutionURL)
            values (1,1,'Gardy','Jennifer', null, null,null, 'University of British Columbia', null);
insert into authors (pluginID, authorshipSeq, lastName, firstname, middlename, groupDept, groupDeptURL, institution, institutionURL)
            values (1,2,'Barsky','Aaron', null, null,null, 'University of British Columbia', null);
            
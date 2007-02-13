/*
    Database schema for CyPluginDB    
*/

-- drop database cyplugindb;
-- create database cyplugindb;

use cyplugindb; 

drop table if exists pluginlist;
drop table if exists plugins;
drop table if exists authors;
drop table if exists categories;
drop table if exists usagelog;

create table pluginlist (
	id				int not null primary key auto_increment,
    PluginName      varchar(100) not null,
	decription		text,
	license_info	text,
	project_url		varchar(100),
	category		varchar(50),
    sysdat          Date
);

create table authors (
	id				int not null primary key auto_increment,
    lastName      	varchar(20) not null,
    firstName      	varchar(20) not null,
    email      		varchar(30) not null	
);

create table categories (
	id				int not null primary key auto_increment,
    Name      		varchar(50) not null,
    description     varchar(100)    
);

create table plugins (
	id				int not null primary key auto_increment,
    PluginName      varchar(20) not null,
	version			varchar(20),
	OS				varchar(10),
	release_date	Date,
	Jar				BLOB,
	CyVersion2p5	boolean,
    sysdat          Date
);

create table usagelog (
	pluginid		int not null primary key,
	remote_hostost	varchar(40),
	ip_address		varchar(20),
    sysdat          Date
);

grant select on cyplugindb.* to cytoscapeuser identified by 'cytoscapeuser';

insert into categories set Name = 'Analysis Plugins', description='Used for analyzing existing networks';
insert into categories set Name = 'Network and Attribute I/O Plugins', description='Used for importing networks and attributes in different file formats';
insert into categories set Name = 'Network Inference Plugins ', description='Used for inferring new networks';
insert into categories set Name = 'Functional Enrichment Plugins ', description='Used for functional enrichment of networks';
insert into categories set Name = 'Communication/Scripting Plugins', description='Used for communicating with or scripting Cytoscape';

insert into pluginlist set PluginName = 'DataMatrix Plugin', 
	decription='This plugin provides a number of integrated tools for exploring and visualizing experimental data in association with the Cytoscape network view. Read in',
	category = 'Analysis Plugins';
insert into pluginlist set PluginName = 'DomainNetworkBuilder Plugin', 
	decription='This plugin decomposes protein networks into domain-domain interactions. Basically',
	category = 'Analysis Plugins';
insert into pluginlist set PluginName = 'Dynamic Expression Plugin', 
	decription='This plug-in loads an expression data file (consult the Cytoscape manual to learn about ...',
	category = 'Analysis Plugins';

insert into pluginlist set PluginName = 'CyGoose Plugin',
	decription='The CyGoose Cytoscape Plugin gives any network in Cytoscape full access to the Gaggle. The ...',
	category = 'Communication/Scripting Plugins';

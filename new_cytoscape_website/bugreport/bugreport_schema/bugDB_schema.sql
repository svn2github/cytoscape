DROP DATABASE IF EXISTS bugs;
CREATE DATABASE bugs;

USE bugs;

CREATE TABLE bugs (
  bug_auto_id int(11) NOT NULL auto_increment,
  reporter_id int(11) NOT NULL;
  description text NOT NULL,
  remote_host varchar(60) default NULL,
  ip_address varchar(20) default NULL,
  sysdat date default NULL,
  PRIMARY KEY  (bug_auto_id)
);


CREATE TABLE reporter (
  reporter_id int(11) default NULL,
  name varchar(150) default NULL,
  email varchar(90) default NULL,
  PRIMARY KEY  (reporter_auto_id)
);


CREATE TABLE attached_files (
  attached_file_auto_id int(11) NOT NULL auto_increment,
  file_data longblob,
  file_type enum('jar','zip') default NULL,
  file_name varchar(100) default NULL,
  md5 varchar(40),
  PRIMARY KEY  (plugin_file_auto_id)
);


CREATE TABLE bug_file (
  bug_id int(11) default NULL,
  file_id int(11) default NULL,
);


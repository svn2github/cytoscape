DROP DATABASE IF EXISTS bugs;
CREATE DATABASE bugs;

USE bugs;

CREATE TABLE bugs (
  bug_auto_id int(11) NOT NULL auto_increment,
  reporter_id int(11) NOT NULL,
  cyversion varchar(20) NOT NULL,
  os varchar(15) NOT NULL,
  description text NOT NULL,
  remote_host varchar(60) default NULL,
  ip_address varchar(20) default NULL,
  sysdat date default NULL,
  editdat date default NULL,
  PRIMARY KEY  (bug_auto_id)
);


CREATE TABLE reporter (
  reporter_auto_id int(11) NOT NULL auto_increment,
  name varchar(150) default NULL,
  email varchar(90) default NULL,
  PRIMARY KEY  (reporter_auto_id)
);


CREATE TABLE attached_files (
  file_auto_id int(11) NOT NULL auto_increment,
  file_name varchar(100) NOT NULL,
  file_type varchar(100) NOT NULL,
  file_data longblob,
  md5 varchar(40) Default NULL,
  PRIMARY KEY  (file_auto_id)
);


CREATE TABLE bug_file (
  bug_id int(11) NOT NULL,
  file_id int(11) NOT NULL
);


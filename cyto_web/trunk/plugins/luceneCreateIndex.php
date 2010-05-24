<?php
// This script will create Lucene Index "./luceneIndex" for all the plugins in CyPluginDB
// If there is an existing index, the existing one will be overwritten.

// Include the DBMS credentials
include 'db.inc';
include 'dbUtil.inc';
include 'luceneUtil.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
	showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();


createLuceneIndex($connection);

?>
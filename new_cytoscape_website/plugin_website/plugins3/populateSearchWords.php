<?php
// This script will populate the two tables 'description_words' and 'description_word_link' for search

// Include the DBMS credentials
include 'db.inc';
include 'processSearchWords.inc';
include 'dbUtil.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
	showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();

$plugin_id_array = getPluginIDs($connection);

// Clean the tables to make sure it is empty
$dbQuery = 'delete from description_words';
// Run the query
if (!($result = @ mysql_query($dbQuery, $connection)))
	showerror();

$dbQuery = 'delete from description_word_link';
// Run the query
if (!($result = @ mysql_query($dbQuery, $connection)))
	showerror();


// populate the tables of search words for all pluigns
foreach ($plugin_id_array as $plugin_id ) {
	addWords($connection, $plugin_id);
}


//deleteWords($connection, $plugin_id);


?>
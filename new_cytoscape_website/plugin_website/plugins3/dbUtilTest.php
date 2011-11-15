<?php
// This script will create Lucene Index "./luceneIndex" for all the plugins in CyPluginDB
// If there is an existing index, the existing one will be overwritten.

// Include the DBMS credentials
include 'db.inc';
include 'dbUtil.inc';


// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
	showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();



$plugin_ids = getPluginIDs($connection);

foreach ($plugin_ids as $plugin_id) {
	//echo "plugin_id =".$plugin_id."<br>\n";
}

$plugin_id = 137;
$pluginInfo = getPluginInfo($connection, $plugin_id);

echo "name =".$pluginInfo['name']."<br>\n";
echo "description =".$pluginInfo['description']."<br>\n";
echo "authors = ".$pluginInfo['author']."<br>\n";


?>
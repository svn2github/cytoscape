<?php
// File Name: downloadpluginjar.php

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
	showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();

$plugin_file_id = $_GET['id'];

$dbQuery = "SELECT * FROM plugin_files ".
			"WHERE plugin_file_auto_id = $plugin_file_id";

// Run the query
if (!($result = @ mysql_query($dbQuery,$connection)))
	showerror();

if (mysql_num_rows($result) == 1) {
	$fileName = @ mysql_result($result, 0, "file_name");
	$fileType = @ mysql_result($result, 0, "file_type");
	$fileContent = @ mysql_result($result, 0, "file_data");

	header("Content-type: $fileType");
	header('Content-Disposition: attachment; filename='.$fileName); 
	echo $fileContent;

} else {
	echo "File doesn't exist.";
}
?>

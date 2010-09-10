<?php
// File Name: downloadpluginjar.php

function updateUsageLog($connection, $plugin_file_id) {
        // Get plugin_version_id from DB
        $dbQuery = "SELECT version_auto_id FROM plugin_version ".
                        "WHERE plugin_file_id = $plugin_file_id";

        // Run the query
        if (!($result = @ mysql_query($dbQuery,$connection)))
                showerror();

        if (mysql_num_rows($result) == 1) {
                $result_row = @mysql_fetch_array($result);
                $plugin_version_id = $result_row["version_auto_id"];

        } else {
                $plugin_version_id = -1;
        }

        $remote_ip_address= $_SERVER['REMOTE_ADDR'];
        $remote_host = gethostbyaddr($remote_ip_address);
        $refer_page = $_SERVER['HTTP_REFERER'];

        // populate the usagelog table
        $dbQuery = 'insert into usagelog values (0, "'.$plugin_version_id. '","'.$remote_host.'","'.$remote_ip_address.'","'.$refer_page.'"'.',now())';

        // Run the query
        if (!($result = @ mysql_query($dbQuery,$connection)))
                showerror();
								
        // update the download_count in plugin_version table
        $dbQuery = "update plugin_version set download_count = download_count + 1 where version_auto_id = $plugin_version_id";

        // Run the query
        if (!($result = @ mysql_query($dbQuery,$connection)))
                showerror();
}


// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
	showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();
	
include 'clean.inc';
$plugin_file_id = cleanInt($_GET['id']);

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

	// Update the usage table after download
	updateUsageLog($connection, $plugin_file_id);

} else {
	echo "File doesn't exist.";
}
?>

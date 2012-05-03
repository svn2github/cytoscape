<?php
include 'clean.inc';

//$uniqueID = cleanInt($_GET['uniqueID']);
$name =  $_GET['name'];

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
    showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
   showerror();


$returnResult='';

$query = 'SELECT project_url FROM plugin_list WHERE plugin_list.name = '."'$name'";
 
// Run the query
if (!($pluginList = @ mysql_query ($query, $connection))) 
    showerror();
      
// Did we get back any rows?
if (@ mysql_num_rows($pluginList) != 0) 
{

	$pluginList_row = @ mysql_fetch_array($pluginList);
	$project_url = $pluginList_row["project_url"];
	
	if ($project_url == ''){
		$project_url = 'NULL';
	}

	$returnResult = 'project_url='.$project_url.'<br>';
}


///
$query = 'SELECT version, source_url, release_note_url FROM plugin_list,plugin_version WHERE plugin_list.plugin_auto_id = plugin_version.plugin_id '.
		" AND name = '".$name."'";


// Run the query
if (!($queryResult = @ mysql_query ($query, $connection)))
	showerror();


$versionCount = @ mysql_num_rows($queryResult);


while ($versionCount > 0) {


	$versionSpecific_row = @ mysql_fetch_array($queryResult);
	
	if ($versionSpecific_row["version"] != null) {
		$returnResult .= "version=" . $versionSpecific_row["version"].'<br>';
		
		if ($versionSpecific_row["source_url"] != ''){
			$returnResult .= "source_url=" . $versionSpecific_row["source_url"].'<br>';
		}
		else {
			$returnResult .= "source_url=NULL".'<br>';
		}
		
		if ($versionSpecific_row["release_note_url"] != ''){
			$returnResult .= "release_note_url=" . $versionSpecific_row["release_note_url"].'<br>';
		}
		else {
			$returnResult .= "release_note_url=NULL".'<br>';
		}
	} 
	
	$versionCount = $versionCount -1;
}

echo $returnResult;

?>

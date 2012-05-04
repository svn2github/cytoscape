<?php
include 'clean.inc';

//$uniqueID = cleanInt($_GET['uniqueID']);
//$name =  $_GET['name'];

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
    showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
   showerror();


// Generate plugin external URLs in  XML format 
function getPluginURLs($connection, $name){
	
	$returnResult='<plugin>';
	$returnResult .= '<pluginName>'.$name.'</pluginName>';
	
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
		$returnResult .= '<project_url>'.$project_url.'</project_url>';
	}
	
	$returnResult .= '<pluginVersions>';
	
	///
	$query = 'SELECT version, source_url, release_note_url FROM plugin_list,plugin_version WHERE plugin_list.plugin_auto_id = plugin_version.plugin_id '.
			" AND name = '".$name."'";
	
	// Run the query
	if (!($queryResult = @ mysql_query ($query, $connection)))
		showerror();
	
	$versionCount = @ mysql_num_rows($queryResult);
	
	while ($versionCount > 0) {
		$versionSpecific_row = @ mysql_fetch_array($queryResult);
		$returnResult .= "<version>";
		
		if ($versionSpecific_row["version"] != null) {
			$returnResult .= "<versionNumber>".$versionSpecific_row["version"]."</versionNumber>";					
			if ($versionSpecific_row["source_url"] != ''){
				$returnResult .="<sourceURL>".$versionSpecific_row["source_url"]."</sourceURL>";
			}
			else {
				$returnResult .="<sourceURL>NULL</sourceURL>";
			}			
			if ($versionSpecific_row["release_note_url"] != ''){
				$returnResult .="<release_note_url>".$versionSpecific_row["release_note_url"]."</release_note_url>";					
			}
			else {
				$returnResult .="<release_note_url>NULL</release_note_url>";
			}
		} 
		$returnResult .= "</version>";		
		$versionCount = $versionCount -1;
	}
	
	$returnResult .= '</pluginVersions>';
	$returnResult .='</plugin>';
	
	return $returnResult;
}

/////////////////////// main logic ////////////////////////////////////

// Get the list plugin names
$query = 'SELECT distinct plugin_auto_id,name, unique_id FROM plugin_list order by name';

// Run the query
if (!($pluginList = @ mysql_query ($query, $connection)))
	showerror();
 
//echo " (",@ mysql_num_rows($pluginList),")";

$result = '<plugins>';

// Did we get back any rows?
if (@ mysql_num_rows($pluginList) != 0)
{
	while($pluginList_row = @ mysql_fetch_array($pluginList))
	{
		$pluginID = $pluginList_row["plugin_auto_id"];
		$plugin_name = $pluginList_row["name"];
		$result .= getPluginURLs($connection, $plugin_name);
	}
}

$result .= '</plugins>';

echo $result;

?>

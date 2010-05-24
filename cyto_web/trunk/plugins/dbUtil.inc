<?php

function getPluginIDs($connection){
	// get the list of pluigin_id
	$dbQuery = "SELECT distinct plugin_id FROM plugin_version WHERE status = 'published'";
	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();
	
	$plugin_id_array = array(); // create an empty array
	
	if (@ mysql_num_rows($result) != 0) {
		while($_row = @ mysql_fetch_array($result))
        {
     	  $plugin_id_array[] = $_row["plugin_id"];
        } 
	}	
	return $plugin_id_array;
}
	
	
function getPluginInfo($connection, $plugin_id){
	// get the name and description for this plugin with id = pluigin_id
	//$dbQuery = "SELECT name, description FROM plugin_list WHERE plugin_auto_id = $plugin_id";
	
	$dbQuery = "select name, description, names 
	from plugin_list, plugin_version, plugin_author, authors
	where plugin_list.plugin_auto_id = plugin_version.plugin_id
	and plugin_author.plugin_version_id = plugin_version.version_auto_id 
	and authors.author_auto_id = plugin_author.author_id
	and plugin_id = ".$plugin_id;

	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();
	
	$plugin_info_array = array(); // create an empty array
	
	if (@ mysql_num_rows($result) != 0) {
		while($_row = @ mysql_fetch_array($result))
        {
     	  $plugin_info_array['name'] = $_row["name"];
     	  $plugin_info_array['description'] = $_row["description"];
		  $plugin_info_array['author'] = $_row["names"];
        } 
	}	
	return $plugin_info_array;
}

?>
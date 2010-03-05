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
	
?>
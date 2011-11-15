<?php
function getUniqueID($connection) {

	$retValue = -1;
	$dbQuery = "select max(unique_id) as current_max from plugin_list";	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) != 0) {
			$the_row = @ mysql_fetch_array($result);
			$current_max = $the_row['current_max'];
			$retValue = $current_max + 1; 
	}

	return $retValue;
}
?>
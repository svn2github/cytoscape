<?php


// These are the DBMS credentials and the database name
$dbServer = "localhost";
$dbName = "cyplugindb";
$dbUser = "cytouser";
$dbPass = "cytouser";
$cytostaff = "cytostaff";
$cytostaffPass = "cytostaff";

// Show an error and stop the script
function showerror() {
	if (mysql_error())
		die("Error " . mysql_errno() . " : " . mysql_error());
	else
		die("Could not connect to the DBMS");
}

?>

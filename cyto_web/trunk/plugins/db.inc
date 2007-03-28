<?php


// These are the DBMS credentials and the database name
$dbServer = "chianti.ucsd.edu";
$dbName = "CyPluginDB";
$dbUser = "cytouser";
$dbPass = "cytouser";

// Show an error and stop the script
function showerror() {
	if (mysql_error())
		die("Error " . mysql_errno() . " : " . mysql_error());
	else
		die("Could not connect to the DBMS");
}

// Secure the user data by escaping characters and shortening the input string
function clean($input, $maxlength) {
	$input = substr($input, 0, $maxlength);
	$input = EscapeShellCmd($input);
	return ($input);
}
?>

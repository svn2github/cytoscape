<?php
// Connect to the MySQL DBMS

// These are the DBMS credentials and the database name
$dbServer = "localhost";
$dbName = "cellcircuits_dev_nov06";
$dbUser = "mdaly";
$dbPass = "mdalysql";
if (isset($mode) && $mode == 'edit') {
	$dbUser = "mdaly"; //"staff";
	$dbPass = "mdalysql"; //"staff";
}

if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
	showerror();

// Use the CellCircuits database
if (!mysql_select_db($dbName, $connection))
	showerror();

// Show an error and stop the script
function showerror() {
	if (mysql_error())
		die("Error " . mysql_errno() . " : " . mysql_error());
	else
		die("Could not connect to the DBMS");
}

?>

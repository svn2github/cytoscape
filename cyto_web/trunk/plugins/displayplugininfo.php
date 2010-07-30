<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="http://chianti.ucsd.edu/~kono/cytoscape/css/main.css" type="text/css" rel="stylesheet" media="screen">
<title>Plugin Information</title>
<script type="text/javascript" 
src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
<script type="text/javascript" src="http://chianti.ucsd.edu/~kono/cytoscape/js/menu_generator.js"></script>

    <style type="text/css">
<!--
.style1a {
	font-size: 24px;
	font-weight: bold;
}
-->
    </style>
</head>

<body>
<div id="container"> 
  <script src="http://chianti.ucsd.edu/~kono/cytoscape/js/header.js"></script>


<?php
include 'getpluginInfo.php';
include 'clean.inc';

//$uniqueID = cleanInt($_GET['uniqueID']);
$name = $_GET['name'];

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
    showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
   showerror();

$query = 'SELECT distinct plugin_auto_id,name, unique_id, description, license, license_required, project_url FROM plugin_list,plugin_version WHERE plugin_list.plugin_auto_id = plugin_version.plugin_id '.
" AND name = '".$name."'";
 
// Run the query
if (!($pluginList = @ mysql_query ($query, $connection))) 
    showerror();
      
// Did we get back any rows?
if (@ mysql_num_rows($pluginList) != 0) 
{
	echo "\n\t\t<ul>";
	$pluginList_row = @ mysql_fetch_array($pluginList);
		
	echo "<b>Plugin name:</b> ".$pluginList_row["name"]."<br>";
	echo getPluginInfoPage($connection, $pluginList_row);
}
			      
?>

<br>

 <script src="http://chianti.ucsd.edu/~kono/cytoscape/js/footer.js"></script> 
<br>
</body>
</html>

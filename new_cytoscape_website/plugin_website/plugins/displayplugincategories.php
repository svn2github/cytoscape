<?php

//include "getPluginUniqueID.inc";


	$pageTitle = 'Plugin Categories';
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link href="http://cytoscape.org/css/main.css" type="text/css" rel="stylesheet" media="screen">
    <title><?php echo $pageTitle;?></title>
    <script type="text/javascript" 
src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
    <script type="text/javascript" src="http://cytoscape.org/js/menu_generator.js"></script>
    
    </head>

    <body>
<div id="container">
<script src="http://cytoscape.org/js/header.js"></script>


<div class="blockfull">
<?php


$tried = NULL;
if (isset ($_POST['tried'])) {
	$tried = 'yes';
}


// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
		showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();



$query = "SELECT * FROM categories";
 
// Run the query
if (!($categories = @ mysql_query ($query, $connection)))
	showerror();

?>
<h1><b>Cytoscape 2.x Plugin Categories:</b></h1>
<br />

<ul>
<?php 

if (@ mysql_num_rows($categories) != 0)
{

	while($category_row = @ mysql_fetch_array($categories))
	{
		$categoryName = $category_row["name"];
		if ($categoryName == "Core" || $categoryName == "Other_old"){
			continue;
		}
		echo "<li>";
		echo $categoryName."<br />";
		echo "</li>";		
	}
}

?>
</ul> 
<br />
<script src="http://cytoscape.org/js/footer.js"></script> 
</body>
</html>

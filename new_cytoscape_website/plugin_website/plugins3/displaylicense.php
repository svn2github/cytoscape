<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape 2.x Plugins</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
</head>
<body bgcolor="#ffffff">
<div id="topbar">
        <div class="title">Plugin License</div>
</div>

<?php include "../nav.php"; ?>

<?php

include 'clean.inc';

$pluginID = cleanInt($_GET['pluginid']);

//echo 'pluginID = '.$pluginID;

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
    showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
   showerror();

$query = "SELECT name, license FROM plugin_list where plugin_auto_id = $pluginID";

// Run the query
if (!($licenseArray= @ mysql_query ($query, $connection)))
   showerror();

$licenseRow= @mysql_fetch_array($licenseArray);
$licenseText=stripslashes($licenseRow['license']);
$pluginName = $licenseRow['name'];
?>

<br>
<div id="indent">
	<p><strong>Name:</strong> <?php echo $pluginName ?></p>
	<p><big><b>License:</b></big><br><?php echo $licenseText ?></p>
  <p>&nbsp;</p>
</div>

<?php include "../footer.php"; ?>
<br>
</body>
</html>

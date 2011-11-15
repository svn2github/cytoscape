<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape 2.x Plugins</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
	<STYLE type="text/css">
  		DIV.mypars {text-align: left}
    </STYLE>

</head>
<body bgcolor="#ffffff">

<div id="topbar">
        <div class="title">Cytoscape 2.x Plugins Release Note</div>
</div>

<?php include "../nav.php"; ?>


<?php
include 'clean.inc';
$version_id = cleanInt($_GET['version_id']);

include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
    showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
   showerror();

$query = "SELECT name, version, release_note FROM plugin_list, plugin_version where plugin_list.plugin_auto_id = plugin_version.plugin_id and version_auto_id = $version_id";

// Run the query
if (!($resultArray= @ mysql_query ($query, $connection)))
   showerror();

$row= @mysql_fetch_array($resultArray);
$name=$row['name'];
$version=$row['version'];

$release_note=stripslashes($row['release_note']);

$pattern = '/\n/';
$replacement = '<br>';
   
$release_note = preg_replace($pattern, $replacement, $release_note);

$release_note = '<br>'.$release_note;

?>
<br>

<div id="indent" class="mypars">
	<p><strong>Plugin Name:</strong> <?php echo $name ?></p>
	<p><strong>Version: </strong><?php echo $version ?></p>
	<p><big><b>Release Note:</b></big><br><?php echo $release_note ?></p>
  <p>&nbsp;</p>
</div>

<?php include "../footer.php"; ?>
<br>
</body>
</html>

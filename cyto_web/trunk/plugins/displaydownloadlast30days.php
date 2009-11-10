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
        <div class="title">Plugin Download Statistics</div>
</div>

<?php include "../nav.php"; ?>

<?php

include 'clean.inc';

$plugin_version_id = cleanInt($_GET['plugin_version_id']);

//echo 'plugin_version_ID = '.$plugin_version_id;

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
    showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
   showerror();

$oneMonthAgo = strtotime ( '-1 month' , strtotime ( date("y-m-d") ) ) ; 
$date_30daysago = date ( 'Y-m-j' , $oneMonthAgo );

$queryLast30days = "select count(log_auto_id) as totalCount".
	" from usagelog".
	" where ".
	" usagelog.sysdat>="."'$date_30daysago' and usagelog.plugin_version_id = $plugin_version_id ";

if (!($statArrayLast30days= @ mysql_query ($queryLast30days, $connection)))
   showerror();
	
$stat_row = @ mysql_fetch_array($statArrayLast30days);
$plugunTotalArraylast30days =$stat_row["totalCount"];

?>

<br>
<div id="indent">
	<p><strong>Total download for this plugin version during last 30 days is </strong> <?php echo $plugunTotalArraylast30days ?></p>

  <p>&nbsp;</p>
</div>

<?php include "../footer.php"; ?>
<br>
</body>
</html>

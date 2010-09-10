<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="http://chianti.ucsd.edu/~kono/cytoscape/css/main.css" type="text/css" rel="stylesheet" media="screen">
<title>Plugin Download Statistics</title>
<script type="text/javascript" 
src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
<script type="text/javascript" src="http://chianti.ucsd.edu/~kono/cytoscape/js/menu_generator.js"></script>
</head>

<body>
<div id="container"> 
  <script src="http://chianti.ucsd.edu/~kono/cytoscape//js/header.js"></script>


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

<script src="http://chianti.ucsd.edu/~kono/cytoscape/js/footer.js"></script> 
<br>
</body>
</html>

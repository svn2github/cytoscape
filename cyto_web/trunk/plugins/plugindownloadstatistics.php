<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<title>Plugin download statistics</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
    <style type="text/css">
<!--
.style1a {
	font-size: 24px;
	font-weight: bold;
}
-->
    </style>
</head>
<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title">Plugin download statistics</div>
</div>
<div id="container">
<?php include "../nav.php"; ?>

<?php

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
    showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
   showerror();

// Get the distinct IP addresses 
$query0 = 'select count(distinct ip_address) as userCount from usagelog';
// Run the query
if (!($tmpArray= @ mysql_query ($query0, $connection)))
   showerror();
$tmp_row = @ mysql_fetch_array($tmpArray);
$tmpCountArray[] = $tmp_row["userCount"];
$distinctUsers =$tmpCountArray[0]; 

?>
<br><p align="left">Since the release of Cytoscape 2.5 (July 2007), there are  <?php echo $distinctUsers; ?> users (distinct IP addresses) downloaded plugins from this site.</p>
<?php

//$oneDayAgo = strtotime ( '-1 day' , strtotime ( date("y-m-d") ) ) ; 
//$date_1dayago = date ( 'Y-m-j' , $oneDayAgo);

$oneMonthAgo = strtotime ( '-1 month' , strtotime ( date("y-m-d") ) ) ; 
$date_30daysago = date ( 'Y-m-j' , $oneMonthAgo );


$query1 = "select plugin_list.name as name, plugin_version.version as version, plugin_version.version_auto_id as plugin_version_id, download_count as totalCount ".
	"from plugin_list, plugin_version ".
	"where plugin_list.plugin_auto_id=plugin_version.plugin_id order by name, version DESC";


// Run the query
if (!($statArray= @ mysql_query ($query1, $connection)))
   showerror();

while($stat_row = @ mysql_fetch_array($statArray))
{
	$plugunNameArray[] = $stat_row["name"];
	$plugunVersionArray[] =$stat_row["version"];
	$plugunVersionIDArray[] =$stat_row["plugin_version_id"];
	$plugunTotalArray[] =$stat_row["totalCount"];
}

for ($i=0; $i<count($plugunVersionIDArray); $i++ ) {	
	$plugunTotalArraylast30days[] ="<a href=\"displaydownloadlast30days.php?plugin_version_id=$plugunVersionIDArray[$i]\">click here</a>";
}

?>
<p>&nbsp;</p>
<table width="543" border="1" align="center">
  <tr>
    <th width="180" scope="col"><div align="center">Plugin Name </div></th>
    <th width="49" scope="col"><div align="center">version</div></th>
    <th width="130" scope="col"><div align="center">Total download</div></th>
    <th width="250" scope="col"><p align="center">Total downlaod </p>
    <p align="center">last 30 days</p></th>

  </tr>
<?php

$lastPluginName = "&nbsp;";

for ($i= 0; $i<count($plugunNameArray); $i++) {
	
	if ($plugunNameArray[$i] == $lastPluginName){
		$name ="&nbsp;";
	}
	else {
		$name =  $plugunNameArray[$i];
	}
	$version = $plugunVersionArray[$i];
	$totalCount = $plugunTotalArray[$i];
	$totalCountLast30days = $plugunTotalArraylast30days[$i];
	if ($totalCount == NULL){
		continue;
	}
	$lastPluginName = $plugunNameArray[$i];
	?>
	 <tr>
	    <th width="180" scope="row"><div align="right"><?php echo $name; ?></div></th>
	    <td><div align="right"><?php echo $version; ?></div></td>
	    <td><div align="right"><?php echo $totalCount; ?></div></td>
	    <td><div align="right"><?php  echo $totalCountLast30days; ?></div></td>
  </tr>
<?php
}
?>
</table>
<p>&nbsp; </p>

<?php include "../footer.php"; ?>
<br>
</body>
</html>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Choose file type</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
<style type="text/css">
<!--
.style1 {
	font-size: 18px;
	font-weight: bold;
}
-->
</style>

</head>
<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title">Choose plugin file type</div>
</div>

<?php include "../nav.php"; ?>


<body>
<p>&nbsp;</p>
<p class="style1">Please choose plugin file type to submit</p>
<table width="960" height="121" border="0">
  <tr>
    <th width="101" ><a href="pluginsubmit.php">Jar file</a></th>
    <td width="849" ><div align="left">A single <a href="http://www.cytoscape.org/cgi-bin/moin.cgi/Cytoscape_Plugin_Tutorial"> properly formatted plugin jar</a>. We will read (and validate) the plugin metadata (name, description, version, etc.) from the jar.</div></td>
  </tr>
  <tr>
    <th height="64" scope="row"><a href="pluginsubmitzip.php">Zip file</a></th>
    <td>A file with extension .zip, which should contain the plugin jar and any other necessary data.  The file will be loaded as is and you will need to enter the plugin metadata.</td>
  </tr>
  <tr>
    <th scope="row"><a href="pluginsubmiturl.php">URL</a> </th>
    <td>Plugin information with project URL, from where user can download the plugin manually </td>
  </tr>
</table>

<p>

<?php include "../footer.php"; ?>
</body>
</html>

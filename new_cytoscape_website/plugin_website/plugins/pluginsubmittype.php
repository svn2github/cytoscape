<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Choose file type</title>
	<link rel="stylesheet" type="text/css" media="screen" href="http://cytoscape.org/css/cytoscape.css">
	<link rel="shortcut icon" href="http://cytoscape.org/images/cyto.ico">
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
<div id="container">
<div id="topbar">
	<div class="title">Choose plugin file type</div>
</div>

<?php include "http://cytoscape.org/nav.php"; ?>

<p>&nbsp;</p>
<p class="style1">Please choose plugin file type to submit</p>
<p class="style1">&nbsp;</p>
<table width="960" height="204" border="0">
  <tr>
    <th width="101" height="40" ><a href="pluginsubmit.php">Jar file</a></th>
    <td width="849" ><div align="left">A single <a href="http://www.cytoscape.org/cgi-bin/moin.cgi/Cytoscape_Plugin_Tutorial"> properly formatted plugin jar</a>. We will read (and validate) the plugin metadata (name, description, version, etc.) from the jar.</div></td>
  </tr>
  <tr>
    <th height="69" scope="row"><a href="pluginsubmitzip.php">Zip file</a></th>
    <td>A file with extension .zip, which should contain the plugin jar and any other necessary data.  The file will be loaded as is and you will need to enter the plugin metadata.</td>
  </tr>
  <tr>
    <th scope="row"><a href="pluginsubmiturl.php">URL</a> </th>
    <td><p>Plugin information with project URL. Since Cytoscape does not host the Zip/Jar file, they will not appear at the Cytoscape plugin manager. Users should download them manualy by following the project URLs, if they are interested.
      </p>
      <p></p></td>
  </tr>
</table>

<p>

<?php include "http://cytoscape.org/footer.php"; ?>
</body>
</html>

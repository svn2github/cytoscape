<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<title>Frequently Asked Questions</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
</head>
<body bgcolor="#ffffff">

<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title">Frequently Asked Questions</div>
</div>

<?php include "nav.php"; ?>

<table width="787" border="0">
  <tr>
    <td width="34" height="81"><strong>Q</strong>: </td>
    <td width="743">The plugins directory has all the relevant jar files and the shell from which I invoked Cytoscape 2.4 indicates that all plugins have been successfully loaded. But I am unable to see loaded plugins from the Plugin drop-down menu. Am I missing anything?</td>
  </tr>
  <tr>
    <td><strong>A</strong>: </td>
    <td>The message �all plugins have been successfully loaded� means all of the plugins found in the plugins dir are actually loaded.  The "plugins" menu is somewhat misleading as there is no requirement that a plugin actually present itself there.  For example, the biopax plugin is accessed simply by importing a biopax formatted file.  Other plugins are found throughout the application, like the cPath plugin that is accessed through the "File -> New -> Network -> Construct network using cPath" menu.</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
</table>

<?php include "footer.php"; ?>
<br>
</body>
</html>

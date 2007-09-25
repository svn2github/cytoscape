<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<title>Frequently Asked Questions</title>
	<link rel="stylesheet" type="text/css" media="screen" href="./css/cytoscape.css">
	<link rel="shortcut icon" href="./images/cyto.ico">
</head>
<body bgcolor="#ffffff">

<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title">Frequently Asked Questions</div>
</div>
<?php include "nav.php"; ?>
<p>&nbsp;</p>
<p><strong>The plugins directory has all the relevant jar files and the shell from which I invoked Cytoscape  indicates that all plugins have been successfully loaded. But I am unable to see loaded plugins from the Plugin drop-down menu. Am I missing anything?</strong></p>
<p>The message �all plugins have been successfully loaded� means all of the plugins found in the plugins dir are actually loaded.  The "plugins" menu is somewhat misleading as there is no requirement that a plugin actually present itself there.  For example, the biopax plugin is accessed simply by importing a biopax formatted file.  Other plugins are found throughout the application, like the cPath plugin that is accessed through the "File -> New -> Network -> Construct network using cPath" menu.</p>
<p><strong>I'm using  Cytoscape to visualize a large network (around 300K edges and 7000 nodes), the  layout algorithms don't seem to operate correctly. Is the network too large?</strong></p>
<p>By default,  Cytoscape allocates 512MB for Java, which is definitely not enough memory for very  large network. &nbsp;You can do a couple of things to increase the default heap  size, depending on how you are running Cytoscape. </p>
<p>(1) If you are  executing it from the shell you want to edit cytoscape.sh and change '-Xmx512m'  to something more reasonable for your machine, say '-Xmx1536m'. &nbsp;</p>
<p>(2) If you are  executing it from the finder (e.g.double-clicking the Cytoscape icon), you need  to open a shell and go into the Cytoscape.app directory, then cd to  Contents/Resources and edit the file i4jlauncher.config. &nbsp;Near the bottom  of that file is a line that says 123=-Xmx512M &nbsp;-- change that one to  123=-Xmx1536M.<br>
    <br>
  Now that that is done, you should have a little more memory to work with.  &nbsp;The next question is which layout algorithms are you interested in using?  &nbsp;Some algorithms are more sensitive to the number of edges than others. With that many edges, I suspect you will wind up with a pretty dense  hairball by the time you visualize it.</p>
<p>
  <?php include "footer.php"; ?>
  <br>
</p>
</body>
</html>

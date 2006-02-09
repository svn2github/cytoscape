
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  <meta name="description" content="Network Comparison Toolkit" />
  <meta name="keywords" content="java,graph alignment,graph algorithms,graph isomorphism,network alignment,systems biology,interaction networks" />
  <meta name="author" content="Michael Smoot / Original design: Andreas Viklund - http://andreasviklund.com/" />
  <link rel="stylesheet" type="text/css" href="andreas01.css" media="screen" title="andreas01 (screen)" />
  <title> <?php echo "$title"; ?> </title>
</head>

<body>
<div id="wrap">

<div id="header">
<h1 style="width: 500px;">Network Comparison Toolkit</h1>
<?php echo "<p>$title</p>\n"; ?>
</div>


<img style="width: 760px; height: 175px;" id="frontphoto" src="images/network.jpg" alt="" />

<div id="avmenu">
<h2 class="hide">Menu:</h2>

<ul>
  <li><a href="index.html">Home</a></li>
  <li><a href="about.html">About</a></li>
  <li><a href="download.html">Downloads</a></li>
  <li><a href="docs.html">Documentation</a></li>
  <li><a href="links.html">Links</a></li>
</ul>

<?php
if ( $announce != "" ) {
	echo "<div class=\"announce\">\n$announce\n</div>\n";
}
?>
</div>

<div id="content">
<?php echo "$content\n"; ?>
</div>


<div id="footer">
Copyright &copy; 2006 University of California San Diego. Design by <a href="http://andreasviklund.com">Andreas Viklund</a>.
</div>

</div>
</body>
</html>

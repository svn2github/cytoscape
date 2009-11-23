<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape 2.x Plugins</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
	<SCRIPT LANGUAGE="JavaScript" SRC="mktree.js"></SCRIPT>
    <style type="text/css">
<!--
.style3 {
	font-size: 18;
	color: #0000FF;
}

/* Expand/collapse plugin tree */
/* Put this inside a @media qualifier so Netscape 4 ignores it */
@media screen, print { 
	/* Turn off list bullets */
	ul.mktree  li { list-style: none; } 
	/* Control how "spaced out" the tree is */
	ul.mktree, ul.mktree ul , ul.mktree li { margin-left:10px; padding:0px; }
	/* Provide space for our own "bullet" inside the LI */
	ul.mktree  li           .bullet { padding-left: 15px; }
	/* Show "bullets" in the links, depending on the class of the LI that the link's in */
	ul.mktree  li.liOpen    .bullet { cursor: pointer; background: url(../images/minus.gif)  center left no-repeat; }
	ul.mktree  li.liClosed  .bullet { cursor: pointer; background: url(../images/plus.gif)   center left no-repeat; }
	ul.mktree  li.liBullet  .bullet { cursor: default; background: url(bullet.gif) center left no-repeat; }
	/* Sublists are visible or not based on class of parent LI */
	ul.mktree  li.liOpen    ul { display: block; }
	ul.mktree  li.liClosed  ul { display: none; }
	/* Format menu items differently depending on what level of the tree they are in */
	ul.mktree  li { font-size: 12pt; }
	ul.mktree  li ul li { font-size: 11pt; }
	ul.mktree  li ul li ul li { font-size: 10pt; }
	ul.mktree  li ul li ul li ul li { font-size: 8pt; }
}

-->
    </style>
</head>
<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title">Cytoscape 2.x Plugins</div>
</div>
<div id="container">
<?php include "../nav.php"; ?>
<div align="left">
  <p align="right"><a href="pluginadmin.php">Admin only</a>&nbsp;&nbsp;<a href="plugindownloadstatistics.php">View download activities</a>&nbsp;&nbsp;<a href="pluginsubmittype.php">Submit a plugin</span> to Cytoscape</a>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;</p>
</div>
<span class="style2">
  <div id="indent">
	<big><b>About Cytoscape Plugins:</b></big>
	<p>
		Cytoscape includes a flexible Plugin architecture that enables developers to add extra 
		functionality beyond that provided in the core. Plugins also provide a convenient place 
		for testing out new Cytoscape features. As more Plugins become available, they will be 
		listed on this page, and posted to our 
		<A HREF="http://groups-beta.google.com/group/cytoscape-announce">cytoscape-announce</A> 
		mailing list.    </p>

<?php 
  // Include the DBMS credentials
  include 'db.inc';
  // Include the function to display the info for each plugin
  include 'getpluginInfo.php';

  // Connect to the MySQL DBMS
  if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass))) 
     showerror();

  // Use the CyPluginDB database
  if (!mysql_select_db($dbName, $connection))
     showerror();

  $query = "SELECT * FROM categories";
  
  // Run the query
  if (!($categories = @ mysql_query ($query, $connection))) 
     showerror();
  
  echo "<p>The plugins on this page are categorized into <big><b>", @mysql_num_rows($categories), "</b></big> sections:</p>";
  echo "\n<p>\n<big><b>Current Cytoscape 2.x plugins</b></big>\n</p>";
		 
  echo  "\n\n<A href=\"#\" onClick=\"expandTree('tree1'); return false;\">Expand All</A>&nbsp;&nbsp;&nbsp;".
		"\n<A href=\"#\" onClick=\"collapseTree('tree1'); return false;\">Collapse All</A>&nbsp;&nbsp;&nbsp";
            	  
  // build an expandable tree
  if (@ mysql_num_rows($categories) != 0) 
  {
       echo "\n\n<ul class=\"mktree\" ", "id=\"tree1\">";
       while($category_row = @ mysql_fetch_array($categories))
       {
     	  $categoryName = $category_row["name"]; 
     	  $categoryDescription = $category_row["description"];
    	  $categoryID = $category_row["category_id"]; 
     	  echo "\n\t<li>";
   		  echo "<span class=\"style3\"><B>",$categoryName,"</B></span>&nbsp;&nbsp;--&nbsp;&nbsp;",$categoryDescription;
   		  //echo $categoryName;
   
   		  //Get the plugin list for the category. "plugin_list.plugin_auto_id = plugin_version.plugin_id" will exclude
		  // those with data in plugin_list, but no data in plugin_version (caused by failed transaction)
     	  $query = 'SELECT distinct plugin_auto_id,name, unique_id, description, license, license_required, project_url FROM plugin_list,plugin_version WHERE plugin_list.plugin_auto_id = plugin_version.plugin_id AND category_id =' . $categoryID.' order by name';
 
  		  // Run the query
          if (!($pluginList = @ mysql_query ($query, $connection))) 
              showerror();
             
		echo " (",@ mysql_num_rows($pluginList),")";

              // Did we get back any rows?
			  if (@ mysql_num_rows($pluginList) != 0) 
			  {
			       echo "\n\t\t<ul>";
			       while($pluginList_row = @ mysql_fetch_array($pluginList))
			       {	    
			       	  $pluginID = $pluginList_row["plugin_auto_id"];
			          echo "\n\t\t\t<li>";
			          echo "\n\t\t\t\t",$pluginList_row["name"];
			          
			          // add plugin info
			          echo "\n\t\t\t<ul>";
			          echo "\n\t\t\t\t<li>";
			          echo getPluginInfoPage($connection, $pluginList_row);
			          echo "\n\t\t\t\t</li>";
			          echo "\n\t\t\t</ul>";
			          
			          echo "\n\t\t\t</li>";
			       }  
			       echo "\n\t\t</ul>";
			  }
             	   
     	  echo "\n\t</li>";

       }  // while loop
       
       echo "\n</ul>"; 
  }

	echo 
	"<p>" .
	"\n<p><big><b>Writing Your Own Plugins</b></big>" .
	"\n<p>" . 
	"\nIf you are interested in building your own Cytoscape Plugin, check out the <a href=\"http://cytoscape.wodaklab.org/wiki/Cytoscape_Plugin_Tutorial\">" .
	"\nCytoscape Plugin Tutorial</a>" .
	"\n</p>" . 
	"\n<p><big><b>Old Plugins</b></big>" .
	"\n<p>" . 
	"\nWe also maintain a list of older <a href=\"http://cytoscape.org/plugins2.php\">Cytoscape 2.x plugins</a> and <a href=\"http://cytoscape.org/plugins1.php\">" .
	"\nCytoscape 1.x Plugins</a>. " .
	"\n</p>" .
	"\n<p><big><b>PlugIn License Policy:</b></big>" .
	"\n<P>" .
	"\nAlthough the Cytoscape core application is distributed under a Library GNU Public License (LGPL)," .
    "\nplugins are separate works which use Cytoscape as a Java code library." .
	"\nPlugins are therefore governed by independent software licenses " .
	"\ndistributed with and specific to each plugin.  The Cytoscape project " .
	"\nhas no intent to capture plugins under the license terms of the core Cytoscape LGPL." .
	"\n</p>" 

 ?>
</div>
</div>
</span>
<?php include "../footer.php"; ?>
<br>
</body>
</html>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="http://chianti.ucsd.edu/~kono/cytoscape/css/main.css" type="text/css" rel="stylesheet" media="screen">
<title>Cytoscape 2.x Plugins</title>
<meta name="description" content="Cytoscape includes a flexible Plugin architecture that enables developers to add extra functionality beyond that provided in the core. Plugins also provide a convenient place for testing out new Cytoscape features. As more Plugins become available, they will be listed on this page, and posted to our cytoscape-announce mailing list. " />

<script type="text/javascript" 
src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
<script type="text/javascript" src="http://chianti.ucsd.edu/~kono/cytoscape/js/menu_generator.js"></script>

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

<body>

<div id="container"> 
   <script src="http://chianti.ucsd.edu/~kono/cytoscape//js/header.js"></script>
 
 
<div align="left">
  <p align="right">&nbsp;&nbsp;<a href="plugindownloadstatistics.php">View download activities</a>&nbsp;&nbsp;<a href="pluginsubmittype.php">Submit a plugin</span> to Cytoscape</a>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;</p>
</div>
<div id="indent">
    <p>
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
  
?>		 
</p>

<form action="keywordsearch.php" method="post">
  <label>
  <input type="text" name="searchwords" size="30">
   <input type="submit" name="Submit" value="Search">
  </label>
</form>
<br>
<p>
  <?php

  echo  "\n\n<A href=\"#\" onClick=\"expandTree('tree1'); return false;\">Expand All</A>&nbsp;&nbsp;&nbsp;".
		"\n<A href=\"#\" onClick=\"collapseTree('tree1'); return false;\">Collapse All</A>&nbsp;&nbsp;&nbsp";
            	  
  // build an expandable tree
  if (@ mysql_num_rows($categories) != 0) 
  {
       echo "\n\n<ul class=\"mktree\" ", "id=\"tree1\">";
       while($category_row = @ mysql_fetch_array($categories))
       {
     	  $categoryName = $category_row["name"]; 
		  if ($categoryName == "Core"){
		  	continue;
		  }
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
					  $unique_id = $pluginList_row["unique_id"];
					  $plugin_name = $pluginList_row["name"];
			          echo "\n\t\t\t<li>";
			          //echo "\n\t\t\t\t<a href=\"displayplugininfo.php?uniqueID=",$unique_id,"\">",$pluginList_row["name"],"</a>";
			          echo "\n\t\t\t\t<a href=\"displayplugininfo.php?name=".$plugin_name."\">",$pluginList_row["name"],"</a>";
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

 ?>
 
  
  <big><b>Old Plugins</b></big>
  <br>
  <br><p>
	We also maintain a list of older <a href="http://cytoscape.org/plugins2.php">Cytoscape 2.x plugins</a> and <a href="http://cytoscape.org/plugins1.php">
	Cytoscape 1.x Plugins</a></p>
  <br>
 
 	<big><b>Writing Your Own Plugins</b></big>
	<br><br><p>
	If you are interested in building your own Cytoscape Plugin, check out the <a href="http://cytoscape.wodaklab.org/wiki/cytoscape_developer_tutorial">
	Cytoscape Plugin Tutorial</a></p>
	<br>
	<big><b>PlugIn License Policy:</b></big>
<br><br>
	<p>
	Although the Cytoscape core application is distributed under a Library GNU Public License (LGPL),
    plugins are separate works which use Cytoscape as a Java code library.
	Plugins are therefore governed by independent software licenses 
	distributed with and specific to each plugin.  The Cytoscape project 
	has no intent to capture plugins under the license terms of the core Cytoscape LGPL.</p>
 
<br><big><b>About Cytoscape Plugins:</b></big>
<br><br><p>
 Cytoscape includes a flexible Plugin architecture that enables developers to add extra 
  functionality beyond that provided in the core. Plugins also provide a convenient place 
  for testing out new Cytoscape features. As more Plugins become available, they will be 
  listed on this page, and posted to our <a href="http://groups-beta.google.com/group/cytoscape-announce">cytoscape-announce</a> mailing list.
  </p>
 <br><br>

  <script src="http://chianti.ucsd.edu/~kono/cytoscape/js/footer.js"></script> 
</div>
</body>
</html>

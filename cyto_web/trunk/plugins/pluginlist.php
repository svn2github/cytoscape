<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape 2.x Plugins</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/css/cytoscape.css">
	<link rel="shortcut icon" href="images/cyto.ico">
	<SCRIPT LANGUAGE="JavaScript" SRC="mktree.js"></SCRIPT>
    <style type="text/css">
<!--
.style3 {
	font-size: 18;
	color: #0000FF;
}
-->
    </style>
</head>
<body bgcolor="#ffffff">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tbody>
		<tr>
			<td width="10">&nbsp;
			</td>
			<td valign="bottom">
				<h1>Cytoscape 2.x Plugins</h1>
			</td>
		</tr>
	</tbody>
</table>
<?php include "../nav.php"; ?>
<div align="left">
  <p align="right"><a href="pluginsubmit.php">Submit a plugin</span> to Cytoscape</a>&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;</p>
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
   
   		  //Get the plugin list for the category
     	  $query = 'SELECT * FROM plugin_list WHERE category_id =' . $categoryID;
 
  		  // Run the query
          if (!($pluginList = @ mysql_query ($query, $connection))) 
              showerror();
             
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
	"\nIf you are interested in building your own Cytoscape Plugin, check out the <a href=\"pluginTutorial.php\">" .
	"\nCytoscape Plugin Tutorial</a>, and the <a href=\"http://cytoscape.systemsbiology.net/Cytoscape2.0/plugin/index.html\">" .
	"\nCytoscape Plugin Writer Documentation</a>.  We also maintain a list of <a href=\"../plugins1.php\">" .
	"\nCytoscape 1.1 Plugins</a>. " .
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
</span>
<?php include "../footer.php"; ?>
<br>
</body>
</html>

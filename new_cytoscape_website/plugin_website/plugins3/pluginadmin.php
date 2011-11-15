<?php include "logininfo.inc"; ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape Plugin Administration</title>
	<link rel="stylesheet" type="text/css" media="screen" href="http://cytoscape.org/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
</head>
<body bgcolor="#ffffff">
<div id="topbar">
        <div class="title">Cytoscape Plugin Administration</div>
</div>
<div id="container">
<?php include "http://cytoscape.org/nav.php"; ?>
<br>
<span class="style2">
  <div id="indent">
	<div align="center">
	  <p><big><b>List of Cytoscape Plugins in CyPluginDB</b></big>
	  </p>
    </div>
</div>
</span>
<br>	    
<table width="726" height="127" border="1">
  <tr>
    <th width="175" scope="col">Category</th>
    <th width="137" scope="col">Plugin Name </th>
    <th width="52" scope="col">Version</th>
    <th width="72" scope="col">Status</th>
    <th width="72" scope="col">Edit</th>
    <th width="84" scope="col">Delete</th>
    <th width="88" scope="col">Last modified </th>
  </tr>

<?php 
  // Include the DBMS credentials
  include 'db.inc';

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
     
  // Add the table rows
  if (@ mysql_num_rows($categories) != 0) 
  {
       while($category_row = @ mysql_fetch_array($categories))
       {
     	  $categoryName = $category_row["name"]; 
    	  $categoryID = $category_row["category_id"]; 
		?>
		  <tr>
			  <td><strong><?php echo $categoryName ?></strong></td>
			    <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
		  </tr>
		<?php
   		  //Get the plugin versions for the category
     	  $query = 'SELECT plugin_list.name,' .
     	  		'plugin_version.version, ' .
     	  		'plugin_version.status, ' .
     	  		'plugin_version.sysdat, ' .
     	  		'plugin_version.version_auto_id' .
     	  		' FROM plugin_list, plugin_version WHERE category_id =' . $categoryID.
     	  		' and plugin_list.plugin_auto_id = plugin_version.plugin_id order by plugin_list.name';
  
  		  // Run the query
          if (!($pluginVersions = @ mysql_query ($query, $connection))) 
              showerror();
			  	
          // Did we get back any rows?
		  if (@ mysql_num_rows($pluginVersions) != 0) 
		  {		  
			       while($pluginVersion_row = @ mysql_fetch_array($pluginVersions))
			       {	    
			       	  $pluginName = $pluginVersion_row["name"];
			       	  $pluginVersion = $pluginVersion_row["version"];
			       	  $pluginStatus = $pluginVersion_row["status"];
			       	  $lastModified = $pluginVersion_row["sysdat"];
			       	  $versionID = $pluginVersion_row["version_auto_id"];
					?><tr>
					    <td>&nbsp;</td>
					    <td><?php echo $pluginName;?></td>
					    <td><?php echo $pluginVersion;?></td>
					    <td><?php echo $pluginStatus;?></td>
					    <td><a href="pluginsubmit.php?versionid=<?php echo $versionID;?>">Edit</a></td>
					    <td><a href="pluginversiondelete.php?versionid=<?php echo $versionID;?>">Delete</a></td>
					    <td><?php echo $lastModified;?></td>
					  </tr>
					<?php			          
			             
			       } // end of inner while loop 
		  }// end if 
             	   
       }  // while loop
  }
     
  ?>
  
</table>
<?php include "http://cytoscape.org/footer.php"; ?>
<br>
</body>
</html>

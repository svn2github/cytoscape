<?php include "logininfo.inc"; ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape Theme Administration</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
</head>
<body bgcolor="#ffffff">
<div id="topbar">
        <div class="title">Cytoscape Theme Administration</div>
</div>

<?php include "../nav.php"; ?>
<br>
<span class="style2">
  <div id="indent">
	<div align="center">
	  <p><big><b>List of Cytoscape Themes in CyPluginDB</b></big>
	  </p>
    </div>
</div>
</span>
<br>	    
<table width="583" height="127" border="1">
  <tr>
    <th width="175" scope="col">Name</th>
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

  $query = "SELECT theme_list.name as name, theme_version.version as version, theme_version.release_date as release_date," .
  		" theme_version.version_auto_id as version_id, theme_version.status as status, theme_version.sysdat as sysdat " .
  		" FROM theme_list, theme_version " .
  		" where theme_list.theme_auto_id = theme_version.theme_id ";
  
  // Run the query
  if (!($results = @ mysql_query ($query, $connection))) 
     showerror();
     
  // Add the table rows
  if (@ mysql_num_rows($results) != 0) 
  {
       while($_row = @ mysql_fetch_array($results))
       {
     	  $themeName = $_row["name"]; 
    	  $themeVersion = $_row["version"];
    	  $themeVersionID = $_row["version_id"];
    	  $themeStatus =  $_row["status"];
    	  if (empty($themeStatus)) {
    	  	$themeStatus = 'unkown';
    	  }
		  $lastModified =  $_row["sysdat"];
		?>
					<tr>
					    <td><?php echo $themeName;?></td>
					    <td><?php echo $themeVersion;?></td>
					    <td><?php echo $themeStatus;?></td>
					    <td><a href="themesubmit.php?versionid=<?php echo $themeVersionID;?>">Edit</a></td>
					    <td><a href="themeversiondelete.php?versionid=<?php echo $themeVersionID;?>">Delete</a></td>
					    <td><?php echo $lastModified;?></td>
					  </tr>
					<?php			          
             	   
       }  // while loop
  }
     
  ?>
</table>
<br>
<?php include "../footer.php"; ?>
<br>
</body>
</html>

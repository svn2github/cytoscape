<?php include "logininfo.inc"; ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Delete a plugin version</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
</head>
<body bgcolor="#ffffff">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tbody>
		<tr>
			<td width="10">&nbsp;
			</td>
			<td valign="bottom">
				<h1>Delete a plugin version </h1>
			</td>
		</tr>
	</tbody>
</table>
<?php include "../nav.php"; ?>

<?php
include 'clean.inc';

$versionID = NULL;
if (isset ($_GET['versionid'])) {
	$versionID = cleanInt($_GET['versionid']);
}
if (isset ($_POST['versionid'])) {
	$versionID = cleanInt($_POST['versionid']);
}

$deleteAction = NULL;
if (isset ($_POST['delete'])) {
	$deleteAction = $_POST['delete'];
}
$tried = NULL;
if (isset ($_POST['tried'])) {
	$tried = 'yes';
}

if (!($tried)) {
?>


<form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="deleteplugin" id="deleteplugin">
  <label></label>
  <label></label>
<table width="605" border="0">
    <tr>
      <td width="595">&nbsp;</td>
    </tr>
    <tr>
      <td>Are you sure you want to delete a plugin version? </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><label>
        <input name="tried" type="hidden" id="tried" value="yes">
        <input name="versionid" type="hidden" id="versionid" value="<?php echo $versionID;?>">
		
        <div align="center">
          <input name="delete" type="submit" id="delete" value="yes"> &nbsp;&nbsp;
          <input name="delete" type="submit" id="delete" value="cancel">
        </div>
      </label></td>
    </tr>
  </table>
</form>
<?php


} else { // process the data

	// If user press cancel button, redirect the user to the admin page
	if ($deleteAction == 'cancel') {
		header('location:pluginadmin.php');
		exit();
	}

	// User confirmed to delete the plugin version

	// Include the DBMS credentials
	include 'db.inc';

	// Connect to the MySQL DBMS
	if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
		showerror();
	// Use the CyPluginDB database
	if (!mysql_select_db($dbName, $connection))
		showerror();

   	//get the plugin_id and plugin_file_id
    $query = 'select plugin_id, plugin_file_id from plugin_version where version_auto_id ='.$versionID;
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
	$theRow = @ mysql_fetch_array($result);
	$plugin_file_id = $theRow['plugin_file_id'];
	$plugin_id = $theRow['plugin_id'];

   	//get the set of author_id
    $query = 'select author_id from plugin_author where plugin_version_id ='.$versionID;
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();	
	$author_ids =''; 	
	while ($theRow = @ mysql_fetch_array($result)) {
			$author_ids .= $theRow["author_id"].',';
	}
	$author_ids = substr($author_ids, 0, strlen($author_ids)-1); //remove the last ','	

	if (trim($author_ids)!="") {		
	   	//construct the query to delete from tables (authors, plugin_author, plugin_files, plugin_version and plugin_list)
	    $query1 = 'delete from authors where author_auto_id IN ('.$author_ids.')';
		// Run the query
		if (!($result = @ mysql_query($query1, $connection)))
			showerror();	
	}

    $query2 = 'delete from plugin_author where plugin_version_id ='.$versionID;
    // Run the query
	if (!($result = @ mysql_query($query2, $connection)))
		showerror();	
    
    $query3 = 'delete from plugin_version where version_auto_id ='.$versionID;
    // Run the query
	if (!($result = @ mysql_query($query3, $connection)))
		showerror();	

    if ($plugin_file_id != NULL) {
    	$query4 = 'delete from plugin_files where plugin_file_auto_id ='.$plugin_file_id;
    	// Run the query
		if (!($result = @ mysql_query($query4, $connection)))
			showerror();	    
    }
    
	//Are there any versions for this plugin left?
    $query = 'select version_auto_id from plugin_version where plugin_id ='.$plugin_id;
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();

	$versionCount = @ mysql_num_rows($result);
	if ($versionCount == 0) {
		// If this is no version left for this plugin, also delete the row from the plugn_list 
    	$query = 'delete from plugin_list where plugin_auto_id ='.$plugin_id;
		// Run the query
		if (!($result = @ mysql_query($query, $connection)))
			showerror();
	}
	
   $query7 = 'delete from contacts where plugin_version_id ='.$versionID;
    // Run the query
	if (!($result = @ mysql_query($query7, $connection)))
		showerror();	
	
	// delete successful, redirect to admin page	
	?>
	The plugin is deleted. <a href="pluginadmin.php">Back to plugin administration page</a>
	<?php 	  	
		// re-run the script "generate_plugin_xml.pl" to update plugins.xml file
		system("./run_generate_plugin_xml.csh");
}
?>
<p>
<?php include "../footer.php"; ?>
</p>
</body>
</html>

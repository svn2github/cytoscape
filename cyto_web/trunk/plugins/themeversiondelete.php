<?php //include "logininfo.inc"; ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Delete theme version</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
</head>
<body bgcolor="#ffffff">
<div id="topbar">
        <div class="title">Delete theme version</div>
</div>


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


<form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="deletetheme" id="deletetheme">
  <label></label>
  <label></label>
<table width="605" border="0">
    <tr>
      <td width="595">&nbsp;</td>
    </tr>
    <tr>
      <td>Are you sure you want to delete a theme version? </td>
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
		header('location:themeadmin.php');
		exit();
	}

	// User confirmed to delete the theme version

	// Include the DBMS credentials
	include 'db.inc';

	// Connect to the MySQL DBMS
	if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
		showerror();
	// Use the CyPluginDB database
	if (!mysql_select_db($dbName, $connection))
		showerror();

   	//get the theme_id
   	$query = 'select theme_id from theme_version where version_auto_id ='.$versionID;
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
	$theRow = @ mysql_fetch_array($result);
	$theme_id = $theRow['theme_id'];

	//echo "theme_id = ",$theme_id,'<br>';   	

	// get the set of theme_plugin_auto_id from table theme_plugin
   	$query = 'select theme_plugin_auto_id from theme_plugin where theme_version_id ='.$versionID;
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();	
	$theme_plugin_ids =''; 	
	while ($theRow = @ mysql_fetch_array($result)) {
		$theme_plugin_ids .= $theRow["theme_plugin_auto_id"].',';
	}
	$theme_plugin_ids = substr($theme_plugin_ids, 0, strlen($theme_plugin_ids)-1); //remove the last ','	

	//echo "theme_plugin_ids =",$theme_plugin_ids,'<br>';

	// begin to delete
	if (trim($theme_plugin_ids)!="") {		
	    $query = 'delete from theme_plugin where theme_plugin_auto_id IN ('.$theme_plugin_ids.')';
		// Run the query
		if (!($result = @ mysql_query($query, $connection)))
			showerror();	
	}

    $query = 'delete from theme_version where version_auto_id ='.$versionID;
    // Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();	

	//Are there any versions for this theme left?
    $query = 'select version_auto_id from theme_version where theme_id ='.$theme_id;
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();

	$versionCount = @ mysql_num_rows($result);
	if ($versionCount == 0) {
		// If there is no version left for this theme, also delete the row from the theme_list 
    	$query = 'delete from theme_list where theme_auto_id ='.$theme_id;
		// Run the query
		if (!($result = @ mysql_query($query, $connection)))
			showerror();
	}


	// delete successful, redirect to admin page	
	?>
	The theme is deleted. <a href="themeadmin.php">Back to theme administration page</a>
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

<?php include "logininfo.inc"; ?>
<?php

// Cytostaff edit the data in CyPluginDB

// If versionid is provided through URL, it is in edit mode
$versionID = NULL; // used for edit mode only

include 'clean.inc';

if (isset($_GET['versionid'])) {
	$versionID = cleanInt($_GET['versionid']);
	$pageTitle = "Edit plugin Info";
}
if (isset ($_POST['versionID'])) { // hidden field
	$versionID = cleanInt($_POST['versionID']);
}

?>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title><?php echo $pageTitle;?></title>
	<link rel="stylesheet" type="text/css" media="screen" href="http://cytoscape.org/css/cytoscape.css">
	<link rel="shortcut icon" href="http://cytoscape.org/images/cyto.ico">
	<style type="text/css">
<!--
.style3 {color: #FF0066}
.style4 {color: #FF0000}
-->
    </style>
</head>
<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title">Edit plugin info</div>
</div>
<div id="container">
<?php include "http://cytoscape.org/nav.php"; ?>
  
<?php
$mode = 'edit';
$tried = NULL;
if (isset ($_POST['tried'])) {
	$tried = 'yes';
}

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
	showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();

// initialize the variables
$name = NULL; // plugin name
$version = NULL;
$description = NULL;
$projectURL = NULL;
$category = NULL;
$releaseDate = NULL;
$month = NULL;
$day = NULL;
$year = NULL;

$cyVersion = NULL;
$names = NULL; // author names
$emails = NULL;
$affiliations = NULL;
$affiliationURLs = NULL;

// pull data out of DB for the given versionID
if ($tried == NULL) {
	
	include 'getplugindatafromdb.inc';

	$category = $db_category;
	$categoryID = $db_categoryID;
	$name = $db_name; // plugin name
	$plugin_id = $db_plugin_id;	
	$description = $db_description;
	$version = $db_version;
	$releaseDate = $db_releaseDate;
	$year = $db_year;
	$month = $db_month;
	$day = $db_day;
	$projectURL = $db_projectURL;

	$names = $db_names;
	$emails = $db_emails;
	$affiliations = $db_affiliations;
	$affiliationURLs = $db_affiliationURLs;

	$cyVersion = $db_cyVersion;
}

// Remember the form data. If form validation failed, these data will
// be used to fill the refreshed form. If pass, they will be saved into
// database
include "formPluginInfo_remember.inc";


//////////////////////// Form validation ////////////////////////
$validated = true;



if ($tried != NULL && $tried == 'yes') {
	include "formPluginInfo_validation.inc";
	
} // End of form validation


/////////////////////////////////  Form definition //////////////////////////

if (!($tried && $validated)) {
?>
</p>
<blockquote>
  <p><SPAN id="_ctl3_LabelRequired">	Fields denoted   by an (<span class="style4">*</span>) are required.</SPAN></p>
</blockquote>
<form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="submitplugin" id="submitplugin">

<?php
include "formPluginInfo.inc";
?>

<p align="center">
  <p align="center">
	  <input name="btnSubmit" type="submit" id="btnSubmit" value="Save" />
  &nbsp;&nbsp;&nbsp;&nbsp;</p>
</form>
	<p align="center">&nbsp;</p>
</p>
</form>

<?php


} 


else
	////////////////////////// form processing /////////////////////////
	// update the plugin info in CyPluginDB

	{
	//exit("Exit before processing");	
	
		// Get the category_id
		$query = 'SELECT category_id FROM categories WHERE name = "' . mysql_real_escape_string($category) . '"';
		// Run the query
		if (!($result = @ mysql_query($query, $connection)))
			showerror();

		$the_row = @ mysql_fetch_array($result);
		$category_id = $the_row['category_id'];

	// do updating

		
		// pull data out of DB for the given versionID	
		include 'getplugindatafromdb.inc';
		
		// update those need update
		
		// For table plugin_list
		$query1_prefix = 'update plugin_list set ';
		$query1_suffix = ' where plugin_auto_id = '.mysql_real_escape_string($db_plugin_id);

		$query1 = $query1_prefix;
		if ($name != $db_name) { // plugin name
			$query1 .='name ="'.mysql_real_escape_string($name).'",';
		} 
		if ($description != $db_description) {
			$query1 .='description ="'.mysql_real_escape_string($description).'",';
		}
		if ($projectURL != $db_projectURL) {
			$query1 .='project_url ="'.mysql_real_escape_string($projectURL).'",';
		}
		if ($category_id != $db_categoryID) {
			$query1 .='category_id ='.mysql_real_escape_string($category_id).',';
		}
		if ($query1 != $query1_prefix) {
			$query1 .='sysdat ='.'now()';		
		}
		
		// Run the query to update table plugin_list
		if ($query1 != $query1_prefix) { // 
			if (!(@ mysql_query($query1.$query1_suffix, $connection)))
				showerror();
		}

		// query to update table plugin_version
		$query2_prefix = 'update plugin_version set ';
		$query2_suffix = ' where version_auto_id = '.mysql_real_escape_string($versionID);

		$query2 = $query2_prefix;

		// Check plugin_file_id???
		if ($version != $db_version) { // plugin version
			$query2 .='version ="'.mysql_real_escape_string($version).'",';
		}
		if ($releaseDate != $db_releaseDate) {
			$query2 .='release_date ="'.mysql_real_escape_string($releaseDate).'",';
		}
		if ($cyVersion != $db_cyVersion) {
			$query2 .='cy_version ="'.mysql_real_escape_string($cyVersion).'",';
		}
				
		if ($query2 != $query2_prefix) {
			$query2 .='sysdat ='.'now()';		
		}
		//echo '<br>query2 = ',$query2,'<br>';
		
		// Run the querys to update table plugin_version
		if ($query2 != $query2_prefix) {
			if (!(@ mysql_query($query2.$query2_suffix, $connection)))
				showerror();
		}

		// For the author and plugin_author tables, first delete the old ones, then add new ones

		// delete the existing authors
		for ($i = 0; $i < count($db_author_ids); $i++) {
			$query = 'delete from plugin_author where author_id ='.mysql_real_escape_string($db_author_ids[$i]);
			// Run the query
			if (!(@ mysql_query($query, $connection)))
				showerror();
			$query = 'delete from authors where author_auto_id ='.mysql_real_escape_string($db_author_ids[$i]);
			// Run the query
			if (!(@ mysql_query($query, $connection)))
				showerror();
		}
		
		// Add new authors into tables
		for ($i = 0; $i < count($names); $i++) {
			if (!(empty($names[$i]) && empty($emails[$i]) && empty($affiliations[$i]) && empty($affiliationURLs[$i]))) {
				$query = 'INSERT INTO authors VALUES (0, "' .mysql_real_escape_string($names[$i]) . '", "' . mysql_real_escape_string($emails[$i]) . '","' . mysql_real_escape_string($affiliations[$i]) . '","' . mysql_real_escape_string($affiliationURLs[$i]) . '")';

				// Run the query
				if (!(@ mysql_query($query, $connection)))
					showerror();			

				$author_auto_id = mysql_insert_id($connection);
				$authorship_seq = $i;
			
				$query = 'INSERT INTO plugin_author VALUES (' . mysql_real_escape_string($versionID) . ', ' . mysql_real_escape_string($author_auto_id) . ',' . mysql_real_escape_string($authorship_seq) . ')';

				// Run the query
				if (!(@ mysql_query($query, $connection)))
					showerror();
			}
		}
?>

	Database is updated successfully!
	<p>Go back to <a href="pluginadmin.php">Plugin adminstration page</a></p>
	<?php
	}
	?>
<?php include "http://cytoscape.org/footer.php"; ?>
<br>
</body>
</html>

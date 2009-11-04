<?php

include "getPluginUniqueID.inc";

// mode = 'new', Data is submited by user
// mode = 'edit', Cytostaff edit the data in CyPluginDB
$mode = 'new'; // by default it is 'new'

// This page accept both zip/jar file, if jar file, validate its property
// If zip file, just load as is
$isZipFile = false;

// If versionid is provided through URL, it is in edit mode
$versionID = NULL; // used for edit mode only

include 'clean.inc';

if (isset ($_GET['versionid'])) {
	$versionID = cleanInt($_GET['versionid']);
}
if (isset ($_POST['versionID'])) { // hidden field
	$versionID = cleanInt($_POST['versionID']);
}

if ($versionID != NULL) {
	$mode = 'edit';
}

// Set the page title based on the mode
if ($mode == 'new') {
	$pageTitle = 'Submit plugin to Cytoscape';
} else
	if ($mode == 'edit') {
		$pageTitle = 'Edit plugin in CyPluginDB';
	} else {
		exit ('Unknown page mode, mode must be either new or edit');
	}
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title><?php echo $pageTitle;?></title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
	<style type="text/css">
<!--
.style4 {color: #FF0000}
-->
    </style>
</head>
<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title"><?php echo $pageTitle; ?></div>
</div>
<div id="container">
<?php include "../nav.php"; ?>

<?php

$tried = NULL;
if (isset ($_POST['tried'])) {
	$tried = 'yes';
}

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if ($mode == 'edit') {
	if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
		showerror();
} else // $mode == 'new'
	{
	if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
		showerror();
}
// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();

// initialize the variables
$releaseNote = NULL;
$releaseNoteURL = NULL;
$fileUpload = NULL;
$jarURL = NULL;
$sourceURL = NULL;
$license_required_checked = NULL;
$reference = NULL;
$comment = NULL;
$license = NULL;
$license_required = NULL;
$contactName = NULL;
$contactEmail = NULL;
$themeOnly = NULL;

// Case for 'edit', pull data out of DB for the given versionID
if (($tried == NULL) && ($mode == 'edit')) {

  ?><a href="pluginInfoEdit.php?versionid=<?php echo $versionID; ?>">Edit plugin info</a> <?php


	include 'getplugindatafromdb.inc';

	$categoryID = $db_categoryID;
	$plugin_id = $db_plugin_id;
	$releaseNote = $db_releaseNote;
	$releaseNoteURL = $db_releaseNoteURL;
	$jarURL = $db_jarURL;
	$sourceURL = $db_sourceURL;
	$reference = $db_reference;
	$license = $db_license;
	$license_required = $db_license_required;
	$comment = $db_comment;
	$contactName = $db_contactName;
	$contactEmail = $db_contactEmail;

	$pluginProps['pluginCategory'] = $db_category;
	$pluginProps['pluginName'] = $db_name;
	$pluginProps['pluginDescription'] = $db_description;
	$pluginProps['pluginVersion'] = $db_version;
	$pluginProps['releaseDate'] = $db_year . '-' . $db_month . '-' . $db_day;
	$pluginProps['projectURL'] = $db_projectURL;
	$authorInst['names'] = $db_names;
	$authorInst['insts'] = $db_affiliations;
	$pluginProps['pluginAuthorsInstitutions'] = $authorInst;
	$pluginProps['cytoscapeVersion'] = $db_cyVersion;
	$pluginProps['themeOnly'] = $db_themeOnly;

	if ($license_required == "yes") {
		$license_required_checked = "checked";
	}
}

// Remember the form data. If form validation failed, these data will
// be used to fill the refreshed form. If pass, they will be saved into
// database
include "formUserInput_remember.inc";

// Detect the action button clicked
$submitAction = NULL;
if (isset ($_POST['btnSubmit'])) {
	$submitAction = $_POST['btnSubmit'];
}

//////////////////////// Form validation ////////////////////////
$validated = true;

if ($tried != NULL && $tried == 'yes') {

	// mode = 'new'
	include 'formUserInput_validation.inc';	
	
	if (($mode == 'edit') && empty ($_FILES['filePlugin']['name'])) {
		//keep the existing file as is.
		include 'getplugindatafromdb.inc';
		$pluginProps = $db_pluginProps;
	} else {
		// if it is a jar file, get plugin properties from the jar file uploaded
		include "./pluginPropsUtil.inc";
		$pluginProps = getPluginProps($fileUpload['tmp_name']);
		$validated = (validatePluginProps($pluginProps) && validateManifestFile($fileUpload['tmp_name']));		
	}	
}

//////// End of form validation ////////////////////

// if the mode is 'new' (i.e. submit from user), check if the plugin already existed
if ($tried != NULL && $tried == 'yes' && $validated && $mode == 'new') {

	if (isPluginVersionExists($connection, $pluginProps)) {
		$validated = false;
	}
}
/////////////////////////////////  Form definition //////////////////////////

if (!($tried && $validated)) {
?>

<blockquote>
  <p><SPAN id="_ctl3_LabelRequired">	Fields denoted   by an (<span class="style4">*</span>) are required.</SPAN></p>
</blockquote>

<form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="submitplugin" id="submitplugin">

<?php
include "formUserInput.inc";
?>

<p align="center">
<?php

	if ($mode == 'new') {
?>
<input name="btnSubmit" type="submit" id="btnSubmit" value="Submit" />
<?php

	} else
		if ($mode == 'edit') {
?>	
  <p align="center">
	  <input name="btnSubmit" type="submit" id="btnSubmit" value="Save" />
	  &nbsp;&nbsp;
	  <input name="btnSubmit" type="submit" id="btnSubmit" value="Save and publish" />
	  &nbsp;&nbsp;
	  <input name="btnSubmit" type="submit" id="btnSubmit" value="Save and unpublish" />
  </p>
</form>
	<p align="center">&nbsp;</p>
	<?php


		}
?>
</p>
</form>

<?php


} // end of Form definition
else
	////////////////////////// form processing /////////////////////////
	// if mode = 'new', takes the details of the plugin from user and adds them to the tables of our CyPluginDB, with status = 'new'.
	// if mode = 'Edit', update the plugin info in CyPluginDB, change status based on button pressed.
{

		// Get the category_id
		$query = 'SELECT category_id FROM categories WHERE name = "' . $pluginProps['pluginCategory'] . '"';
		// Run the query
		if (!($result = @ mysql_query($query, $connection)))
			showerror();
	
		$the_row = @ mysql_fetch_array($result);
		$category_id = $the_row['category_id'];		
	
	
	// In case of edit, do updating
	if ($mode == 'edit') {
		//If Jar/Zip File is provided and validated, update everything

		// pull data out of DB for the given versionID	
		if (!empty ($_FILES['filePlugin']['name'])) {
			include 'getplugindatafromdb.inc';
		}

		// update those need update

		// For table plugin_list
		$query1_prefix = 'update plugin_list set ';
		$query1_suffix = ' where plugin_auto_id = ' . $db_plugin_id;

		$query1 = $query1_prefix;
		if ($pluginProps['pluginName'] != $db_name) {
			$query1 .= 'name ="' . $pluginProps['pluginName'] . '",';
		}
		if ($pluginProps['pluginDescription'] != $db_description) {
			$query1 .= 'description ="' . $pluginProps['pluginDescription'] . '",';
		}
		if ($license != $db_license) {
			$query1 .= 'license ="' . $license . '",';
		}
		if ($license_required != $db_license_required) {
			$query1 .= 'license_required ="' . $license_required . '",';
		}
		if ($pluginProps['projectURL'] != $db_projectURL) {
			$query1 .= 'project_url ="' . $pluginProps['projectURL'] . '",';
		}
		if ($category_id != $db_categoryID) {
			$query1 .= 'category_id =' . $category_id . ',';
		}
		if ($query1 != $query1_prefix) {
			$query1 .= 'sysdat =' . 'now()';
		}

		// Run the query to update table plugin_list
		if ($query1 != $query1_prefix) { // 
			if (!(@ mysql_query($query1 . $query1_suffix, $connection)))
				showerror();
		}

		// query to update table plugin_version
		$query2_prefix = 'update plugin_version set ';
		$query2_suffix = ' where version_auto_id = ' . $versionID;

		$query2 = $query2_prefix;

		// Check plugin_file_id???		
		if ($pluginProps['pluginVersion'] != $db_version) { // plugin version
			$query2 .= 'version ="' . $pluginProps['pluginVersion'] . '",';
		}
		if ($pluginProps['releaseDate'] != $db_releaseDate) {
			$query2 .= 'release_date ="' . $pluginProps['releaseDate'] . '",';
		}
		if ($pluginProps['themeOnly'] != $db_themeOnly) {
			$query2 .= 'theme_only ="' . $pluginProps['themeOnly'] . '",';
		}
		if ($releaseNote != $db_releaseNote) {
			$query2 .= 'release_note ="' . $releaseNote . '",';
		}
		if ($releaseNoteURL != $db_releaseNoteURL) {
			$query2 .= 'release_note_url ="' . $releaseNoteURL . '",';
		}
		if ($comment != $db_comment) {
			$query2 .= 'comment ="' . $comment . '",';
		}
		//if ($jarURL != $db_jarURL) {
		//	$query2 .='jar_url ="'.$jarURL.'",';
		//}
		if ($sourceURL != $db_sourceURL) {
			$query2 .= 'source_url ="' . $sourceURL . '",';
		}
		if ($pluginProps['cytoscapeVersion'] != $db_cyVersion) {
			$query2 .= 'cy_version ="' . $pluginProps['cytoscapeVersion'] . '",';
		}

		//Determine the status based on what button is clicked
		$status = NULL;
		if ($submitAction == 'Save') { // Edit, for save only, do not change status
			$status = 'Do not change';
		} else
			if ($submitAction == 'Save and publish') { // Edit, for save only, do not change status
				$status = 'published';
			}
		if ($submitAction == 'Save and unpublish') { // Edit, for save only, do not change status
			$status = 'pending';
		}
		if (($status != 'Do not change') && ($status != $db_status)) {
			$query2 .= 'status ="' . $status . '",';
		}

		if ($reference != $db_reference) {
			$query2 .= 'reference ="' . $reference . '",';
		}

		if ($query2 != $query2_prefix) {
			$query2 .= 'sysdat =' . 'now()';
		}

		// Run the querys to update table plugin_version
		if ($query2 != $query2_prefix) {
			if (!(@ mysql_query($query2 . $query2_suffix, $connection)))
				showerror();
		}

		// a jar file is selected, replace the old one with the new one		
		$plugin_file_auto_id = $db_pluginFileID;
		if ($fileUpload['name'] != NULL) { // A file is selected
			$md5 = md5_file($fileUpload['tmp_name']);
			$fileUpload_type = $fileUpload['type'];
			$fileUpload_name = $fileUpload['name'];

			$fileHandle = fopen($fileUpload['tmp_name'], "r");
			$fileContent = fread($fileHandle, $fileUpload['size']);
			$fileContent = addslashes($fileContent);

			// a file already existed, replace it

			$query_f3 = "update plugin_files set " .
			" file_data = '$fileContent', file_type = '$fileUpload_type', file_name = '$fileUpload_name', " .
			" md5 ='$md5'" .
			" where plugin_file_auto_id =" . $plugin_file_auto_id;

			// Run the query
			if (!(@ mysql_query($query_f3, $connection)))
				showerror();
		}

		// For the author and plugin_author tables, first delete the old ones, then add new ones

		// delete the existing authors
		for ($i = 0; $i < count($db_author_ids); $i++) {
			$query = 'delete from plugin_author where author_id =' . $db_author_ids[$i];
			// Run the query
			if (!(@ mysql_query($query, $connection)))
				showerror();
			$query = 'delete from authors where author_auto_id =' . $db_author_ids[$i];
			// Run the query
			if (!(@ mysql_query($query, $connection)))
				showerror();
		}

		// Add new authors into tables
		for ($i = 0; $i < count($pluginProps['pluginAuthorsInstitutions']['names']); $i++) {

			$query = 'INSERT INTO authors VALUES (0, "' . addslashes($pluginProps['pluginAuthorsInstitutions']['names'][$i]) . '", "' . NULL . '","' . $pluginProps['pluginAuthorsInstitutions']['insts'][$i] . '","' . NULL . '")';

			// Run the query
			if (!(@ mysql_query($query, $connection)))
				showerror();

			$author_auto_id = mysql_insert_id($connection);
			$authorship_seq = $i;

			$query = 'INSERT INTO plugin_author VALUES (' . $versionID . ', ' . $author_auto_id . ',' . $authorship_seq . ')';

			// Run the query
			if (!(@ mysql_query($query, $connection)))
				showerror();
		}

		// query to update table contacts
		$query5_prefix = 'update contacts set ';
		$query5_suffix = ' where plugin_version_id = ' . $versionID;

		$query5 = $query5_prefix;

		if ($contactName != $db_contactName) {
			$query5 .= 'name ="' . $contactName . '",';
		}
		if ($contactEmail != $db_contactEmail) {
			$query5 .= 'email ="' . $contactEmail . '",';
		}

		if ($query5 != $query5_prefix) {
			$query5 .= 'sysdat =' . 'now()';
		}

		// Run the querys to update table contacts
		if ($query5 != $query5_prefix) {
			if (!(@ mysql_query($query5 . $query5_suffix, $connection)))
				showerror();
		}
?>
		Database is updated successfully!
		<p>Go back to <a href="pluginadmin.php">Plugin adminstration page</a></p>
		<?php


		// re-run the script "generate_plugin_xml.pl" to update plugins.xml file
		system("./run_generate_plugin_xml.csh");
	}
	// End of case for 'edit' mode

	if ($mode == 'new') {
//exit("<br>Exit here line 587");

		//$submitAction == 'Submit', accept data submited from user
		//process the data and Save the data into DB.

		include "uploadNewPluginData.inc";

	} 
	// end of form processing
}
?>

<?php include "../footer.php"; ?>
<br>
</body>
</html>

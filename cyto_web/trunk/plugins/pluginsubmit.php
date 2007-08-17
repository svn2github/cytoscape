<?php


// mode = 'new', Data is submited by user
// mode = 'edit', Cytostaff edit the data in CyPluginDB
$mode = 'new'; // by default it is 'new'

// If versionid is provided through URL, it is in edit mode
$versionID = NULL; // used for edit mode only

if (isset ($_GET['versionid'])) {
	$versionID = $_GET['versionid'];
}
if (isset ($_POST['versionID'])) { // hidden field
	$versionID = $_POST['versionID'];
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
.style3 {color: #FF0066}
.style4 {color: #FF0000}
-->
    </style>
</head>
<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title"><?php echo $pageTitle; ?></div>
</div>

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
//$name = NULL; // plugin name
//$version = NULL;
//$description = NULL;
//$projectURL = NULL;
//$category = NULL;
//$releaseDate = NULL;
//$month = NULL;
//$day = NULL;
//$year = NULL;
$releaseNote = NULL;
$releaseNoteURL = NULL;
$fileUpload = NULL;
$jarURL = NULL;
$sourceURL = NULL;
//$Cy2p0_checked = NULL;
//$Cy2p1_checked = NULL;
//$Cy2p2_checked = NULL;
//$Cy2p3_checked = NULL;
//$Cy2p4_checked = NULL;
//$Cy2p5_checked = NULL;
$license_required_checked = NULL;
//$cyVersion = NULL;
$reference = NULL;
$comment = NULL;
$license = NULL;
$license_required = NULL;
//$names = NULL; // author names
//$emails = NULL;
//$affiliations = NULL;
//$affiliationURLs = NULL;
$contactName = NULL;
$contactEmail = NULL;

// Case for 'edit', pull data out of DB for the given versionID
if (($tried == NULL) && ($mode == 'edit')) {

	include 'getplugindatafromdb.inc';

	//$category = $db_category;
	$pluginProps['pluginCategory'] = $db_category;
	$categoryID = $db_categoryID;

	//$name = $db_name; // plugin name
	$pluginProps['pluginName'] = $db_name;
	$plugin_id = $db_plugin_id;
	//$description = $db_description;
	$pluginProps['pluginDescription'] = $db_description;

	//$version = $db_version;
	$pluginProps['pluginVersion'] = $db_version;

	//$releaseDate = $db_releaseDate;
	$pluginProps['releaseDate'] = $db_year . '-' . $db_month . '-' . $db_day;

	//$year = $db_year;
	//$month = $db_month;
	//$day = $db_day;
	//$projectURL = $db_projectURL;
	$pluginProps['projectURL'] = $db_projectURL;

	$releaseNote = $db_releaseNote;
	$releaseNoteURL = $db_releaseNoteURL;

	$authorInst['names'] = $db_names;
	$authorInst['insts'] = $db_affiliations;
	$pluginProps['pluginAuthorsInstitutions'] = $authorInstL;
	//$names = $db_names;
	//$emails = $db_emails;
	//$affiliations = $db_affiliations;
	//$affiliationURLs = $db_affiliationURLs;

	//fileUpload

	$jarURL = $db_jarURL;
	$sourceURL = $db_sourceURL;
	//$cyVersion = $db_cyVersion;
	$pluginProps['cytoscapeVersion'] = $db_cyVersion;

	$reference = $db_reference;
	$license = $db_license;
	$license_required = $db_license_required;
	$comment = $db_comment;

	//$theVersions = preg_split("{,}", $db_cyVersion); // Split into an array

	if ($license_required == "yes") {
		$license_required_checked = "checked";
	}
}

// Remember the form data. If form validation failed, these data will
// be used to fill the refreshed form. If pass, they will be saved into
// database

if (isset ($_FILES['filePlugin'])) {
	$fileUpload = $_FILES['filePlugin'];
}

if (isset ($_POST['taReleaseNote'])) {
	$releaseNote = addslashes($_POST['taReleaseNote']);
}

if (isset ($_POST['tfReleaseNoteURL'])) {
	$releaseNoteURL = $_POST['tfReleaseNoteURL'];
}

if (isset ($_POST['tfSourceURL'])) {
	$sourceURL = $_POST['tfSourceURL'];
}

if (isset ($_POST['taReference'])) {
	$reference = addslashes($_POST['taReference']);
}

if (isset ($_POST['taLicense'])) {
	$license = addslashes($_POST['taLicense']);
}
if (isset ($_POST['chkLicense_required'])) {
	$license_required_checked = "checked";
	$license_required = "yes";
} else {
	$license_required = "no";
}
if (isset ($_POST['taComment'])) {
	$comment = addslashes($_POST['taComment']);
}

//Contact
if (isset ($_POST['tfContactName'])) {
	$contactName = addslashes($_POST['tfContactName']);
}
if (isset ($_POST['tfContactEmail'])) {
	$contactEmail = addslashes($_POST['tfContactEmail']);
}

// Detect the action button clicked
$submitAction = NULL;
if (isset ($_POST['btnSubmit'])) {
	$submitAction = $_POST['btnSubmit'];
}

//////////////////////// Form validation ////////////////////////
$validated = true;

if ($tried != NULL && $tried == 'yes') {

	//if mode == 'new', A jar/zip file is required
	//if mode == 'edit', if a jar/zip file is not provided,
	//the existing file will not be updated. 
	if (($mode == 'new') && empty ($_FILES['filePlugin']['name'])) {
		$validated = false;
?>
		Error: A jar/zip file is required.<br>
		<?php

	}
	elseif (($mode == 'edit') && empty ($_FILES['filePlugin']['name'])) {
		//keep the existing file as is.
		include 'getplugindatafromdb.inc';
		$pluginProps = $db_pluginProps;		
	} else {
		// get plugin properties from the jar/zip file uploaded
		include "pluginPropsUtil.inc";
		$pluginProps = getPluginProps($fileUpload['tmp_name']);

		$validated = validatePluginProps($pluginProps);
	}
}

//////// End of form validation ////////////////////

// if the mode is 'new' (i.e. submit from user), check if the plugin already existed
if ($tried != NULL && $tried == 'yes' && $validated && $mode == 'new') {
	$query = 'SELECT version_auto_id FROM categories, plugin_list, plugin_version' .
	' WHERE categories.category_id = plugin_list.category_id ' .
	'       and plugin_list.plugin_auto_id = plugin_version.plugin_id ' .
	'       and categories.name ="' . $pluginProps['pluginCategory'] . "\" " .
	'		and plugin_list.name = "' . $pluginProps['pluginName'] . "\" " .
	'		and plugin_version.version = "' . $pluginProps['pluginVersion'] . "\"";

	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();

	if (@ mysql_num_rows($result) != 0) {
		$validated = false;
?>
			Error: The version of this plugin already existed.<br>
			<?php


	}
}
//$validated = true; // debug only
/////////////////////////////////  Form definition //////////////////////////

if (!($tried && $validated)) {
?>
</p>
<blockquote>
  <p><SPAN id="_ctl3_LabelRequired">	Fields denoted   by an (<span class="style4">*</span>) are required.</SPAN></p>
</blockquote>
<form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="submitplugin" id="submitplugin">
  <table width="878" border="0">
  <tr>
    <td width="208"><div align="right"></div></td>
    <td width="660"><label><?php if ($mode == 'edit') {echo 'If no file is selected, the existing one will be kept as is.'; } ?></label></td>
  </tr>
  <tr>
    <td><div align="right"><span class="style4">*</span>Jar/Zip File </div></td>
    <td><input name="filePlugin" type="file" id="filePlugin" size="80" /></td>
  </tr>
  <tr>
    <td><div align="right"></div></td>
    <td><label></label></td>
  </tr>
  <tr>
    <td><div align="right">Contact</div></td>
    <td><table width="660" border="0">
      <tr>
        <td width="420"><div align="center"> Name</div></td>
        <td width="230"><div align="center">e-mail (not made public) </div></td>
      </tr>
      <tr>
        <td><label>
          <input name="tfContactName" type="text" id="tfContactName" size="70" value ="<?php echo htmlentities(stripslashes($contactName)) ?>" />
        </label></td>
        <td><input name="tfContactEmail" type="text" id="tfContactEmail" size="30" value ="<?php echo $contactEmail ?>" /></td>
        </tr>
    </table></td>
  </tr>

  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td><div align="right">Release note</div></td>
    <td><label>
      <textarea name="taReleaseNote" cols="80" rows="3" id="taReleaseNote" ><?php echo $releaseNote ?></textarea>
    </label></td>
  </tr>
  <tr>
    <td><div align="right">Release note URL</div></td>
    <td><input name="tfReleaseNoteURL" type="text" id="tfReleaseNoteURL" value ="<?php echo $releaseNoteURL ?>" size="80"></td>
  </tr>
  <tr>
    <td><div align="right">Source URL</div></td>
    <td><input name="tfSourceURL" type="text" id="tfSourceURL" value ="<?php echo $sourceURL ?>" size="80"></td>
  </tr>
  <tr>
    <td><div align="right">Reference</div></td>
    <td><textarea name="taReference" cols="80" rows="3" id="taReference"><?php echo $reference; ?></textarea></td>
  </tr>
  <tr>
    <td><div align="right">License</div></td>
    <td><label>
      <textarea name="taLicense" cols="80" rows="3" id="taLicense"><?php echo $license ?></textarea>
    </label></td>
  </tr>
  <tr>
    <td><div align="right">License required</div></td>
    <td><label>
      <input type="checkbox" name="chkLicense_required" id="chkLicense_required" value="checkbox" <?php echo $license_required_checked ?> />
    </label>	</td>
  </tr>
  <tr>
    <td><div align="right">Comment</div></td>
    <td><label>
      <textarea name="taComment" cols="80" rows="2" id="taComment"><?php echo $comment; ?></textarea>
    </label></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><input name="tried" type="hidden" id="tried" value="yes">
      <input name="versionID" type="hidden" id="versionID" value="<?php echo $versionID; ?>"></td>
  </tr>
</table>
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
		if ($pluginProps['pluginVesion'] != $db_version) { // plugin version
			$query2 .= 'version ="' . $pluginProps['pluginVesion'] . '",';
		}
		if ($pluginProps['releaseDate'] != $db_releaseDate) {
			$query2 .= 'release_date ="' . $pluginProps['releaseDate'] . '",';
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
		//echo '<br>query2 = ',$query2,'<br>';

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
		?>
		Database is updated successfully!
		<p>Go back to <a href="pluginadmin.php">Plugin adminstration page</a></p>
		<?php

		// re-run the script "generate_plugin_xml.pl" to update plugins.xml file
		system("./run_generate_plugin_xml.csh");
	}
	// End of case for 'edit' mode

	if ($mode == 'new') {

		//$submitAction == 'Submit', accept data submited from user
		//process the data and Save the data into DB.

		//Load the Jar file to DB
		$plugin_file_auto_id = NULL;

		if ($fileUpload['name'] != NULL) {
			//echo "A file is selected";`
			$md5 = md5_file($fileUpload['tmp_name']);
			$fileUpload_type = $fileUpload['type'];
			$fileUpload_name = $fileUpload['name'];

			$fileHandle = fopen($fileUpload['tmp_name'], "r");
			$fileContent = fread($fileHandle, $fileUpload['size']);
			$fileContent = addslashes($fileContent);

			$dbQuery = "INSERT INTO plugin_files VALUES ";
			$dbQuery .= "(0, '$fileContent', '$fileUpload_type', '$fileUpload_name', '$md5')";
			//echo "<br>dbQuery = " . $dbQuery . "<br>";
			// Run the query
			if (!(@ mysql_query($dbQuery, $connection)))
				showerror();

			echo "<br><b>File uploaded successfully</b><br>";
			$plugin_file_auto_id = mysql_insert_id($connection);
		}

		$plugin_auto_id = NULL;
		//Check if there is an old version of this plugin in DB
		$dbQuery = 'SELECT plugin_auto_id FROM plugin_list ' .
		'         WHERE plugin_list.name = "' . $pluginProps['pluginName'] . '" and category_id =' . $category_id;

		// Run the query
		if (!($result = @ mysql_query($dbQuery, $connection)))
			showerror();

		if (@ mysql_num_rows($result) != 0) {
			//There is an old version in the DB, update the row in the table plugin_list

			$the_row = @ mysql_fetch_array($result);
			$plugin_auto_id = $the_row['plugin_auto_id'];
			//echo "There is an old version of this plugin in the DB, plugin_auto_id =" . $plugin_auto_id . "<br>";

			// Update the table "plugin_list"  
			$dbQuery = 'UPDATE plugin_list ' .
			'SET description = "' . $pluginProps['pluginDescription'] . '",' .
			'project_url ="' . $pluginProps['projectURL'] . '",' .
			'license ="' . $license . '",' .
			'license_required ="' . $license_required . '" ' .
			'WHERE plugin_auto_id = ' . $plugin_auto_id;

			// Run the query
			if (!($result = @ mysql_query($dbQuery, $connection)))
				showerror();

		} else {
			//This is a new plugin, add a row in the table plugin_list

			$plugin_unique_id = -1;

			$dbQuery = 'INSERT INTO plugin_list VALUES ' .
			'(0, "' . $pluginProps['pluginName'] . '", "' . $plugin_unique_id . '", "' . addslashes($pluginProps['pluginDescription']) . '", "' . $license . '", "' . $license_required . '", "' . $pluginProps['projectURL'] . '",' .
			$category_id . ',now())';

			// Run the query
			if (!($result = @ mysql_query($dbQuery, $connection)))
				showerror();

			$plugin_auto_id = mysql_insert_id($connection);
		}

		// Insert a row into table plugin_version
		$status = 'new';
		$dbQuery = 'INSERT INTO plugin_version VALUES (0, ' . $plugin_auto_id . ', ';
		if ($plugin_file_auto_id == NULL) {
			$dbQuery .= 'NULL';
		} else {
			$dbQuery .= $plugin_file_auto_id;
		}
		$dbQuery .= ',"' . $pluginProps['pluginVersion'] . '",\'' .
		$pluginProps['releaseDate'] . '\',"' . $releaseNote . '","' . $releaseNoteURL . '","' . $comment . '","' . $jarURL . '","' .
		$sourceURL . '","' . $pluginProps['cytoscapeVersion'] . '","' . $status . '","' . $reference . '", now())';

		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();

		$version_auto_id = mysql_insert_id($connection);
		// insert rows into author tables (authors and plugin_author)

		$authorCount = count($pluginProps['pluginAuthorsInstitutions']['names']);

		for ($i = 0; $i < $authorCount; $i++) {
			$dbQuery = 'INSERT INTO authors VALUES (0, "' . addslashes($pluginProps['pluginAuthorsInstitutions']['names'][$i]) . '", "' . 'null' . '","' . $pluginProps['pluginAuthorsInstitutions']['insts'][$i] . '","' . 'null' . '")';

			// Run the query
			if (!(@ mysql_query($dbQuery, $connection)))
				showerror();

			$author_auto_id = mysql_insert_id($connection);

			$authorship_seq = $i;
			$dbQuery = 'INSERT INTO plugin_author VALUES (' . $version_auto_id . ', ' . $author_auto_id . ',' . $authorship_seq . ')';

			// Run the query
			if (!(@ mysql_query($dbQuery, $connection)))
				showerror();
		}
?>
	Thank you for submitting your plugin to Cytoscape. Cytoscape staff will review the data  and publish it on the cytoscape website. If your-mail address is provided, you will get confirmation via e-mail.
	<p>Go back to <a href="index.php">Back to cytoscape plugin page</a></p>
	<?php

		// Send a confirmation e-mail to user
		// Also cc to cytostaff,  new plugin is uploaded
		sendConfirmartionEmail($name, $emails[0]);

	} // end of form processing
}
?>

<?php include "../footer.php"; ?>
<br>
</body>
</html>

<?php

$mode = 'new';

// If versionid is provided through URL, it is in edit mode
$versionID = NULL; // used for edit mode only

include 'clean.inc';
if (isset ($_GET['versionid'])) {
	$versionID = cleanInt($_GET['versionid']);
}

if ($versionID != NULL) {
	$mode = 'edit';
}

// Set the page title based on the mode
if ($mode == 'new') {
	$pageTitle = 'Submit plugin URL to Cytoscape';
} else
	if ($mode == 'edit') {
		$pageTitle = 'Edit plugin URL in CyPluginDB';
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
	<div class="title">Submit a Plugin URL to Cytoscape</div>
</div>

<?php include "../nav.php"; ?>
<?php
include "getPluginUniqueID.inc";

$tried = NULL;
if (isset ($_POST['tried'])) {
	$tried = 'yes';
}

// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
	showerror();

// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();

// Define a new variable
$isPluginURLSubmission = true;

// initialize the variables for user input
$releaseNote = NULL;
$releaseNoteURL = NULL;
$fileUpload = NULL; // not used for URL submission
$jarURL = NULL;	// not used
$sourceURL = NULL; // not used
$license_required_checked = NULL;
$reference = NULL;
$comment = NULL;
$license = NULL;
$license_required = NULL;
$contactName = NULL;
$contactEmail = NULL;

// initialize the variables for pluginInfo
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

// Remember the form data. If form validation failed, these data will
// be used to fill the refreshed form. If pass, they will be saved into
// database
include "formPluginInfo_remember.inc";
include "formUserInput_remember.inc";


//////////////////////// Form validation ////////////////////////
$validated = true;
if ($tried != NULL && $tried == 'yes') {
		
	include "formPluginInfo_validation.inc";
	include 'formUserInput_validation.inc';
		
	if ($validated) {
		// make sure project URL is valid
		//if () {

		// ????

		//}
		//else {
		//	$validated = false;
		//	echo "<strong><br>Error: Project URL is null or invalid!</strong>";
		//}
	}
	if ($validated) {
		$pluginProps['pluginName'] = trim($name);
		$pluginProps['pluginDescription']=trim($description);
		$pluginProps['pluginVersion']=trim($version);
		$pluginProps['cytoscapeVersion']=trim($cyVersion);
		$pluginProps['pluginCategory']=trim($category);
		$pluginProps['projectURL']=trim($projectURL);
		
		//
		if (trim($names[1] == "" && trim($affiliations[1])== "")) {
			array_splice($names,1,1);
			array_splice($affiliations,1,1); 
		}
		if (trim($names[0] == "" && trim($affiliations[0])== "")) {
			$names = null;
			$affiliations = null;
		}		
		$pluginAuthorsInstitutions['names'] = $names;
		$pluginAuthorsInstitutions['insts'] = $affiliations;
		$pluginProps['pluginAuthorsInstitutions']=$pluginAuthorsInstitutions;
		
		$pluginProps['releaseDate']=trim($year).'-'.trim($month).'-'.trim($day);
		$pluginProps['minimumJavaVersion'] = '0.0';
		
	}
}
//////////////////// End of form validation ////////////////////

// if the mode is 'new' (i.e. submit from user), check if the plugin already existed
if ($tried != NULL && $tried == 'yes' && $validated && $mode == 'new') {
	include "./pluginPropsUtil.inc";

	if (isPluginVersionExists($connection, $pluginProps)) {
		$validated = false;
	}
}

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
	include "formUserInput.inc"; 
	?>
	  <p align="center">
	
	<input name="btnSubmit" type="submit" id="btnSubmit" value="Submit" />
	 </p>
	</form>
	<?php
}
else {

	// Get the category_id
	$query = 'SELECT category_id FROM categories WHERE name = "' . $pluginProps['pluginCategory'] . '"';
	
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
	
	$the_row = @ mysql_fetch_array($result);
	$category_id = $the_row['category_id'];		

	//echo "category_id = ",$category_id, "<br>";
	$pluginProps['themeOnly'] = false; // this property does not apply for plugin URL, always false
	
	include "uploadNewPluginData.inc";
}
?>

<?php include "../footer.php"; ?>
</body>
</html>

<?php
?>

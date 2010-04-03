<?php
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Submit a Zip file to Cytoscape</title>
	<link rel="stylesheet" type="text/css" media="screen" href="http://cytoscape.org/css/cytoscape.css">
	<link rel="shortcut icon" href="http://cytoscape.org/images/cyto.ico">
	<style type="text/css">
<!--
.style4 {color: #FF0000}
-->
    </style>
</head>
<body bgcolor="#ffffff">
<div id="container">
<div id="topbar">
	<div class="title">Submit a Zip file to Cytoscape</div>
</div>
<?php include "http://cytoscape.org/nav.php"; ?>
<?php
include "getPluginUniqueID.inc";

$mode = 'new'; // it is 'new' only

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

// initialize the variables for user input
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
		// make sure the file has extension zip
		$isZipFile = false;
		$fileExt = ".xxx";
		if (strlen($fileUpload['name']) > 3) {
			$fileExt = substr($fileUpload['name'], strlen($fileUpload['name'])-4); 
		} 		
		if (strcasecmp($fileExt,'.zip') == 0) {
			$isZipFile = true;
		}
		else {
			$validated = false;
			echo "<strong><br>Error: The file doesnot have an extension .zip!</strong>";
		}
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
		
		include "./pluginPropsUtil.inc";
	
		$validated = (validatePluginProps($pluginProps));		
	}
}
//////////////////// End of form validation ////////////////////

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
	//echo "Form is validated, process the data<br>";

	// Get the category_id
	$query = 'SELECT category_id FROM categories WHERE name = "' . $pluginProps['pluginCategory'] . '"';
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
	
	$the_row = @ mysql_fetch_array($result);
	$category_id = $the_row['category_id'];		

	//echo "category_id = ",$category_id, "<br>";

	include "uploadNewPluginData.inc";
}
?>

<?php include "http://cytoscape.org/footer.php"; ?>
</body>
</html>

<?php
?>

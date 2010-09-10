<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="http://chianti.ucsd.edu/~kono/cytoscape/css/main.css" type="text/css" rel="stylesheet" media="screen">
<title>Submit a Zip file to Cytoscape</title>
<script type="text/javascript" 
src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
<script type="text/javascript" src="http://chianti.ucsd.edu/~kono/cytoscape/js/menu_generator.js"></script>
	<style type="text/css">
<!--
.style4 {color: #FF0000}
-->
    </style>
</head>

<body>
<div id="container"> 
  <script src="http://chianti.ucsd.edu/~kono/cytoscape//js/header.js"></script>


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

<script src="http://chianti.ucsd.edu/~kono/cytoscape/js/footer.js"></script> 
</body>
</html>

<?php
?>

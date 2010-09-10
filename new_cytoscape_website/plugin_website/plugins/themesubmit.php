<?php
function getThemeDataFromFile($uploadFile) {
	//echo "Inside function getThemeDataFromFile()<br>";
	//echo "tmp_file = ", $tmp_file,'<br>';
	//echo "fileName = ", $_FILES['themefile']['name'],"<br>";
	
	//$themeData['test'] = 'test';
	
	$fp = fopen($uploadFile, 'r');

	if (!$fp) {
		exit("Error! Couldn't open the file.");
	}
	
	$isThemeFile = false;
	
	while ($line = fgets($fp)) {
		if (($line == "") || (strstr($line, '#') == $line))
			continue;

		if (stristr($line, 'Cytoscape theme definition') == $line) { // the line starts with xxx
			$isThemeFile = true;
		}

		if (stristr($line, 'themeName') == $line) { // the line starts with themeName
			$pos = strpos($line, "="); // find first "="
			$themeData['name'] = trim(substr($line, $pos +1));
		}
		if (stristr($line, 'themeVersion') == $line) {
			$pos = strpos($line, "=");
			$themeData['version'] = trim(substr($line, $pos +1));
		}
		if (stristr($line, 'themeDescription') == $line) {
			$pos = strpos($line, "=");
			$themeData['description'] = trim(substr($line, $pos +1));
		}
		if (stristr($line, 'cytoscapeVersion') == $line) {
			$pos = strpos($line, "=");
			$themeData['cyVersion'] = trim(substr($line, $pos +1));
		}
		if (stristr($line, 'releaseDate') == $line) {
			$pos = strpos($line, "=");
			$themeData['releaseDate'] = trim(substr($line, $pos +1));
		}
		
		if (stristr($line, 'plugin') == $line) {
			$pos = strpos($line, "=");
			
			$tmpArray = preg_split("{:}", trim(substr($line, $pos +1))); // Split into an array						
			$pluginData['category'] = $tmpArray[0];
			$pluginData['name'] = $tmpArray[1];
			$pluginData['version'] = $tmpArray[2];
			
			if (!isset($themeData['pluginGroup'])) {
				$pluginGroup[] = $pluginData;
			 	$themeData['pluginGroup'] = $pluginGroup;
			}
			else {
				$pluginGroup = $themeData['pluginGroup'];
				$pluginGroup[] = $pluginData;
				$themeData['pluginGroup'] = $pluginGroup;
			}			
		}
		
		if (stristr($line, 'themeAuthor') == $line) {// not saved for now
			$pos = strpos($line, "=");
			$themeData['themeAuthor'] = trim(substr($line, $pos +1));
		}
		if (stristr($line, 'contactEmail') == $line) {
			$pos = strpos($line, "=");
			$themeData['contactEmail'] = trim(substr($line, $pos +1));
		}
	}
	
	if (!fclose($fp)) {
		exit("Error! Couldn't close the file.");
	}

	if (!$isThemeFile) {
		echo "This may not be a theme file. Theme file should start with the line 'Cytoscape theme definition'!";
		exit("");
	}

	
	//Create test data
	/*
	$themeData['name'] = "themeName";
	$themeData['version'] = '0.1';
	$themeData['description'] = "themeDescription";
	$themeData['cyVersion'] = "2.6,2.7";
	$themeData['releaseDate'] = "2008-01-10";

	$pluginData['name'] = "MCODE";
	$pluginData['category'] = "Analysis";
	$pluginData['version'] = "1.2";

	$pluginGroup[] = $pluginData;

	$pluginData['name'] = "GroupTool";
	$pluginData['category'] = "Other";
	$pluginData['version'] = "1.0";

	$pluginGroup[] = $pluginData;
	
	$themeData['pluginGroup'] = $pluginGroup;
	*/
	return $themeData;	
}


function printThemeData($themeData) {
	if ($themeData == null) {
		echo "themeData = null<br>";
		return;
	}
	echo "themeName = ", $themeData['name'], "<br>";
	echo "themeVersion = ", $themeData['version'], "<br>";
	echo "themeDescription = ", $themeData['description'], "<br>";
	echo "cyVersion = ",$themeData['cyVersion'],'<br>';
	echo "releaseDate = ",$themeData['releaseDate'],'<br>';

	$pluginGroup = $themeData['pluginGroup'];
	for ($i=0; $i< count($pluginGroup); $i++) {
		$pluginData = $pluginGroup[$i];
		echo "pluginName = ",$pluginData['name'],'<br>';
		echo "pluginCategory = ",$pluginData['category'],'<br>';
		echo "pluginVersion = ",$pluginData['version'],'<br>';	
	}
	echo "themeAuthor = ",$themeData['themeAuthor'],'<br>';
	echo "contactEmail = ",$themeData['contactEmail'],'<br>';
}


function getPluginVersionID($connection, $pluginData) {
		// Get plugin_version_id based on the plugin -- 'category'', 'name' and 'version'	

		// Get the category_id
		$query = 'SELECT category_id FROM categories WHERE name = "' . $pluginData['category'] . '"';
		// Run the query
		if (!($result = @ mysql_query($query, $connection)))
			showerror();
	
		$the_row = @ mysql_fetch_array($result);
		$category_id = $the_row['category_id'];		
		
		if (empty($category_id)) {
			echo "<br>Undefined Plugin category: ",$pluginData['category'],'<br>';	
			return "";
		}
		
		//
		$query = 'SELECT version_auto_id FROM plugin_list,plugin_version WHERE plugin_list.category_id ='.$category_id.
				' and plugin_list.plugin_auto_id = plugin_version.plugin_id '.
				'and plugin_list.name ="'.$pluginData['name'].'" and plugin_version.version = '.$pluginData['version'];

		//Run the query
		if (!($result = @ mysql_query($query, $connection)))
			showerror();
	
		$the_row = @ mysql_fetch_array($result);
		$version_id = $the_row['version_auto_id'];		

		return $version_id;
}

function validateThemeData($connection, $themeData) {

	$retValue = true;
	
	$theme_version_id = getThemeVersionID($connection, $themeData);
	if (!empty($theme_version_id)) {
		echo "Error: Theme already existed!<br>";	
		$retValue = false;	
	}

	if (count($themeData['pluginGroup']) == 0) {
		echo "Error: Plugin group is not defined!<br>";	
		$retValue = false;			
	}

	$pluginGroup = $themeData['pluginGroup'];
	for ($i=0; $i<count($pluginGroup); $i++) {
		$pluginVersionID = getPluginVersionID($connection, $pluginGroup[$i]);
 		if (empty($pluginVersionID)) {
 			echo "Error: Unknown plugin-version: ",$pluginGroup[$i]['name'],'--',$pluginGroup[$i]['version'],"<br>";	
 			$retValue = false;	
 		}
	}

	return $retValue;
}

function getThemeVersionID($connection, $themeData) {
		$query = 'SELECT version_auto_id FROM theme_list, theme_version WHERE theme_list.theme_auto_id = theme_version.theme_id ' .
				' and theme_list.name = "' . $themeData['name'] .'"' .
				' and theme_version.version = ' . $themeData['version'];
								
		// Run the query
		if (!($result = @ mysql_query($query, $connection)))
			showerror();
	
		$the_row = @ mysql_fetch_array($result);
		$theme_version_id = $the_row['version_auto_id'];		

	return $theme_version_id;
}

function getThemeUniqueID($connection) {
	$retValue = -1;
	$dbQuery = "select max(unique_id) as current_max from theme_list";	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) != 0) {
			$the_row = @ mysql_fetch_array($result);
			$current_max = $the_row['current_max'];
			$retValue = $current_max + 1; 
	}

	return $retValue;
}


function saveThemeToDB($connection, $themeData) {
	//echo "Enter saveThemeToDB() <br>";
	//printThemeData($themeData);
	echo "<br><br>";

	//Check if there is an old version of this theme in DB
	$dbQuery = 'SELECT theme_auto_id FROM theme_list ' .
		'         WHERE name = "' . $themeData['name'] . '"';

	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) != 0) {
			//There is an old version in the DB, update the row in the table theme_list
			
			$the_row = @ mysql_fetch_array($result);
			$theme_auto_id = $the_row['theme_auto_id'];
			echo "There is an old version of this theme in the DB, theme_auto_id =" . $theme_auto_id . "<br>";

			$theProjuctURL = "";
			if (isset($pluginProps['projectURL'])) {
				$theProjuctURL = $pluginProps['projectURL'];	
			}

			// Update the table "theme_list"  
			$dbQuery = 'UPDATE theme_list ' .
			'SET description = "' . addslashes($themeData['description']) . '" ' .
			'WHERE theme_auto_id = ' . $theme_auto_id;
			
			// Run the query
			if (!($result = @ mysql_query($dbQuery, $connection)))
				showerror();
						
	}
	else {
			//echo "This is a new theme, add a row in the table theme_list.<br>";
			//This is a new theme, add a row in the table theme_list

			$theme_unique_id = getThemeUniqueID($connection);
			
			$dbQuery = 'INSERT INTO theme_list VALUES ' .
			'(0, "' . $themeData['name'] . '", ' . $theme_unique_id . ', "' . addslashes($themeData['description']). '",now())';

			// Run the query
			if (!($result = @ mysql_query($dbQuery, $connection)))
				showerror();

			$theme_auto_id = mysql_insert_id($connection);
	}

	// Insert a row into table plugin_version
	$dbQuery = 'INSERT INTO theme_version VALUES (0, ' . $theme_auto_id .
		',\'' . $themeData['cyVersion'] . '\',' .$themeData['version'] . ',\'' . $themeData['releaseDate'] .'\',\'public\',now())';
	
	//echo "query = ",$dbQuery,'<br>';	
	// Run the query
	if (!(@ mysql_query($dbQuery, $connection)))
		showerror();

	$theme_version_auto_id = mysql_insert_id($connection);

	// Insert rows into table theme_plugin
	$pluginGroup = $themeData['pluginGroup'];
	for ($i=0; $i< count($pluginGroup); $i++) {
		$pluginVersionID = getPluginVersionID($connection, $pluginGroup[$i]);
		
		$dbQuery = 'INSERT INTO theme_plugin VALUES (0, ' . $theme_version_auto_id .','. $pluginVersionID.')';
	
		//echo "query = ",$dbQuery,'<br>';	
		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();
	}
}


// mode = 'new', Data is submited by user
// mode = 'edit', Cytostaff edit the data in CyPluginDB
$mode = 'new'; // by default it is 'new'

// If versionid is provided through URL, it is in edit mode
$themeID = NULL; // used for edit mode only
include 'clean.inc';
if (isset ($_GET['versionid'])) {
	$themeID = cleanInt($_GET['versionid']);
}

if ($themeID != NULL) {
	$mode = 'edit';
}

// Set the page title based on the mode
if ($mode == 'new') {
	$pageTitle = 'Submit Theme to Cytoscape';
} else
	if ($mode == 'edit') {
		$pageTitle = 'Edit Theme in CyPluginDB';
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
$fileUpload = NULL;

// Case for 'edit', pull data out of DB for the given themeID
if (($tried == NULL) && ($mode == 'edit')) {
	exit("Edit page is not implemented yet. <br>If you want to make the change to the existing theme, you need to delete it and upload it again!");
}

// Remember the form data. If form validation failed, these data will
// be used to fill the refreshed form. If pass, they will be saved into
// database
// ????


//////////////////////// Form validation ////////////////////////
$validated = true;

if ($tried != NULL && $tried == 'yes') {
	if (($mode == 'new') && empty ($_FILES['themefile']['name'])) {
		$validated = false;
?>
		<strong>Error: A theme file is required.</strong>
		<?php
	}
}
//////// End of form validation ////////////////////


// if the mode is 'new' (i.e. submit from user), check if the theme already existed
if ($tried != NULL && $tried == 'yes' && $validated && $mode == 'new') {

	//if (isThemeVersionExists($connection, $pluginProps)) {
	//	$validated = false;
	//}
	
	//$themeData = getThemeDataFromFile($_FILES['themefile']['tmp_name']);
	//printThemeData($themeData);
	
	//$themeID = getThemeVersionID($connection, $themeData);
	
	
}
/////////////////////////////////  Form definition //////////////////////////

if (!($tried && $validated)) {
?>

<blockquote>
  <p><SPAN id="_ctl3_LabelRequired">	Fields denoted   by an (<span class="style4">*</span>) are required.</SPAN></p>
</blockquote>

<form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="submittheme" id="submittheme">

  <table width="753" border="0">
    <tr>
      <th width="130" scope="col"><span class="style2 style4">*</span>Theme File </th>
      <th width="389" scope="col"><div align="left">
        <input name="themefile" type="file" id="themefile" size="50">
      </div></th>
      <th width="220" scope="col">&nbsp;</th>
    </tr>
    
    <?php
    /*
    <tr>
      <th scope="row">&nbsp;</th>
      <td><div align="center">Name</div></td>
      <td><span class="style2 style4">*</span>E-mail</td>
    </tr>
    <tr>
      <th scope="row">Contact</th>
      <td><input name="tfContact" type="text" id="tfContact" size="62" /></td>
      <td><input name="tfEmail" type="text" id="tfEmail" size="35" /></td>
    </tr>
    */
    ?>
    <tr>
      <th height="80" scope="row">&nbsp;</th>
      <td><input name="Submit" type="submit" id="Submit" value="Submit" /></td>
      <td>&nbsp;</td>
    </tr>
      <tr>
    <td>&nbsp;</td>
    <td><input name="tried" type="hidden" id="tried" value="yes">
      <input name="themeID" type="hidden" id="themeID" value="<?php echo $themeID; ?>"></td>
    </tr>
  </table>
</form>

<?php
} // end of Form definition
else
	////////////////////////// form processing /////////////////////////
{

	$themeData = getThemeDataFromFile($_FILES['themefile']['tmp_name']);
	if (validateThemeData($connection, $themeData)) {
		saveThemeToDB($connection, $themeData);
		
		echo "Theme data is uploaded successfully!<br>";
	}
	else {
		echo "<br>Please correct the errors, and try again!<br><br>";
	}
	
}
// end of form processing

?>

<?php include "../footer.php"; ?>
<br>
</body>
</html>

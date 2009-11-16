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
	<div class="title">Edit plugin info</div>
</div>
<div id="container">
<?php include "../nav.php"; ?>
  
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

$Cy2p0_checked = NULL;
$Cy2p1_checked = NULL;
$Cy2p2_checked = NULL;
$Cy2p3_checked = NULL;
$Cy2p4_checked = NULL;
$Cy2p5_checked = NULL;
$Cy2p6_checked = NULL;
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

	$theVersions = preg_split("{,}", $db_cyVersion); // Split into an array
	foreach ($theVersions as $theVersion) {
		if ($theVersion == '2.0') {
			$Cy2p0_checked = "checked";
		}
		if ($theVersion == '2.1') {
			$Cy2p1_checked = "checked";
		}
		if ($theVersion == '2.2') {
			$Cy2p2_checked = "checked";
		}
		if ($theVersion == '2.3') {
			$Cy2p3_checked = "checked";
		}
		if ($theVersion == '2.4') {
			$Cy2p4_checked = "checked";
		}
		if ($theVersion == '2.5') {
			$Cy2p5_checked = "checked";
		}
		if ($theVersion == '2.6') {
			$Cy2p6_checked = "checked";
		}		
	}
	
}

// Remember the form data. If form validation failed, these data will
// be used to fill the refreshed form. If pass, they will be saved into
// database
include "formPluginInfo_remember.inc";

/*
if (isset ($_POST['tfName'])) {
	$name = $_POST['tfName'];
}

if (isset ($_POST['tfVersion'])) {
	$version = $_POST['tfVersion'];
}

if (isset ($_POST['taDescription'])) {
	//Enclding the string for SQL
	$description = addslashes($_POST['taDescription']);	
}

if (isset ($_POST['tfProjectURL'])) {
	$projectURL = $_POST['tfProjectURL'];
}

if (isset ($_POST['optCategory'])) {
	$category = $_POST['optCategory'];
}

if (isset ($_POST['tfMonth'])) {
	$month = $_POST['tfMonth'];
}

if (isset ($_POST['tfDay'])) {
	$day = $_POST['tfDay'];
}

if (isset ($_POST['tfYear'])) {
	$year = $_POST['tfYear'];
	$releaseDate = $year . '-' . $month . '-' . $day;
}


if (isset ($_POST['chk2p0'])) {
	$Cy2p0_checked = "checked";
	$cyVersion = '2.0';
}
if (isset ($_POST['chk2p1'])) {
	$Cy2p1_checked = "checked";
	if ($cyVersion == NULL) {
		$cyVersion = '2.1';
	} else {
		$cyVersion .= ',2.1';
	}
}
if (isset ($_POST['chk2p2'])) {
	$Cy2p2_checked = "checked";
	if ($cyVersion == NULL) {
		$cyVersion = '2.2';
	} else {
		$cyVersion .= ',2.2';
	}
}
if (isset ($_POST['chk2p3'])) {
	$Cy2p3_checked = "checked";
	if ($cyVersion == NULL) {
		$cyVersion = '2.3';
	} else {
		$cyVersion .= ',2.3';
	}
}
if (isset ($_POST['chk2p4'])) {
	$Cy2p4_checked = "checked";
	if ($cyVersion == NULL) {
		$cyVersion = '2.4';
	} else {
		$cyVersion .= ',2.4';
	}
}
if (isset ($_POST['chk2p5'])) {
	$Cy2p5_checked = "checked";
	if ($cyVersion == NULL) {
		$cyVersion = '2.5';
	} else {
		$cyVersion .= ',2.5';
	}
}

//Authors
if (isset ($_POST['tfNames0'])) {
	$names[0] = addslashes($_POST['tfNames0']);
}
if (isset ($_POST['tfEmail0'])) {
	$emails[0] = $_POST['tfEmail0'];
}
if (isset ($_POST['tfAffiliation0'])) {
	$affiliations[0] = addslashes($_POST['tfAffiliation0']);
}
if (isset ($_POST['tfAffiliationURL0'])) {
	$affiliationURLs[0] = $_POST['tfAffiliationURL0'];
}

if (isset ($_POST['tfNames1'])) {
	$names[1] = addslashes($_POST['tfNames1']);
}
if (isset ($_POST['tfEmail1'])) {
	$emails[1] = $_POST['tfEmail1'];
}
if (isset ($_POST['tfAffiliation1'])) {
	$affiliations[1] = addslashes($_POST['tfAffiliation1']);
}
if (isset ($_POST['tfAffiliationURL1'])) {
	$affiliationURLs[1] = $_POST['tfAffiliationURL1'];
}
*/

//////////////////////// Form validation ////////////////////////
$validated = true;



if ($tried != NULL && $tried == 'yes') {
	include "formPluginInfo_validation.inc";
	
/*
	if (empty ($_POST['tfName'])) {
		$validated = false;
?>
		Error: Plugin_name is a required field.<br>
		<?php


	}
	if (empty ($_POST['tfVersion'])) {
		$validated = false;
?>
		Error: Version is a required field.<br>
		<?php


	}
	if (empty ($_POST['taDescription'])) {
		$validated = false;
?>
		Error: Description is a required field.<br>
		<?php


	}
	if ($category == "Please choose one") {
		$validated = false;
?>
		Error: Category is a required field.<br>
		<?php


	}

	// validate the release date
	if (!(empty ($month) && empty ($day) && empty ($year))) {
		if (!((strspn($month, "0123456789") == strlen($month)) && (strlen($month) > 0) && (strlen($month) < 3))) {
			$validated = false;
?>
			Invalid release month <br>
			<?php


		}
		if (!((strspn($day, "0123456789") == strlen($day)) && (strlen($day) > 0) && (strlen($day) < 3))) {
			$validated = false;
?>
			Invalid release day <br>
			<?php


		}
		if (!((strspn($year, "0123456789") == strlen($year)) && (strlen($year) > 0) && (strlen($year) == 4))) {
			$validated = false;
?>
			Invalid release year <br>
			<?php


		}
	}
*/
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
/* 
 <table width="878" border="0">
  <tr>
    <td width="208"><div align="right" ><span class="style3">*</span>Plugin name</div></td>
    <td width="660"><input name="tfName" type="text" value ="<?php echo $name ?>" size="40" /></td>
  </tr>
  <tr>
    <td><div align="right"><span class="style4">*</span>version</div>      </td>
    <td><input name="tfVersion" type="text" id="tfVersion" value ="<?php echo $version ?>" size="20" /></td>
  </tr>
  <tr>
    <td height="75"><div align="right"><span class="style4">*</span>Description</div></td>
    <td><textarea name="taDescription" cols="80" rows="5" id="taDescription"><?php echo $description ?></textarea></td>
  </tr>
    <tr>
    <td><div align="right"><span class="style4">*</span>Category</div></td>
    <td><label>
      <select name="optCategory" id="optCategory" >

        <option <?php if ($category && $category == 'Please choose one') echo 'selected' ?>>Please choose one</option>	
        <?php  echo '<option>Core</option>';?>
        <option <?php if ($category && $category == 'Analysis') echo 'selected' ?>>Analysis</option>
        <option <?php if ($category && $category == 'Network and Attribute I/O') echo 'selected' ?>>Network and Attribute I/O</option>
        <option <?php if ($category && $category == 'Network Inference') echo 'selected' ?>>Network Inference</option>
        <option <?php if ($category && $category == 'Functional Enrichment') echo 'selected' ?>>Functional Enrichment</option>
        <option <?php if ($category && $category == 'Communication/Scripting') echo 'selected' ?>>Communication/Scripting</option>
		<option <?php if ($category && $category == 'Other') echo 'selected' ?>>Other</option>
      </select>
    </label></td>
  </tr>
  <tr>
    <td><div align="right"><span class="style4">*</span>Release Date </div></td>
    <td><table width="118" border="0">
        <tr>
          <td width="32" scope="col"><div align="center">mm</div></td>
          <td width="29" scope="col"><div align="center">dd</div></td>
          <td width="57" scope="col"><div align="center">yyyy</div></td>
        </tr>
        <tr>
          <td><input name="tfMonth" type="text" id="tfMonth" value ="<?php echo $month ?>" size="2" /></td>
          <td><input name="tfDay" type="text" id="tfDay" value ="<?php echo $day ?>" size="2" /></td>
          <td><input name="tfYear" type="text" id="tfYear" value ="<?php echo $year ?>" size="4" /></td>
        </tr>
      </table>      </td>
  </tr>
  <tr>
    <td><div align="right"></div></td>
    <td><label></label></td>
  </tr>
  <tr>
    <td><div align="right"><span class="style4">*</span>Cytoscape versions </div></td>
    <td><table width="404" border="0">
      <tr>
        <td width="72"><label>2.0
            <input name="chk2p0" type="checkbox" id="chk2p0" value="Cy2p0" <?php echo $Cy2p0_checked ?> />
        </label></td>
        <td width="73"><label>2.1
            <input name="chk2p1" type="checkbox" id="chk2p1" value="Cy2p1" <?php echo $Cy2p1_checked ?> />
        </label></td>
        <td width="72"><label>2.2
            <input name="chk2p2" type="checkbox" id="chk2p2" value="Cy2p2" <?php echo $Cy2p2_checked ?> />
        </label></td>
        <td width="72"><label>2.3
            <input name="chk2p3" type="checkbox" id="chk2p3" value="Cy2p3" <?php echo $Cy2p3_checked ?> />
        </label></td>
        <td width="72"><label>
          2.4
          <input name="chk2p4" type="checkbox" id="chk2p4" value="Cy2p4" <?php echo $Cy2p4_checked ?> />
        </label></td>
        <td width="135"><label>
          2.5
          <input name="chk2p5" type="checkbox" id="chk2p5" value="Cy2p5" <?php echo $Cy2p5_checked ?> />
        </label></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td><div align="right"></div></td>
    <td><label></label></td>
  </tr>
  <tr>
    <td><div align="right">Author(s)</div></td>
    <td><table width="660" border="0">
      <tr>
        <td width="444"><div align="center"> Name(s)</div></td>
        <td width="206"><div align="center">contact e-mail (not made public) </div></td>
      </tr>
      <tr>
        <td><label>
          <input name="tfNames0" type="text" id="tfNames0" size="70" value ="<?php echo htmlentities(stripslashes($names[0])) ?>" />
        </label></td>
        <td><input name="tfEmail0" type="text" id="tfEmail0" size="30" value ="<?php echo $emails[0] ?>" /></td>
        </tr>
      <tr>
        <td><div align="center">Affiliation</div></td>
        <td><div align="center">Affiliation URL</div></td>
        </tr>
      <tr>
        <td><input name="tfAffiliation0" type="text" id="tfAffiliation0" size="70" value ="<?php echo htmlentities(stripslashes($affiliations[0])) ?>" /></td>
        <td><input name="tfAffiliationURL0" type="text" id="tfAffiliationURL0" size="30" value ="<?php echo $affiliationURLs[0] ?>" /></td>
      </tr>
    </table></td>
  </tr>

  <tr>
    <td>&nbsp;</td>
    <td><table width="660" border="0">
      <tr>
        <td width="444"><div align="center"> Name(s)</div></td>
        <td width="206"><div align="left">contact e-mail </div></td>
      </tr>
      <tr>
        <td><label>
        <input name="tfNames1" type="text" id="tfNames1" size="70" value ="<?php if (isset($names[1])){ echo htmlentities(stripslashes($names[1]));} ?>" />
        </label></td>
        <td><input name="tfEmail1" type="text" id="tfEmail02" size="30" value ="<?php if (isset($emails[1])){ echo $emails[1];} ?>" /></td>
      </tr>
      <tr>
        <td><div align="center">Affiliation</div></td>
        <td><div align="center">Affiliation URL</div></td>
      </tr>
      <tr>
        <td><input name="tfAffiliation1" type="text" id="tfAffiliation1" size="70" value ="<?php if (isset($affiliations[1])){echo htmlentities(stripslashes($affiliations[1]));} ?>" /></td>
        <td><input name="tfAffiliationURL1" type="text" id="tfAffiliationURL1" size="30" value ="<?php if (isset($affiliationURLs[1])){ echo stripslashes($affiliationURLs[1]);} ?>" /></td>
      </tr>
    </table></td>
  </tr>
  
  <tr>  
<td>&nbsp;</td>
<td>&nbsp;</td>
  </tr>

  
  <tr>
    <td><div align="right">Project URL</div></td>
    <td><input name="tfProjectURL" type="text" id="tfProjectURL" value ="<?php echo $projectURL ?>" size="80" /></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><input name="tried" type="hidden" id="tried" value="yes">
      <input name="versionID" type="hidden" id="versionID" value="<?php echo $versionID; ?>"></td>
  </tr>
</table>
*/
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
		$query = 'SELECT category_id FROM categories WHERE name = "' . $category . '"';
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
		$query1_suffix = ' where plugin_auto_id = '.$db_plugin_id;

		$query1 = $query1_prefix;
		if ($name != $db_name) { // plugin name
			$query1 .='name ="'.$name.'",';
		} 
		if ($description != $db_description) {
			$query1 .='description ="'.$description.'",';
		}
		if ($projectURL != $db_projectURL) {
			$query1 .='project_url ="'.$projectURL.'",';
		}
		if ($category_id != $db_categoryID) {
			$query1 .='category_id ='.$category_id.',';
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
		$query2_suffix = ' where version_auto_id = '.$versionID;

		$query2 = $query2_prefix;

		// Check plugin_file_id???
		if ($version != $db_version) { // plugin version
			$query2 .='version ="'.$version.'",';
		}
		if ($releaseDate != $db_releaseDate) {
			$query2 .='release_date ="'.$releaseDate.'",';
		}
		if ($cyVersion != $db_cyVersion) {
			$query2 .='cy_version ="'.$cyVersion.'",';
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
			$query = 'delete from plugin_author where author_id ='.$db_author_ids[$i];
			// Run the query
			if (!(@ mysql_query($query, $connection)))
				showerror();
			$query = 'delete from authors where author_auto_id ='.$db_author_ids[$i];
			// Run the query
			if (!(@ mysql_query($query, $connection)))
				showerror();
		}
		
		// Add new authors into tables
		for ($i = 0; $i < count($names); $i++) {
			if (!(empty($names[$i]) && empty($emails[$i]) && empty($affiliations[$i]) && empty($affiliationURLs[$i]))) {
				$query = 'INSERT INTO authors VALUES (0, "' . $names[$i] . '", "' . $emails[$i] . '","' . $affiliations[$i] . '","' . $affiliationURLs[$i] . '")';

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
		}
?>

	Database is updated successfully!
	<p>Go back to <a href="pluginadmin.php">Plugin adminstration page</a></p>
	<?php
	}
	?>
<?php include "../footer.php"; ?>
<br>
</body>
</html>

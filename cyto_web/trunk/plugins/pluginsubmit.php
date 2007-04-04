<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Submit Plugin to Cytoscape</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/css/cytoscape.css">
	<link rel="shortcut icon" href="images/cyto.ico">
	<style type="text/css">
<!--
.style3 {color: #FF0066}
.style4 {color: #FF0000}
-->
    </style>
</head>
<body bgcolor="#ffffff">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tbody>
		<tr>
			<td width="10">&nbsp;
			</td>
			<td valign="bottom">
				<h1>Submit Plugin to Cytoscape</h1>
			</td>
		</tr>
	</tbody>
</table>

<?php include "../nav.php"; ?>
  
<?php


// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
	showerror();
// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();

/** 
* function bigboxotext 
* 
* bigboxotext is a conversion script that allows you to retain your formatting and
* text characters without being penalised by standards on output (xhtml 1.1 validation) and
* allows you to store your textareas content in a mysql database without the fear of loosing formatting.
* 
* <code>
* <?php  // using type 1
*    $textarea = $_POST['textareasfieldname'];
* 	$textarea = $bigboxotext($output;
* ?> 
* </code> 
* 
* @author Chris McKee <pcdevils@gmail.com>  
* 
* @param  string $output - Text Area to be formatted
*/
function bigboxotext($output) {
	$output = str_replace(chr(10), "<br />", $output);
	$output = str_replace(chr(146), "&#8217;", $output);
	$output = str_replace(chr(130), "&#8218;", $output);
	$output = str_replace(chr(133), "&#8230;", $output);
	$output = str_replace(chr(150), "&ndash;", $output);
	$output = str_replace(chr(151), "&ndash;", $output);
	$output = str_replace(chr(152), "&ndash;", $output);

	$output = str_replace(chr(146), "&#39;", $output); // error 146
	$output = str_replace("'", "&#39;", $output); // error 146
	$output = str_replace(chr(145), "&#39;;", $output); // error 145 
	$output = str_replace(chr(147), '"', $output);
	$output = str_replace(chr(148), '"', $output);
	$output = str_replace(chr(151), "&#8212", $output);

	return $output;
}

$name = NULL;
if (isset ($_POST['tfName'])) {
	$name = $_POST['tfName'];
}
$version = NULL;
if (isset ($_POST['tfVersion'])) {
	$version = $_POST['tfVersion'];
}
$description = NULL;
if (isset ($_POST['taDescription'])) {
	$description = $_POST['taDescription'];
	//$description = bigboxotext($description); //preserving the format in a TextArea 
}
$projectURL = NULL;
if (isset ($_POST['tfProjectURL'])) {
	$projectURL = $_POST['tfProjectURL'];
}
$category = NULL;
if (isset ($_POST['optCategory'])) {
	$category = $_POST['optCategory'];
}
$releaseDate = NULL;
$month = NULL;
if (isset ($_POST['tfMonth'])) {
	$month = $_POST['tfMonth'];
}
$day = NULL;
if (isset ($_POST['tfDay'])) {
	$day = $_POST['tfDay'];
}
$year = NULL;
if (isset ($_POST['tfYear'])) {
	$year = $_POST['tfYear'];
	$releaseDate = $year . '-' . $month . '-' . $day;
}

$releaseNote = NULL;
if (isset ($_POST['taReleaseNote'])) {
	$releaseNote = $_POST['taReleaseNote'];
}
$releaseNoteURL = NULL;
if (isset ($_POST['tfReleaseNoteURL'])) {
	$releaseNoteURL = $_POST['tfReleaseNoteURL'];
}
$fileUpload = NULL;
if (isset ($_FILES['filePlugin'])) {
	$fileUpload = $_FILES['filePlugin'];
}
$jarURL = NULL;
if (isset ($_POST['tfJarURL'])) {
	$jarURL = $_POST['tfJarURL'];
}
$sourceURL = NULL;
if (isset ($_POST['tfSourceURL'])) {
	$sourceURL = $_POST['tfSourceURL'];
}
$Cy2p0_checked = NULL;
$Cy2p1_checked = NULL;
$Cy2p2_checked = NULL;
$Cy2p3_checked = NULL;
$Cy2p4_checked = NULL;
$Cy2p5_checked = NULL;
$cyVersion = NULL;
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
if (isset ($_POST['taReference'])) {
	$reference = $_POST['taReference'];
}
$comment = NULL;
if (isset ($_POST['taComment'])) {
	$comment = $_POST['taComment'];
}

//Authors
$names = NULL;
if (isset ($_POST['tfNames0'])) {
	$names[0] = $_POST['tfNames0'];
}
$emails = NULL;
if (isset ($_POST['tfEmail0'])) {
	$emails[0] = $_POST['tfEmail0'];
}
$affiliations = NULL;
if (isset ($_POST['tfAffiliation0'])) {
	$affiliations[0] = $_POST['tfAffiliation0'];
}
$affiliationURLs = NULL;
if (isset ($_POST['tfAffiliationURL0'])) {
	$affiliationURLs[0] = $_POST['tfAffiliationURL0'];
}

if (isset ($_POST['tfNames1'])) {
	$names[1] = $_POST['tfNames1'];
}
if (isset ($_POST['tfEmail1'])) {
	$emails[1] = $_POST['tfEmail1'];
}
if (isset ($_POST['tfAffiliation1'])) {
	$affiliations[1] = $_POST['tfAffiliation1'];
}
if (isset ($_POST['tfAffiliationURL1'])) {
	$affiliationURLs[1] = $_POST['tfAffiliationURL1'];
}



$tried = NULL;
if (isset ($_POST['tried'])) {
	$tried = 'yes';
}

//////////////////////// Form validation ////////////////////////
$validated = true;

if ($tried != NULL && $tried == 'yes') {

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

	//Either a jarURL or a jar file should supplied
	if (empty ($_POST['tfJarURL']) && empty ($_FILES['filePlugin']['name'])) {
		$validated = false;
?>
		Error: Either a jarURL or a jar file should be supplied.<br>
		<?php
	}

} // End of form validation

// Check if the plugin already existed
if ($tried != NULL && $tried == 'yes' && $validated) {
	$query = 'SELECT version_auto_id FROM categories, plugin_list, plugin_version' .
	' WHERE categories.category_id = plugin_list.category_id ' .
	'       and plugin_list.plugin_auto_id = plugin_version.plugin_id ' .
	'       and categories.name ="' . $category . "\" " .
	'		and plugin_list.name = "' . $name . "\" " .
	'		and plugin_version.version = "' . $version . "\"";

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

//echo "tried = ", $tried, "  validated = ",$validated,"<br>"; 

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
        <option "<?php if ($category && $category == 'Please choose one') echo 'selected' ?>">Please choose one</option>
        <option "<?php if ($category && $category == 'Analysis Plugins') echo 'selected' ?>">Analysis Plugins</option>
        <option "<?php if ($category && $category == 'Network and Attribute I/O Plugins') echo 'selected' ?>">Network and Attribute I/O Plugins</option>
        <option "<?php if ($category && $category == 'Network Inference Plugins') echo 'selected' ?>">Network Inference Plugins</option>
        <option "<?php if ($category && $category == 'Functional Enrichment Plugins') echo 'selected' ?>">Functional Enrichment Plugins</option>
        <option "<?php if ($category && $category == 'Communication/Scripting Plugins') echo 'selected' ?>">Communication/Scripting Plugins</option>
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
    <td><div align="right">Jar File </div></td>
    <td><input name="filePlugin" type="file" id="filePlugin" size="80" /></td>
  </tr>
  <tr>
    <td><div align="right"><span class="style4">*</span>or</div></td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td><div align="right">Jar URL </div></td>
    <td><input name="tfJarURL" type="text" id="jarURL" value ="<?php echo $jarURL ?>" size="80" /></td>
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
          <input name="tfNames0" type="text" id="tfNames0" size="70" value ="<?php echo $names[0] ?>" />
        </label></td>
        <td><input name="tfEmail0" type="text" id="tfEmail0" size="30" value ="<?php echo $emails[0] ?>" /></td>
        </tr>
      <tr>
        <td><div align="center">Affiliation</div></td>
        <td><div align="center">Affiliation URL</div></td>
        </tr>
      <tr>
        <td><input name="tfAffiliation0" type="text" id="tfAffiliation0" size="70" value ="<?php echo $affiliations[0] ?>" /></td>
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
        <input name="tfNames1" type="text" id="tfNames1" size="70" value ="<?php echo $names[1] ?>" />
        </label></td>
        <td><input name="tfEmail1" type="text" id="tfEmail02" size="30" value ="<?php echo $emails[1] ?>" /></td>
      </tr>
      <tr>
        <td><div align="center">Affiliation</div></td>
        <td><div align="center">Affiliation URL</div></td>
      </tr>
      <tr>
        <td><input name="tfAffiliation1" type="text" id="tfAffiliation1" size="70" value ="<?php echo $affiliations[1] ?>" /></td>
        <td><input name="tfAffiliationURL1" type="text" id="tfAffiliationURL1" size="30" value ="<?php echo $affiliationURLs[1] ?>" /></td>
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
    <td><div align="right">Release note</div></td>
    <td><label>
      <textarea name="taReleaseNote" cols="80" rows="3" id="taReleaseNote"></textarea>
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
    <td><textarea name="taReference" cols="80" rows="3" id="taReference"></textarea></td>
  </tr>
  <tr>
    <td><div align="right">License (brief) </div></td>
    <td><label>
    <input name="tfLicenseBrief" type="text" id="tfLicenseBrief" size="80">
    </label></td>
  </tr>
  <tr>
    <td><div align="right">License (detail)</div></td>
    <td><label>
      <textarea name="taLicenseDetail" cols="80" rows="3" id="taLicenseDetail"></textarea>
    </label></td>
  </tr>
  <tr>
    <td><div align="right">Comment</div></td>
    <td><label>
      <textarea name="taComment" cols="80" rows="2" id="taComment"></textarea>
    </label></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td><input name="tried" type="hidden" id="tried" value="yes"></td>
  </tr>
</table>


<p align="center">
  <input name="btnSubmit" type="submit" id="btnSubmit" value="Submit" />
</p>
</form>
<p align="center">&nbsp;</p>
<?php


} else
	////////////////////////// form processing /////////////////////////
	// Takes the details of the plugin from user and 
	// adds them to the tables of our CyPluginDB_RAW.
	{
	//echo '<p>process the data and Save the data into DB.</p>';

	/*
		echo "name = ", $name, "<br>";
		echo "version = ", $version, "<br>";
		echo "description =", $description, "<br>";
		echo "projectURL = ", $projectURL, "<br>";
		echo "category = ", $category, "<br>";
		echo "month = ", $month, "<br>";
		echo "day = ", $day, "<br>";
		echo "year = ", $year, "<br>";
		echo "releaseNote = ", $releaseNote, "<br>";
		echo "releaseNoteURL = ", $releaseNoteURL, "<br>";
		echo "filePlugin.name=", $fileUpload['name'], "<br>";
		echo "filePlugin.type=", $fileUpload['type'], "<br>";
		echo "filePlugin.size=", $fileUpload['size'], "<br>";
		echo "filePlugin.tmp_name=", $fileUpload['tmp_name'], "<br>";
		echo "jarURL = ", $jarURL, "<br>";
		echo "sourceURL = ", $sourceURL, "<br>";
		//echo "Cy2p0_checked = ", $Cy2p0_checked, "<br>";
		//echo "Cy2p1_checked = ", $Cy2p1_checked, "<br>";
		//echo "Cy2p2_checked = ", $Cy2p2_checked, "<br>";
		//echo "Cy2p3_checked = ", $Cy2p3_checked, "<br>";
		//echo "Cy2p4_checked = ", $Cy2p4_checked, "<br>";
		//echo "Cy2p5_checked = ", $Cy2p5_checked, "<br>";
		echo "cyVersion = ", $cyVersion, "<br>";
		echo "<br>reference = ",$reference, "<br>";
		echo "comment = ",$comment, "<br>";
		echo 'authorCount = ', $authorCount, '<br>';
		
		echo 'names0 = ', $names[0], '<br>';
		echo 'emails0 = ', $emails[0], '<br>';
		echo 'affilications0 = ', $affiliations[0], '<br>';
		echo 'affiliationURLs0 = ', $affiliationURLs[0], '<br>';
					
		echo 'names1 = ', $names[1], '<br>';
		echo 'emails1 = ', $emails[1], '<br>';	
		echo 'affilications1 = ', $affiliations[1], '<br>';
		echo 'affiliationURLs1 = ', $affiliationURLs[1], '<br>';	
*/


	//Load the Jar file to DB if any
	$plugin_file_auto_id = NULL;

	if ($fileUpload['name'] != NULL) {
		//echo "A file is selected";
		$fileUpload_type = $fileUpload['type'];
		$fileUpload_name = $fileUpload['name'];

		$fileHandle = fopen($fileUpload['tmp_name'], "r");
		$fileContent = fread($fileHandle, $fileUpload['size']);
		$fileContent = addslashes($fileContent);

		$dbQuery = "INSERT INTO plugin_files VALUES ";
		$dbQuery .= "(0, '$fileContent', '$fileUpload_type', '$fileUpload_name')";
		//echo "<br>dbQuery = " . $dbQuery . "<br>";
		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();

		echo "<br><b>File uploaded successfully</b><br>";
		$plugin_file_auto_id = mysql_insert_id($connection);
	}
	
	// Get the category_id
	$dbQuery = 'SELECT category_id FROM categories WHERE name = "' . $category . '"';
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	$the_row = @ mysql_fetch_array($result);
	$category_id = $the_row['category_id'];

	$plugin_auto_id = NULL;
	//Check if there is an old version of this plugin in DB
	$dbQuery = 'SELECT plugin_auto_id FROM plugin_list ' .
	'         WHERE plugin_list.name = "' . $name . '" and category_id =' . $category_id;

	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) != 0) {
		//There is an old version in the DB, update the row in the table plugin_list
		$the_row = @ mysql_fetch_array($result);
		$plugin_auto_id = $the_row['plugin_auto_id'];
		echo "There is an old version of this plugin in the DB, plugin_auto_id =" . $plugin_auto_id . "<br>";
	} else {
		//This is a new plugin, add a row in the table plugin_list
		//echo "This is a new plugin<br>";

		$dbQuery = 'INSERT INTO plugin_list VALUES ' .
		'(0, "' . $name . '", "' . $description . '",NULL,NULL,"' . $projectURL . '",' .
		$category_id . ',now())';
		//echo "<br>dbQuery = " . $dbQuery . "<br>";
		// Run the query
		if (!($result = @ mysql_query($dbQuery, $connection)))
			showerror();

		$plugin_auto_id = mysql_insert_id($connection);
		//echo "new plugin_auto_id = " . $plugin_auto_id . "<br>";
	}

	// Insert a row into table plugin_version
	$status = 'new';
	$dbQuery = 'INSERT INTO plugin_version VALUES (0, ' . $plugin_auto_id . ', ';
	if ($plugin_file_auto_id == NULL) {
		$dbQuery .= 'NULL';
	} else {
		$dbQuery .= $plugin_file_auto_id;
	}
	$dbQuery .= ',"' . $version . '",\'' .
	$releaseDate . '\',"' . $releaseNote . '","' . $releaseNoteURL . '","' . $comment . '","' . $jarURL . '","' .
	$sourceURL . '","' . $cyVersion . '","'.$status.'","' . $reference . '", now())';

	//echo "<br>dbQuery = " . $dbQuery . "<br>";

	// Run the query
	if (!(@ mysql_query($dbQuery, $connection)))
		showerror();

	$version_auto_id = mysql_insert_id($connection);
	//echo "new version_auto_id = " . $version_auto_id . "<br>";

	// insert rows into author tables (authors and plugin_author)

	$authorCount = count($names);

	for ($i = 0; $i < $authorCount; $i++) {
		$dbQuery = 'INSERT INTO authors VALUES (0, "' . $names[$i] . '", "' . $emails[$i] . '","' . $affiliations[$i] . '","' . $affiliationURLs[$i] . '")';

		//echo "<br>dbQuery = " . $dbQuery . "<br>";

		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();

		$author_auto_id = mysql_insert_id($connection);
		//echo "new author_auto_id = " . $author_auto_id . "<br>";

		$authorship_seq = $i;
		$dbQuery = 'INSERT INTO plugin_author VALUES (' . $version_auto_id . ', ' . $author_auto_id . ',' . $authorship_seq . ')';

		//echo "<br>dbQuery = " . $dbQuery . "<br>";

		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();
	}
	
	?>
	Thank you for submitting your plugin to Cytoscape.Cytoscape staff will review the data  and publish it on the cytoscape website. If there are any questions, you will be contacted via e-mail.
	<?php
	

} // end of form processing
?>

<?php include "../footer.php"; ?>
<br>
</body>
</html>

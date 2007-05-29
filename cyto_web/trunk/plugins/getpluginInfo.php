<?php
function getAuthorInfo($connection, $plugin_version_id) {

	// A plugin may be released by more than one institution, more than one author at
	// each institution

	$query = 'SELECT * FROM authors, plugin_author WHERE plugin_author.plugin_version_id =' . $plugin_version_id .
	' and plugin_author.author_id = authors.author_auto_id ORDER BY authorship_seq';

	// Run the query
	if (!($authorsInfo = @ mysql_query($query, $connection)))
		showerror();

	//echo "authorRowsCount =", mysql_num_rows($authorsInfo);
	$authorInfoPage = "";

	while ($author_row = @ mysql_fetch_array($authorsInfo)) {

		if ($author_row["names"] != NULL) {
			$authorInfoPage .= $author_row["names"].' ';
		}

		if ($author_row["affiliation"] != NULL) {
			if (!empty ($author_row["affiliationURL"])) {
				$authorInfoPage .= ' <a href="' . $author_row["affiliationURL"] . '">' . $author_row["affiliation"] . '<a> ';
			} else {
				$authorInfoPage .= $author_row["affiliation"];
			}
		}
		$authorInfoPage .= '<br>';
	}

	if (empty ($authorInfoPage)) {
		$authorInfoPage = "<br>";
	}

	//echo $authorInfoPage;
	return $authorInfoPage;
} // End of function getAuthorInfo()

// Construct the pluginInfo page     
function getPluginInfoPage($connection, $pluginList_row) {

	$pluginInfoPage = "<br>" .
	"<b>Description:</b> " . $pluginList_row["description"];
	$projectURL = $pluginList_row["project_url"];
	if ($projectURL != null) {
		$pluginInfoPage .= "\n<br><b>Project website:</b> <a href=\"$projectURL\">" . $projectURL . "</a>";
	}
	$license = $pluginList_row["license"];
	if ($license != null) {
		$data = array($pluginList_row["name"],$license);				
		$data = base64_encode(serialize($data));
		$pluginInfoPage .= "\n<br><b>License:</b>" .
				" click <a href=\"displaylicense.php?data=$data\">here</a><br>";		
	}

	//Get info for all versions of the given plugin (one plugin may have more than one version)
	$query = 'SELECT * FROM plugin_version' .
	' WHERE  plugin_id = ' . $pluginList_row['plugin_auto_id'].' and status = "published"';

	// Run the query
	if (!($allVersionInfo = @ mysql_query($query, $connection)))
		showerror();

	$versionCount = @ mysql_num_rows($allVersionInfo);

	if ($versionCount == 0)
		return "Pending to Cytoscape staff review";

	//List info for all available versions
	while ($versionCount > 0) {

		// Add a blank line between different version
		if ($versionCount > 1) { // case for multiple version
			$pluginInfoPage .= "\n<br>--<br>";
		}

		$versionSpecific_row = @ mysql_fetch_array($allVersionInfo);

		if ($versionSpecific_row["reference"] != null) {
			$pluginInfoPage .= "\n<br><b>Reference:</b> " . $versionSpecific_row["reference"];
		}

		if ($versionSpecific_row["version"] != null) {
			$pluginInfoPage .= "\n<br><b>Version:</b> " . $versionSpecific_row["version"] . "<br>";
		} else {
			$pluginInfoPage .= "\n<br><b>Version:</b>Unknown<br>";
		}

		if ($versionSpecific_row["release_date"] != null) {
			$pluginInfoPage .= "\n<b>Release Date:</b> " . $versionSpecific_row["release_date"] . "<br>";
		} else {
			$pluginInfoPage .= "\n<b>Release Date:</b>Unknown<br>";
		}

		$pluginInfoPage .= "\n<b>Released by:</b> " .
		getAuthorInfo($connection, $versionSpecific_row['version_auto_id']);

		if ($versionSpecific_row["release_note_url"] != null) {
			$pluginInfoPage .= "\n<b>Release notes:</b>" .
			"  <a href=\"" . $versionSpecific_row["release_note_url"] . "\">" . $versionSpecific_row["release_note_url"] . "</a><br>";
		} else if (($versionSpecific_row["release_note"] != null)&&(strlen(trim($versionSpecific_row["release_note"]))!=0)) {
				$data = array($pluginList_row["name"],$versionSpecific_row["version"], $versionSpecific_row["release_note"]);				
				$data = base64_encode(serialize($data));
				$pluginInfoPage .= "\n<b>Release notes:</b>" .
				" click <a href=\"displayreleasenote.php?data=$data\">here</a><br>";
			}

		if ($versionSpecific_row["cy_version"]) {
			$pluginInfoPage .= "\n<b>Verified to work in:</b> " . $versionSpecific_row["cy_version"] . "<br>";
		}

		if ($versionSpecific_row["comment"] != null) {
			$pluginInfoPage .= "\n<b>Note:</b> " . $versionSpecific_row["comment"] . "<br>";
		}

		if ($versionSpecific_row["jar_url"] != null) {
			$pluginInfoPage .= "\n<b>Download Jar/zip:</b> <a href=\"" . $versionSpecific_row["jar_url"] . "\">" . $versionSpecific_row["jar_url"] . "</a><br>";
		} else {
			$pluginInfoPage .= "\n<b>Download Jar/zip:</b> click <a href=\"" . 'pluginjardownload.php?id=' . $versionSpecific_row["plugin_file_id"] . "\">here</a><br>";
		}

		if ($versionSpecific_row["source_url"] != null) {
			$pluginInfoPage .= "\n<b>Download source:</b> <a href=\"" . $versionSpecific_row["source_url"] . "\">" . $versionSpecific_row["source_url"] . "</a>";
		}

		$versionCount = $versionCount -1;
	} // end of while loop

	$pluginInfoPage .= "<br><br>";

	return $pluginInfoPage;
}
?>
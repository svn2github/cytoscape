<?php
	// Pull the data out of DB for this versionID
	$query = 'SELECT categories.name as catName,' .
	' categories.category_id as catID, ' .
	' plugin_list.plugin_auto_id as plugin_id, ' .
	' plugin_list.name as pluginName, ' .
	' plugin_list.description as pluginDescription, ' .
	' plugin_list.license as pluginLicense, ' .
	' plugin_list.license_required as licenseRequired, ' .
	' plugin_list.project_url as projectURL, ' .
	' plugin_version.plugin_file_id as pluginFileID, ' .
	' plugin_version.version as pluginVersion, ' .
	' plugin_version.release_date as releaseDate, ' .
	' plugin_version.release_note as releaseNote, ' .
	' plugin_version.release_note_url as releaseNoteURL, ' .
	' plugin_version.comment as comment, ' .
	' plugin_version.jar_url as jarURL, ' .
	' plugin_version.source_url as sourceURL, ' .
	' plugin_version.cy_version as cyVersion, ' .
	' plugin_version.status as status,' .	
	' plugin_version.reference as reference' .
	' FROM categories, plugin_list, plugin_version' .
	' WHERE  categories.category_id     = plugin_list.category_id and ' .
	'    plugin_list.plugin_auto_id = plugin_version.plugin_id and ' .
	'    plugin_version.version_auto_id = ' . $versionID;

	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();

	$first_row = @ mysql_fetch_array($result);
	$db_category = $first_row['catName'];
	$db_plugin_id = $first_row['plugin_id'];
	$db_categoryID = $first_row['catID'];
	$db_name = $first_row['pluginName'];
	$db_description = $first_row['pluginDescription'];
	$db_version = $first_row['pluginVersion'];
	$db_releaseDate = $first_row['releaseDate'];
	list ($db_year, $db_month, $db_day) = split('[-]', $db_releaseDate);
	$db_projectURL = $first_row['projectURL'];
	$db_releaseNote = $first_row['releaseNote'];
	$db_releaseNoteURL = $first_row['releaseNoteURL'];

	//fileUpload
	$db_pluginFileID = $first_row['pluginFileID'];

	$db_jarURL = $first_row['jarURL'];
	$db_sourceURL = $first_row['sourceURL'];
	$db_cyVersion = $first_row['cyVersion'];
	$db_reference = $first_row['reference'];

	$db_license = $first_row['pluginLicense'];
	$db_license_required = $first_row['licenseRequired'];
	$db_comment = $first_row['comment'];
	$db_status =  $first_row['status'];


	// get the author info for this versionID
	$query = 'select * from authors, plugin_author ' .
	' where authors.author_auto_id = plugin_author.author_id and plugin_version_id =' . $versionID .
	' order by plugin_author.authorship_seq';
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
	//$authorCount = mysql_num_rows($result);	
	$db_author_ids = NULL;
	$db_names = NULL;
	$db_emails = NULL;
	$db_affiliations = NULL;
	$db_affiliationURLs = NULL;

	while ($author_row = @ mysql_fetch_array($result)) {
		$db_author_ids[] = $author_row['author_id'];
		$db_names[] = $author_row['names'];
		$db_emails[] = $author_row['email'];
		$db_affiliations[] = $author_row['affiliation'];
		$db_affiliationURLs[] = $author_row['affiliationURL'];
	}
	
?>

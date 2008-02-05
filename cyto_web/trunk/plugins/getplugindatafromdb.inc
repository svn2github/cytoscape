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
	' plugin_version.reference as reference,' .
	' plugin_version.theme_only as theme_only' .
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
	$db_description = stripslashes($first_row['pluginDescription']);
	$db_version = $first_row['pluginVersion'];
	$db_releaseDate = $first_row['releaseDate'];
	list ($db_year, $db_month, $db_day) = split('[-]', $db_releaseDate);
	$db_projectURL = $first_row['projectURL'];
	$db_releaseNote = stripslashes($first_row['releaseNote']);
	$db_releaseNoteURL = $first_row['releaseNoteURL'];
	$db_themeOnly = $first_row['theme_only'];

	//fileUpload
	$db_pluginFileID = $first_row['pluginFileID'];
	$db_jarURL = $first_row['jarURL'];
	$db_sourceURL = $first_row['sourceURL'];
	$db_cyVersion = $first_row['cyVersion'];
	$db_reference = stripslashes($first_row['reference']);
	$db_license = stripslashes($first_row['pluginLicense']);
	$db_license_required = $first_row['licenseRequired'];
	$db_comment = stripslashes($first_row['comment']);
	$db_status =  $first_row['status'];

	//
	$db_pluginProps['pluginCategory'] = $first_row['catName'];
	$db_pluginProps['pluginID'] = $first_row['plugin_id'];
	$db_pluginProps['pluginCategoryID'] = $first_row['catID'];
	$db_pluginProps['pluginName'] = $first_row['pluginName'];
	$db_pluginProps['pluginDescription'] = stripslashes($first_row['pluginDescription']);
	$db_pluginProps['pluginVersion'] = $first_row['pluginVersion'];
	$db_pluginProps['releaseDate'] = $first_row['releaseDate'];
	$db_pluginProps['projectURL'] = $first_row['projectURL'];
	$db_pluginProps['cytoscapeVersion'] = $first_row['cyVersion'];
	$db_pluginProps['themeOnly'] = $first_row['theme_only'];
	
	// The following data were not available from the file plugin.props
	$db_pluginProps2['releaseNote'] = stripslashes($first_row['releaseNote']);
	$db_pluginProps2['releaseNoteURL'] = $first_row['releaseNoteURL'];
	$db_pluginProps2['jarURL'] = $first_row['jarURL'];
	$db_pluginProps2['sourceURL'] = $first_row['sourceURL'];
	$db_pluginProps2['reference'] = stripslashes($first_row['reference']);
	$db_pluginProps2['license'] = stripslashes($first_row['pluginLicense']);
	$db_pluginProps2['licenseRequired'] = $first_row['licenseRequired'];
	$db_pluginProps2['comment'] = stripslashes($first_row['comment']);
	$db_pluginProps2['status'] = $first_row['status'];

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
	
	$db_authInst['names'] = $db_names;
	$db_authInst['insts'] = $db_affiliations;

	$db_pluginProps['pluginAuthorsInstitutions'] = $db_authInst;
	
	// Get contact from DB
	$query = 'select * from contacts where plugin_version_id =' . $versionID;
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
		
	$contact_row = @ mysql_fetch_array($result);
	$db_contactName = $contact_row['name'];
	$db_contactEmail = $contact_row['email'];
	
	$db_pluginProps2['contactName'] = $contact_row['name'];
	$db_pluginProps2['contactEmail'] = $contact_row['email'];
	
	//StripSlashes for the names and affiliations, names may contain "" or ''	
	// they should be striped somewhere, not here
	/*
	$names_count = count($db_names);

	for ($i=0; $i<$names_count; $i++) {
		$db_names[$i] = stripslashes($db_names[$i]);
	}


	$affiliations_count = count($db_affiliations);
	for ($i=0; $i<$affiliations_count; $i++) {
		$db_affiliations[$i] = stripslashes($db_affiliations[$i]);
	}
	*/
?>

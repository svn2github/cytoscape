<?php
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
		'         WHERE plugin_list.name = "' . mysql_real_escape_string($pluginProps['pluginName']) . '" and category_id =' . mysql_real_escape_string($category_id);

		// Run the query
		if (!($result = @ mysql_query($dbQuery, $connection)))
			showerror();

		if (@ mysql_num_rows($result) != 0) {
			//There is an old version in the DB, update the row in the table plugin_list

			$the_row = @ mysql_fetch_array($result);
			$plugin_auto_id = $the_row['plugin_auto_id'];
			//echo "There is an old version of this plugin in the DB, plugin_auto_id =" . $plugin_auto_id . "<br>";

			$theProjuctURL = "";
			if (isset($pluginProps['projectURL'])) {
				$theProjuctURL = $pluginProps['projectURL'];	
				//echo "line 616: theProjectURL =", $theProjuctURL;			
			}
			//echo "line 618 1: theProjectURL =", $theProjuctURL;			

			// Update the table "plugin_list"  
			$dbQuery = 'UPDATE plugin_list ' .
			'SET description = "' . mysql_real_escape_string(addslashes($pluginProps['pluginDescription'])) . '",' .
			'project_url ="' . mysql_real_escape_string($theProjuctURL) . '",' .
			'license ="' . mysql_real_escape_string(addslashes($license)) . '",' .
			'license_required ="' . mysql_real_escape_string($license_required) . '" ' .
			'WHERE plugin_auto_id = ' . mysql_real_escape_string($plugin_auto_id);

			//echo "<br>uploadNewPluginData.inc line 57: dbQuery =", $dbQuery, "<br>";			
			
			// Run the query
			if (!($result = @ mysql_query($dbQuery, $connection)))
				showerror();

		} else {
			//This is a new plugin, add a row in the table plugin_list

			//$plugin_unique_id = -1;
			$plugin_unique_id = getUniqueID($connection);
			
			$dbQuery = 'INSERT INTO plugin_list VALUES ' .
			'(0, "' . mysql_real_escape_string($pluginProps['pluginName']) . '", "' . mysql_real_escape_string($plugin_unique_id) . '", "' . mysql_real_escape_string(addslashes($pluginProps['pluginDescription'])) . '", "' . mysql_real_escape_string($license) . '", "' . mysql_real_escape_string($license_required) . '", "' . mysql_real_escape_string($pluginProps['projectURL']) . '",' .
			mysql_real_escape_string($category_id) . ',now())';

			// Run the query
			if (!($result = @ mysql_query($dbQuery, $connection)))
				showerror();

			$plugin_auto_id = mysql_insert_id($connection);
		}

		// Insert a row into table plugin_version
		$status = 'new';
		$dbQuery = 'INSERT INTO plugin_version VALUES (0, ' . mysql_real_escape_string($plugin_auto_id) . ', ';
		if ($plugin_file_auto_id == NULL) {
			$dbQuery .= 'NULL';
		} else {
			$dbQuery .= $plugin_file_auto_id;
		}
		$dbQuery .= ',"' . mysql_real_escape_string($pluginProps['pluginVersion']) . '",\'' .
		mysql_real_escape_string($pluginProps['releaseDate']) . '\',"' . mysql_real_escape_string($releaseNote) . '","' . mysql_real_escape_string($releaseNoteURL) . '","' . mysql_real_escape_string($pluginProps['minimumJavaVersion']) . '","' . mysql_real_escape_string($comment) . '","' . mysql_real_escape_string($jarURL) . '","' .
		$sourceURL . '","' . mysql_real_escape_string($pluginProps['cytoscapeVersion']) . '","' . mysql_real_escape_string($status) . '","' . mysql_real_escape_string($reference).'","' .mysql_real_escape_string($pluginProps['themeOnly']).'", 0,now())';
		
		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();

		$version_auto_id = mysql_insert_id($connection);
		// insert rows into author tables (authors and plugin_author)

		$authorCount = count($pluginProps['pluginAuthorsInstitutions']['names']);

		for ($i = 0; $i < $authorCount; $i++) {
			$dbQuery = 'INSERT INTO authors VALUES (0, "' . mysql_real_escape_string(addslashes($pluginProps['pluginAuthorsInstitutions']['names'][$i])) . '", "' . '' . '","' . mysql_real_escape_string($pluginProps['pluginAuthorsInstitutions']['insts'][$i]) . '","' . '' . '")';

			// Run the query
			if (!(@ mysql_query($dbQuery, $connection)))
				showerror();

			$author_auto_id = mysql_insert_id($connection);

			$authorship_seq = $i;
			$dbQuery = 'INSERT INTO plugin_author VALUES (' . mysql_real_escape_string($version_auto_id) . ', ' . mysql_real_escape_string($author_auto_id) . ',' . mysql_real_escape_string($authorship_seq) . ')';

			// Run the query
			if (!(@ mysql_query($dbQuery, $connection)))
				showerror();
		}

		// insert a row into contacts table

		//echo "contactName =",$contactName,"<br>";
		//echo "contactEmail =",$contactEmail,"<br>";

		$dbQuery = 'INSERT INTO contacts VALUES (0, "' . mysql_real_escape_string(addslashes($contactName)) . '", "' . mysql_real_escape_string($contactEmail) . '",' . mysql_real_escape_string($version_auto_id) . ', now())';

		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();
?>
	Thank you for submitting your plugin to Cytoscape. Cytoscape staff will review the data  and publish it on the cytoscape website. If your-mail address is provided, you will get confirmation via e-mail.
	<p>Go back to <a href="index.php">Back to cytoscape plugin page</a></p>
	<?php


		// Send a confirmation e-mail to user
		// Also cc to cytostaff,  new plugin is uploaded
		include "sendConfirmationEmail.inc";
		sendConfirmartionEmail(stripslashes($contactName), $contactEmail, $pluginProps['pluginName'], $pluginProps['pluginDescription']);
?>

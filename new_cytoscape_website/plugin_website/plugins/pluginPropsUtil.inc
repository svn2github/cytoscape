<?php

function isPluginVersionExists($connection, $pluginProps) {

	$query = 'SELECT version_auto_id FROM categories, plugin_list, plugin_version' .
	' WHERE categories.category_id = plugin_list.category_id ' .
	'       and plugin_list.plugin_auto_id = plugin_version.plugin_id ' .
	'       and categories.name ="' . $pluginProps['pluginCategory'] . "\" " .
	'		and plugin_list.name = "' . $pluginProps['pluginName'] . "\" " .
	'		and plugin_version.version = "' . $pluginProps['pluginVersion'] . "\"";

	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();

	if (@ mysql_num_rows($result) != 0) {
		$retValue = true;
?>
			<strong>Error:</strong> <span class="style4">This version of this plugin already exists.</span><br>
			<?php
	}
	else {
		$retValue = false;
	}

	return $retValue;
}


function validatePluginProps($pluginProps) {
	$retVal = true;
	if ($pluginProps == NULL || count($pluginProps) == 0) {
?>
		<strong>Error: plugin.props in the jar/zip file is missing or empty.</strong><br>
		<?php
		return false;
	}

	if (!array_key_exists('pluginName', $pluginProps)) {
		$retVal = false;
?>
		<strong>Error in plugin.prop: pluginName is required.</strong><br>
		<?php


	}
	else if (strpos($pluginProps['pluginName'],' ')>0) {
		$retVal = false;
?>
		<strong>Error in plugin.prop: white space is not allowed in the plugin name.</strong><br>
		<?php
		
	}
	if (!array_key_exists('pluginVersion', $pluginProps)) {
		$retVal = false;
?>
		<strong>Error in plugin.prop: PluginVersion is required.</strong><br>
		<?php
	}
	else {
		if (!preg_match('/^[0-9]+\.[0-9]+$/', $pluginProps['pluginVersion'])) {	
			$retVal = false;
			
?>
		<strong>Error in plugin.prop: plugin version must be double type, two numbers seperated by a dot (.).</strong><br>
		<?php

		} 
		else {
			// Exclude the version number, like 0.09
			list($part1, $part2) = split('\.', $pluginProps['pluginVersion']);
			if (is_numeric($part2) && $part2 < 10 && strlen($part2)>1){
				$retVal = false;
				?>
				<strong>Error in plugin.prop: plugin minor version must not start with 0</strong><br>
				<?php
			}
		}
	}
	
	
	if (!array_key_exists('pluginDescription', $pluginProps)) {
		$retVal = false;
?>
		<strong>Error in plugin.prop: pluginDescription is required.</strong><br>
		<?php


	}
	if (!array_key_exists('pluginCategory', $pluginProps)) {
		$retVal = false;
?>
		<strong>Error in plugin.prop: pluginCategory is required.</strong><br>
		<?php


	}
	else {
		if (!((strnatcasecmp($pluginProps['pluginCategory'], 'Core') == 0) ||
		    (strnatcasecmp($pluginProps['pluginCategory'], 'Analysis') == 0) ||
		    (strnatcasecmp($pluginProps['pluginCategory'], 'Network and Attribute I/O') == 0) ||
		    (strnatcasecmp($pluginProps['pluginCategory'], 'Network Inference') == 0) ||
		    (strnatcasecmp($pluginProps['pluginCategory'], 'Functional Enrichment') == 0) ||
		    (strnatcasecmp($pluginProps['pluginCategory'], 'Communication/Scripting') == 0) ||
		    (strnatcasecmp($pluginProps['pluginCategory'], 'Other') == 0)))
		{
			$retVal = false;
			?>
		<strong>Error in plugin.prop: pluginCategory must be one of the categories listed at <a href = "http://cytoscape.org/plugins25/index.php">Cytoscape plugin web site</a>.</strong><br>
		<?php
			
		}
		
	}
	
	if (!array_key_exists('cytoscapeVersion', $pluginProps)) {
		$retVal = false;
?>
		<strong>Error in plugin.prop: cytoscapeVersion is required.</strong><br>
		<?php


	}
	else {
		if ($pluginProps['cytoscapeVersion'] == null || $pluginProps['cytoscapeVersion'] == "") {
		$retVal = false;
?>
		<strong>Error in plugin.prop: cytoscapeVersion is required.</strong><br>
		<?php
		}
	}

	//If release Date is not provided, set it to today (system date).
	if (!array_key_exists('releaseDate', $pluginProps)) {
		$pluginProps['releaseDate'] = date('Y').'-'.date('m').'-'.date('d');		
	} elseif ($pluginProps['releaseDate']=='invalidDate') {
			$retVal = false;
			?>
		<strong>Error in plugin.prop: Invalid release date.</strong><br>
		<?php
	}
		
	if ($retVal == false) {
	?>	
                <br>Please check the definition of Cytoscape plugin propertiies at <a href="http://www.cytoscape.org/cgi-bin/moin.cgi/Cytoscape_Plugin_Tutorial">Cytoscape wiki</a> for detail. </p>
                <br><br>

	<?php
	}
	
	return $retVal;
}

function getReleaseDateFromStr($releaseDateStr) {
	// The releaseDateStr will have the format like "May 1, 2007"
	// The return value will be have the format like "2007-5-1"
	// If the input string is invlalid date Str, return null

	$elements = sscanf($releaseDateStr, "%s%d,%d");

	if (count($elements) == 3) {
		$month = trim($elements[0]);
		$day = $elements[1];
		$year = $elements[2];
	} else {
		return null;
	}

	if (strnatcasecmp($month, "January") == 0) {
		$month = '1';
	}
	elseif (strnatcasecmp($month, "February") == 0) {
		$month = '2';
	}
	elseif (strnatcasecmp($month, "March") == 0) {
		$month = '3';
	}
	elseif (strnatcasecmp($month, "April") == 0) {
		$month = '4';
	}
	elseif (strnatcasecmp($month, "May") == 0) {
		$month = '5';
	}
	elseif (strnatcasecmp($month, "June") == 0) {
		$month = '6';
	}
	elseif (strnatcasecmp($month, "July") == 0) {
		$month = '7';
	}
	elseif (strnatcasecmp($month, "August") == 0) {
		$month = '8';
	}
	elseif (strnatcasecmp($month, "September") == 0) {
		$month = '9';
	}
	elseif (strnatcasecmp($month, "October") == 0) {
		$month = '10';
	}
	elseif (strnatcasecmp($month, "November") == 0) {
		$month = '11';
	}
	elseif (strnatcasecmp($month, "December") == 0) {
		$month = '12';
	}
	else {
		return 'invalidDate';
	}
	
	return $year . '-' . $month . '-' . $day;
} // End of function getReleaseDateFromStr()

//Parse the authInstStr and save the data into an Array of Array
function getAuthorInstArray($authorInstStr) {

	if (empty($authorInstStr)) {
		return null;
	}

	$tmpArray = preg_split("{;}", $authorInstStr); // Split into an array

	foreach ($tmpArray as $record) {
		$tmpArray2 = preg_split("{:}", $record);
		$names[] =$tmpArray2[0]; 
		$insts[] =$tmpArray2[1]; 
	}

	$retValue['names'] = $names;
	$retValue['insts'] = $insts;
	
	return $retValue;
}


//Get the plugin properties from a Jar/Zip file
function getPluginProps($zipFileName) {
	
	$pluginProps = NULL;

	$zip = zip_open($zipFileName);
	if ($zip) {
		while ($zip_entry = zip_read($zip)) {
			$cur_file_name = zip_entry_name($zip_entry);

			if (substr_count($cur_file_name, "plugin.props") > 0) {
				
			$position = strpos($cur_file_name,"plugin.props"); //find position of plugin.props 
			if ($position != (strlen($cur_file_name) - 12)) {
				continue;
			}
				// This is the plugin.prop file 
				if (zip_entry_open($zip, $zip_entry, "r")) {
					$buf = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));

					$all_lines = explode("\n", $buf);
					$lineCount = count($all_lines);

					// Remove blank and comment lines
					for ($i = 0; $i < $lineCount; $i++) {
						$cur_line = trim($all_lines[$i]);
						if (($cur_line == "") || (strstr($cur_line, '#') == $cur_line)) {
							continue;
						}
						$lines[] = trim($all_lines[$i]);
					}

					for ($i = 0; $i < count($lines); $i++) {
						$cur_line = $lines[$i];
						if (stristr($cur_line, 'pluginName') == $lines[$i]) { // the line starts with pluginName
							$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['pluginName'] = trim(substr($cur_line, $pos +1));
						}
						if (stristr($cur_line, 'pluginDescription') == $lines[$i]) {
							$pos = strpos($cur_line, "="); // find first "="
							$desText = trim(substr($cur_line, $pos +1));

							$supportMultilineDes = true;							
							// Begin of multiple-line description
							if ($supportMultilineDes) {								
								$i=$i+1;
								while ($i < count($lines)) {
									
									$cur_line = $lines[$i];
	
									if (!(stristr($cur_line, 'pluginVersion') == $lines[$i])) {
										$desText = $desText.$cur_line;
										$i=$i+1;
									}
									else { 
										// reach the end of description text
										$i=$i-1;
										break;
									}
								}
							}
							// End of multiple-line description
																		
							$pluginProps['pluginDescription'] = $desText;
							
						}
						if (stristr($cur_line, 'pluginVersion') == $lines[$i]) {
							$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['pluginVersion'] = trim(substr($cur_line, $pos +1));
						}
						if (stristr($cur_line, 'cytoscapeVersion') == $lines[$i]) {
								$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['cytoscapeVersion'] = trim(substr($cur_line, $pos +1));
							//remove any white space
							$pluginProps['cytoscapeVersion'] = ereg_replace(' ','',$pluginProps['cytoscapeVersion']);
							$pluginProps['cytoscapeVersion'] = ereg_replace('\t','',$pluginProps['cytoscapeVersion']);

						}
						if (stristr($cur_line, 'pluginCategory') == $lines[$i]) {
							$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['pluginCategory'] = trim(substr($cur_line, $pos +1));
						}
						if (stristr($cur_line, 'projectURL') == $lines[$i]) {
							$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['projectURL'] = trim(substr($cur_line, $pos +1));
						}
						if ((stristr($cur_line, 'pluginAuthorsInstitutions') == $lines[$i]) || (stristr($cur_line, 'pluginAuthorsIntsitutions') == $lines[$i])) {
							$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['pluginAuthorsInstitutions'] = getAuthorInstArray(trim(substr($cur_line, $pos +1)));
						}
						if (stristr($cur_line, 'releaseDate') == $lines[$i]) {
							$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['releaseDate'] = getReleaseDateFromStr(trim(substr($cur_line, $pos +1)));
						}
						if (stristr($cur_line, 'themeOnly') == $lines[$i]) {
							$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['themeOnly'] = trim(substr($cur_line, $pos +1));
						}
					}

					//If themeOnly is not provided, set it to "no".
					if (!array_key_exists('themeOnly', $pluginProps)) {
						$pluginProps['themeOnly'] = 'no';					
					}
					
					//If release Date is not provided, set it to today (system date).
					if (!array_key_exists('releaseDate', $pluginProps)) {
						$pluginProps['releaseDate'] = date('Y').'-'.date('m').'-'.date('d');					
					}
					
					//Not defined yet, may need it, in the future
					$pluginProps['minimumJavaVersion'] = '0.0';
					zip_entry_close($zip_entry);
				}
				break;
			}
		} // end while loop
		zip_close($zip);
	} 
	else {
		//failed to open the zip file
	}
		
	return $pluginProps;
	
}// end of getPluginProps()

//Get the contents of MANIFEST file from a Jar/Zip file
// Then check if any line start with "Cytoscape-Plugin"
function validateManifestFile($zipFileName) {
	$retValue = false;
	$javaVersion_tooHigh = false;
	$pluginClassName ="";

	$zip = zip_open($zipFileName);
	if ($zip) {
		while ($zip_entry = zip_read($zip)) {
			$cur_file_name = zip_entry_name($zip_entry);

			if (substr_count($cur_file_name, "MANIFEST.MF") > 0) {	
				// This is the MANIFEST.MF file 
				if (zip_entry_open($zip, $zip_entry, "r")) {
					$buf = zip_entry_read($zip_entry, zip_entry_filesize($zip_entry));

					$all_Lines = explode("\n", $buf);
					
					for ($i = 0; $i < count($all_Lines); $i++) {
						$cur_line = $all_Lines[$i];
						if ((!empty($cur_line))&& stristr($cur_line,'Cytoscape-Plugin') == $cur_line) {
							//echo "MANIFEST.MF: Found attribute Cytoscape-Plugin<br>";

							$pluginClassName = substr($cur_line, 17);

							$retValue = true;
						}	
					}
				}
				break;
			}
		} //while loop
		zip_close($zip);
	}
	else {
		//failed to open the zip file
	}

	$version = getJavaVersion($zipFileName, $pluginClassName);

	if ($version > 49){ // 49 is the jdk version (java platform version 1.5)
		$javaVersion_tooHigh = true;
	}

	if ($retValue == false && $javaVersion_tooHigh == false) {
	?>
	<br><b>Error</b>: MANIFEST.MF is either missing or the attribute Cytoscape-Plugin is not defined.	
                <br>Please check the definition of Cytoscape plugin MANIFEST.MF at <a href="http://www.cytoscape.org/cgi-bin/moin.cgi/Cytoscape_Plugin_Tutorial">Cytoscape wiki</a> for detail. </p>
                <br><br>
	<?php
	}

	if ($javaVersion_tooHigh == true) {

		?>
	<br><b>Error</b>: Plugin should be compiled with Java 1.5 or less.	
                <br><br>			
		<?php
		$retValue = false;

	}

	return $retValue;
} // end of validateManifestFile()


// Get the version of java class
function getJavaVersion($jarFileName, $javaClassName) {

	$tokens = explode('.', trim($javaClassName));
	$baseClassName = $tokens[count($tokens)-1];

	$zip = zip_open($jarFileName);
	
	if ($zip) {
		while ($zipcontent = zip_read($zip)) {
			$zip_entry_name = zip_entry_name($zipcontent);
			
			if (strstr($zip_entry_name, '/'.$baseClassName.'.class')){
				
				if (zip_entry_open($zip, $zipcontent)){ 
					$content = zip_entry_read($zipcontent,1024);
					// save the content in file 					
					// Open the file and erase the contents if any  
					$fp = fopen("./_tmp/tmp.class", "w");  
					fwrite($fp, $content);  
					fclose($fp);  
					
					zip_entry_close($zipcontent);
				}
				break;				
			}
		}
		zip_close($zip);
	}
	else {
		//failed to open the zip file
		return -1;
	}
	
	$cmd = "./get_class_major ./_tmp/tmp.class";
	$result =shell_exec($cmd);

	return $result;
}

?> 

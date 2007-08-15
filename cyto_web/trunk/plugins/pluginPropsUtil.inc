<?php
function validatePluginProps($pluginProps) {
	$retVal = true;
	if (count($pluginProps) == 0) {
?>
		Error: plugin.props in the jar/zip file is either non-existence or empty.<br>
		<?php


		return false;
	}

	if (!array_key_exists('pluginName', $pluginProps)) {
		$retVal = false;
?>
		Error in plugin.prop: pluginName is required.<br>
		<?php


	}
	if (!array_key_exists('pluginVersion', $pluginProps)) {
		$retVal = false;
?>
		Error in plugin.prop: PluginVersion is required.<br>
		<?php


	}
	if (!array_key_exists('pluginDescription', $pluginProps)) {
		$retVal = false;
?>
		Error in plugin.prop: pluginDescription is required.<br>
		<?php


	}
	if (!array_key_exists('pluginCategory', $pluginProps)) {
		$retVal = false;
?>
		Error in plugin.prop: pluginCategory is required.<br>
		<?php


	}
	if (!array_key_exists('cytoscapeVersion', $pluginProps)) {
		$retVal = false;
?>
		Error in plugin.prop: cytoscapeVersion is required.<br>
		<?php


	}
	if (!array_key_exists('releaseDate', $pluginProps)) {
		$retVal = false;
?>
		Error in plugin.prop: releaseDate is required.<br>
		<?php


	} else {
		//releaseDate = $year.'-'.$month.'-'.$day;
		$tmpArray1 = preg_split("{,}", $pluginProps['releaseDate']); // Split into an array
		$tmpArray2 = preg_split("{' '}", $tmpArray1[0]);
		$year = $tmpArray[1];

	}

	//return false;
	return retVal;
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
	$zip = zip_open($zipFileName);
	if ($zip) {
		while ($zip_entry = zip_read($zip)) {
			$cur_file_name = zip_entry_name($zip_entry);

			if (substr_count($cur_file_name, "plugin.prop") > 0) {
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
							$pluginProps['pluginDescription'] = trim(substr($cur_line, $pos +1));
						}
						if (stristr($cur_line, 'pluginVersion') == $lines[$i]) {
							$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['pluginVersion'] = trim(substr($cur_line, $pos +1));
						}
						if (stristr($cur_line, 'cytoscapeVersion') == $lines[$i]) {
							$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['cytoscapeVersion'] = trim(substr($cur_line, $pos +1));
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
					}

					zip_entry_close($zip_entry);
				}
				break;
			}
		} // end while loop

		zip_close($zip);

		return $pluginProps;

	} // end of getPluginProps()

}
/* test code
$pluginProps = getPluginProps("groupTool.jar");
echo "Array size = ", count($pluginProps), "\n";
	
$keys = array_keys($pluginProps);
	
foreach ($keys as $key) {
	echo $key, '--', $pluginProps[$key],"\n";
}
*/
?> 

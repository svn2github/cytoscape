<?php
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
							$pluginProps['pluginAuthorsInstitutions'] = trim(substr($cur_line, $pos +1));
						}
						if (stristr($cur_line, 'releaseDate') == $lines[$i]) {
							$pos = strpos($cur_line, "="); // find first "="
							$pluginProps['releaseDate'] = trim(substr($cur_line, $pos +1));
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

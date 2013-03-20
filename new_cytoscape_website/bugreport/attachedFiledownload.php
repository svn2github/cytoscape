<?php 
	include "functions.php"; 

$file_id = null;
if (isset ($_GET['file_id'])) {
		$file_id = ($_GET['file_id']);
}
else {
	exit("File ID unknow!");
}


$connection = getDBConnection(NULL);// user permission

$query = "SELECT * FROM attached_files ".
			"WHERE file_auto_id = $file_id";

// Run the query
if (!($result = @ mysql_query($query,$connection)))
	showerror();

if (mysql_num_rows($result) == 1) {
	$fileName = @ mysql_result($result, 0, "file_name");
	
	// If space in file name, the downloaded file may lose file extension
	// replace white spaces in file name with underscore
	$fileName = Str_replace(" ","_", $fileName);
	
	$fileType = @ mysql_result($result, 0, "file_type");
	$fileContent = @ mysql_result($result, 0, "file_data");

	header("Content-type: $fileType");
	header('Content-Disposition: attachment; filename='.$fileName); 
	echo $fileContent;

	// Update the usage table after download
	//updateUsageLog($connection, $plugin_file_id);

} else {
	echo "File doesn't exist.";
}
?>

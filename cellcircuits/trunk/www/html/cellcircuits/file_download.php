<?php
// Download files: PDF, supplement_notes, raw_data

// Include the DBMS credentials
include 'db.php';

$debug = false;
if ($debug) {
	$file_id = 2;
	$file_type = 'supplement_notes';
}
else {
	$file_id = $_GET['file_id'];
	$file_type = $_GET['file_type'];
}

// default to raw data
$db_table = "raw_files";
$query_attribute = 'raw_file_auto_id';

if ($file_type == 'pdf') {
	$db_table = "pdf_files";
	$query_attribute = 'pdf_file_auto_id';
}
else if ($file_type == 'supplement_notes') {
	$db_table = "supplement_material_files";
	$query_attribute = 'id';
}

$dbQuery = "SELECT * FROM $db_table ".
			"WHERE $query_attribute = $file_id";

//echo "dbQuery = $dbQuery<br>\n";

// Run the query
if (!($result = @ mysql_query($dbQuery,$connection)))
	showerror();

if (mysql_num_rows($result) == 1) {
	$fileName = @ mysql_result($result, 0, "file_name");
	$fileType = @ mysql_result($result, 0, "file_type");
	$fileContent = @ mysql_result($result, 0, "data");

	header("Content-type: $fileType");
	header('Content-Disposition: attachment; filename='.$fileName); 
	echo $fileContent;
	//echo $fileName;	
	//echo $fileType;
} else {
	echo "File doesn't exist.";
}
?>
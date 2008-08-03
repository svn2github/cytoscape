<?php
include_once 'db.php';

// default type is 'unknown'
$image_type = "unknown";

$debug = false;
if ($debug) {
	$image_file_id = 3;
	$image_type = 'cover_image';
}
else {
	$image_file_id = $_GET['image_file_id'];
	$image_type = $_GET['image_type'];
}

if ($image_type == "unknown") {
	echo "Image type unknown<br>\n";
	exit;
}

if ($image_type =='cover_image') {
	$image_table = 'cover_image_files';
	$query_attribute = 'cover_image_file_auto_id';
}
else if ($image_type =='network_image') {
	$image_table = 'network_image_files';
	$query_attribute = 'id';
}


list($image_type, $data) = getImage($image_table,$query_attribute,$image_file_id, $connection);

	
header("Content-type: image/$image_type");
echo $data;


function getImage($image_table,$query_attribute, $image_file_id, $connection) {
	$dbQuery  = "SELECT file_name, data from $image_table ";
	$dbQuery  .= "WHERE $query_attribute =$image_file_id";
	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		return NULL;
	}
	$file_name = @ mysql_result($result, 0, "file_name");
	$tmpArray = split("\.", $file_name);
	
	if (count($tmpArray) == 2 ) {
		$image_file_ext = $tmpArray[1];
	}
	else {
		$image_file_ext = 'unknown';
	}
	$data = @ mysql_result($result, 0, "data");
	
	return array($image_file_ext, $data);
}

?>


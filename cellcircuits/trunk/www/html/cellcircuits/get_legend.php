<?php
// get legend page, given a pub_id

// Include the DBMS credentials
include 'db.php';

// default type is 'unknown'
$pub_id = -1;

$debug = false;
if ($debug) {
	$pub_id = 2;
}
else {
	$pub_id = $_GET['pub_id'];
}

$dbQuery = "SELECT * FROM legend_files ".
			"WHERE pub_id = $pub_id";

//echo "dbQuery = $dbQuery<br>\n";

// Run the query
if (!($result = @ mysql_query($dbQuery,$connection)))
	showerror();

#$record_count = @ mysql_num_rows($result);
	
$legend_file = NULL;
$img_files = NULL;
$other_files = NULL;

while ($_row = @ mysql_fetch_array($result)) {
	//print_r(array_keys($_row));
	$file = NULL;
	$file['id'] = $_row["id"];
	$file['file_name'] = $_row["file_name"];
	$file['file_type'] = $_row["file_type"];
	$file['data'] = $_row["data"];

	#echo "file['file_name'] = ".$file['file_name']."\n";

	if ($file['file_name'] == 'legend_FAQ.html') {
		$legend_file = $file;
	}
	else if (isImageFile($file['file_name'])) {
		$image_files[] = $file;
	}
	else {
		$other_files[] = $file;
	}
}

// replace the image files with PHP script
for ($i=0; $i<count($image_files); $i++) {
	$img_file_id = $image_files[$i]['id'];

	$fileName = $image_files[$i]['file_name'];
	$tmpArray = split('\.', $fileName);

	$fileBase = $tmpArray[0];
	$fileExt = $tmpArray[1];
	
	//$pattern = "'\./$fileBase\\.$fileExt'";

	$pattern1 = "'href=.\./$fileBase\\.$fileExt'";
	$replacement1 = "href=\"getImage.php?image_type=legend_image&image_file_id=$img_file_id&return_type=html";

	$pattern2 = "'src=.\./$fileBase\\.$fileExt'";
	$replacement2 = "src=\"getImage.php?image_type=legend_image&image_file_id=$img_file_id&return_type=image";

	//$replacement = "getImage.php?image_type=legend_image&image_file_id=$img_file_id";
	//$subject = $legend_file['data'];
	
	$legend_file['data'] = preg_replace($pattern1, $replacement1, $legend_file['data']);
	$legend_file['data'] = preg_replace($pattern2, $replacement2, $legend_file['data']);

}

#echo "\n\n\nAfter replacement\n\n";
echo $legend_file["data"];


function isImageFile($file_name) {
	$dot_position = strrpos($file_name,".");
	$file_ext = substr($file_name, $dot_position);
	if ($file_ext == '.jpg' || $file_ext == '.gif' || $file_ext == '.jpeng') {
		return true;
	}
	return false;
}



# header("Content-type: $fileType");
# header('Content-Disposition: attachment; filename='.$fileName); 
# echo $fileContent;
?>
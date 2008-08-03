<?php
include_once 'db.php';
include_once 'cc_utils.php';
include_once 'publication_unload_util.php';

// get rawdata_id
$debug = false;
if ($debug) {
	$rawdata_id = 3;
}
else {
	if (isset($_GET['rawdata_id'])) {
		$rawdata_id = $_GET['rawdata_id'];
	}
	else {
		echo "Unknown data set id";
		exit();
	}
}


if (unload_one_publication($rawdata_id, $connection))
{
		?>The data set is unpublished successfully!<br>Go back to the page --<a href="cc_admin.php"> publication management</a><?php
}
else {
		?>Failed to delete derived data set!<br><?php
}
///// End of Main logic /////////////////

?>

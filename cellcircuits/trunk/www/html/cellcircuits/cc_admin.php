<?php
include "logininfo.inc";
// Include the DBMS credentials
include_once 'cc_utils.php';
include 'db.php';

$data = getRawDataListFromDB($connection);

?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Publication Management</title>
<style type="text/css">
<!--
.style1 {
	font-size: 18px;
	font-weight: bold;
}
-->
</style>
</head>

<body>
<p>&nbsp;</p>
<p align="center" class="style1">Publication Management </p>
<p>&nbsp;</p>
<table width="821" border="1">
  <tr>
    <th width="47" scope="col">Data ID</th>
    <th width="284" scope="col">Publication</th>
    <th width="67" scope="col">Status</th>
    <th width="65" scope="col">Action</th>
    <th width="94" scope="col">Download</th>
    <th width="70" scope="col">Edit</th>
    <th width="61" scope="col">Delete</th>
    <th width="81" scope="col">Last modified</th>
  </tr>
  
  <?php
  	for ($i=0; $i<count($data); $i++) {
		$record = $data[$i];
  ?>
  <tr>
    <td><?php echo $record['raw_data_auto_id']; ?></td>
    <td><?php echo $record['pubmed_html_full'] ; ?></td>
    <td><div align="center"><?php echo $record['status']; ?></div></td>
    <td><div align="center"><?php if ($record['status'] == 'unpublished' || $record['status'] == 'new') 
			{ 
			?> 
			<a href="publication_load.php?rawdata_id=<?php echo $record['raw_data_auto_id']; ?>" >Publish</a> 
			<?php 
			} 
			else if ($record['status'] == 'load_fail'){
			?>
			<a href="publication_unload.php?rawdata_id=<?php echo $record['raw_data_auto_id']; ?>" >Unload</a>			
			<?php
			}
			else {
			?>
			<a href="publication_unload.php?rawdata_id=<?php echo $record['raw_data_auto_id']; ?>" >Unpublish</a>
			<?php  
			} 
			?>
	</div></td>
    <td><a href="file_download.php?file_type=raw_data&file_id=<?php echo $record['raw_data_auto_id']; ?>" ><?php echo $record['file_name'];?></a></td>
    <td><div align="center"><a href="data_submit_step2.php?mode=edit&rawdata_id=<?php echo $record['raw_data_auto_id']; ?>" >Edit</a></div></td>
    <td><div align="center"><a href="publication_delete.php?rawdata_id=<?php echo $record['raw_data_auto_id']; ?>" >Delete</a></div></td>
    <td><a href="publication_delete.php?rawdata_id=<?php echo $record['raw_data_auto_id']; ?>" ><?php echo $record['time_stamp']; ?></a></td>
  </tr>
 <?php
 	}
 ?>
</table>
<p>
  <label></label>
</p>
</body>
</html>


<?php

// Retrive list of raw data in table "submission_data"
function getRawDataListFromDB($connection) {

	$dbQuery = "select raw_data_auto_id, pmid, pubmed_xml_record, pubmed_html_full,  pubmed_html_medium, pubmed_html_short, data_file_id, status, time_stamp, file_name "; 
	$dbQuery .= "from submission_data, raw_files ";
	$dbQuery .= "where submission_data.data_file_id = raw_files.raw_file_auto_id order by status";

	//echo "<br>dbQuery = " . $dbQuery . "<br>";
	
	// Run the query
	if (!($dataRecords = @ mysql_query($dbQuery, $connection)))
		showerror();

	$record_count = @ mysql_num_rows($dataRecords);
	
	$data = NULL;
	while ($_row = @ mysql_fetch_array($dataRecords)) {
		//print_r(array_keys($_row));
		$record = NULL;
		$record['raw_data_auto_id'] = $_row["raw_data_auto_id"];
		$record['pmid'] = $_row["pmid"];
		$record['pubmed_xml_record'] = $_row["pubmed_xml_record"];
		$record['pubmed_html_full'] = $_row["pubmed_html_full"];
		$record['pubmed_html_medium'] = $_row["pubmed_html_medium"];
		$record['pubmed_html_short'] = $_row["pubmed_html_short"];

		//$record['contact_person'] = $_row["contact_person"];
		//$record['email'] = $_row["email"];
		$record['data_file_id'] = $_row["data_file_id"];
		//$record['comment'] = $_row["comment"];
		$record['status'] = $_row["status"];
		$record['time_stamp'] = $_row["time_stamp"];
		$record['file_name'] = $_row["file_name"];
		
		$data[] = $record;
	}
	
	return $data;
} //


?>
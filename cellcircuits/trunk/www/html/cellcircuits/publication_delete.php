<?php
include_once 'cc_utils.php';
include_once 'publication_unload_util.php';

$debug = false;
if ($debug) {
	$rawdata_id = 2;
	$tried = 'yes';
	$deleteAction = 'not_cancel';
}
else {
	$rawdata_id = NULL;
	if (isset ($_GET['rawdata_id'])) {
		$rawdata_id = $_GET['rawdata_id'];
	}
	if (isset ($_POST['rawdata_id'])) {
		$rawdata_id = $_POST['rawdata_id'];
	}
	
	$deleteAction = NULL;
	if (isset ($_POST['delete'])) {
		$deleteAction = $_POST['delete'];
	}
	$tried = NULL;
	if (isset ($_POST['tried'])) {
		$tried = 'yes';
	}
}


if (!($tried)) {
	// Include the DBMS credentials
	include_once 'db.php';

   	//construct the query to get the rawdata_file_id from submission_data
    $query = 'select pubmed_xml_record from submission_data where raw_data_auto_id ='.$rawdata_id;
	//echo "query =".$query."<br>";
	
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		$pub_html = "Failed to get the publication from DB.";
	}
	else {
		$pubmed_xml_record = @ mysql_result($result, 0, "pubmed_xml_record");
		//echo "$pubmed_xml_record";
		$pub_html = convert_xml2html($pubmed_xml_record, 'pubmedref_to_html_full.xsl');  
	}
?>



<form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="deleterawdata" id="deleterawdata">
  <label></label>
  <label></label>
<table width="605" border="0">
    <tr>
      <td width="595">&nbsp;</td>
    </tr>
    <tr>
      <td>Are you sure you want to delete the following publication? </td>
    </tr>
	    <tr>
	      <td>&nbsp;</td>
    </tr>
	    <tr>
      <td><p><?php echo $pub_html; ?></p>        </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><label>
        <input name="tried" type="hidden" id="tried" value="yes">
        <input name="rawdata_id" type="hidden" id="rawdata_id" value="<?php echo $rawdata_id;?>">
		
        <div align="center">
          <input name="delete" type="submit" id="delete" value="yes"> &nbsp;&nbsp;
          <input name="delete" type="submit" id="delete" value="cancel">
        </div>
      </label></td>
    </tr>
  </table>
</form>
<?php


} else { // process the data

	// If user press cancel button, redirect the user to the admin page
	if ($deleteAction == 'cancel') {
		header('location:cc_admin.php');
		exit();
	}

	// User confirmed to delete the plugin version

	// Include the DBMS credentials
	include_once 'db.php';

	// Step 1 -- unload  the publication if the publication was already loaded
	unload_one_publication($rawdata_id, $connection);

	// Step 2 -- delete raw_data record
	
   	//construct the query to get the rawdata_file_id from submission_data
    $query = 'select data_file_id from submission_data where raw_data_auto_id ='.$rawdata_id;

	//echo "query =".$query."<br>";
	
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();

	$theRow = @ mysql_fetch_array($result);
	$rawdata_file_id = $theRow['data_file_id'];

	//echo "rawdata_file_id = ".$rawdata_file_id."<br>";


	// delete the record in raw_files table
    $query2 = 'delete from raw_files where raw_file_auto_id ='.$rawdata_file_id;
    // Run the query
	if (!($result = @ mysql_query($query2, $connection)))
		showerror();	
    
	// delete the record in sunmission_data table
    $query3 = 'delete from submission_data where raw_data_auto_id ='.$rawdata_id;
    // Run the query
	if (!($result = @ mysql_query($query3, $connection)))
		showerror();	

	// delete successful, redirect to admin page	
	?>
	The data set is deleted. <a href="cc_admin.php">Back to publication management page</a>
	<?php
}
?>
</body>
</html>

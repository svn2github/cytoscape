<?php //include "logininfo.inc"; ?>

<?php
include 'functions.php';

$bugid = getBugID($_GET, $_POST);

$deleteAction = NULL;
if (isset ($_POST['delete'])) {
	$deleteAction = $_POST['delete'];
}
$tried = NULL;
if (isset ($_POST['tried'])) {
	$tried = 'yes';
}

if (!($tried)) {	
	// show the web page to confirm the deletion action
	showPageHeader("Delete a bug report");
	showDeleteForm($bugid);	 
	showPageTail();	
} else { // process the data

	// If user press cancel button, redirect the user to the admin page
	if ($deleteAction == 'cancel') {
		header('location:bugreportadmin.php');
		exit();
	}

	// User confirmed to delete this bug report
	//deleteReportFromDB($bugid);
	markReportStatusAsDeleted($bugid);

	showPageHeader("Delete a bug report");
	
	// delete successful, redirect to admin page	
	?>
	The bug report is deleted. <a href="bugreportadmin.php">Back to Bug report administration page</a>
	<?php 	
	showPageTail();
}

///////////////////// End of page ////////////////////////////////////

function markReportStatusAsDeleted($bugid){

	$connection = getDBConnection("edit");

	$query = "Update bugs SET status = 'deleted' WHERE bug_auto_id =$bugid";
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
}


function deleteReportFromDB($bugid){
		
	$connection = getDBConnection("edit");

	// get attached file ID from bug_file
	$file_id = null;
	$query = "SELECT file_id FROM bug_file WHERE bug_id =$bugid";
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();

	if (@ mysql_num_rows($result) != 0) {
		$_row = @ mysql_fetch_array($result);
		$file_id = $_row['file_id'];
	}
	
	// delete the attached file if any
	if ($file_id != null){
    	$query = "delete from attached_files where file_auto_id=$file_id";
		// Run the query
		if (!($result = @ mysql_query($query, $connection)))
			showerror();
    }
	
    // delete the records from table 'bug_file' if any	
    $query = "delete from bug_file where bug_id=$bugid";
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
    
   	// Get reporter_id for this bug report
	$reporter_id = null;
	$query = "SELECT reporter_id FROM bugs WHERE bug_auto_id =$bugid";
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();

	$_row = @ mysql_fetch_array($result);
	$reporter_id = $_row['reporter_id'];
		

	// Get the number of reports reported by this reporter
	$query = "SELECT bug_auto_id FROM bugs WHERE reporter_id =$reporter_id";
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
	
	$reportCount = @ mysql_num_rows($result);
	if ($reportCount == 1){		
		// delete the reporter because the reporter only report this bug
    	$query = "delete from reporter where reporter_auto_id=$reporter_id";
		// Run the query
		if (!($result = @ mysql_query($query, $connection)))
			showerror();
	}
	else {
		// the reporter reported more than one report, we can not delete it for now
	}

	// delete the master record of this bug report
	$query = "delete from bugs where bug_auto_id=$bugid";
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
	
}



function getBugID($_GET, $_POST){
	$bugid = NULL;
	
	if (isset ($_GET['bugid'])) {
		$bugid = $_GET['bugid'];
	}
	
	if (isset ($_POST['bugid'])) {
		$bugid = $_POST['bugid'];
	}
	
	return $bugid;
}



function showDeleteForm($bugid) {
	?>
	<form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="deletebug" id="deletebug">
  <label></label>
  <label></label>
<table width="605" border="0">
    <tr>
      <td width="595">&nbsp;</td>
    </tr>
    <tr>
      <td>Are you sure you want to delete this bug report? </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><label>
        <input name="tried" type="hidden" id="tried" value="yes">
        <input name="bugid" type="hidden" id="bugid" value="<?php echo $bugid;?>">
		
        <div align="center">
          <input name="delete" type="submit" id="delete" value="yes"> &nbsp;&nbsp;
          <input name="delete" type="submit" id="delete" value="cancel">
        </div>
      </label></td>
    </tr>
  </table>
</form>

	<?php	
}


?>

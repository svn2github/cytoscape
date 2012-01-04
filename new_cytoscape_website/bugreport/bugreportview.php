<?php 
	include "functions.php"; 
?>

<?php

$bugid = null;
if (isset ($_GET['bugid'])) {
		$bugid = ($_GET['bugid']);
}
else {
	exit("BugID unknow!");
}

showPageHeader("View Bug report #".$bugid);

$bugreport = getBugReportFromDB($bugid);
?>

<div>
<div id = "view_title">
Detail of bug report #<?php echo $bugid; ?>
</div>


<div id = "view_name">
<b>Reporter Name:</b> <?php echo $bugreport['reportername']?>
</div>
<div id = "view_email">
<b>Reporter E-mail:</b> <?php echo $bugreport['reporteremail']?>
</div>

<div id = "view_date">
<b>Date submitted:</b> <?php echo $bugreport['sysdate']?>
</div>

<div id = "view_cyversion">
<b>Cytoscape version:</b> <?php echo $bugreport['cyversion']?>
</div>

<div id = "view_os">
<b>os :</b> <?php echo $bugreport['os']?>
</div>

<div id = "view_description">
<b>Problem description:</b> <?php echo $bugreport['description']?>
</div>

<div id = "view_attachedfile">
<?php if (isset($bugreport['file_id'])) { ?>
<b>Attached file:</b> <?php echo "<a href=\"attachedFiledownload.php?file_id=".$bugreport['file_id']."\">".$bugreport['file_name']."</a>"; ?>
<?php } ?>
</div>


</div>


<?php 
showPageTail();
?>


<?php 
function getBugReportFromDB($bugid){
	$bugreport = null;	

	$connection = getDBConnection(NULL);// user permission
	
	// 
	$query = "SELECT * FROM bugs,reporter where bugs.reporter_id = reporter.reporter_auto_id and ".
			"bug_auto_id=$bugid";
	
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
	
	if (@ mysql_num_rows($result) != 0) { 
		$_row = @ mysql_fetch_array($result);
			    
		$bugreport['reportername'] = $_row['name'];
		$bugreport['reporteremail'] = $_row['email'];

		$bugreport['sysdate'] = $_row['sysdat'];
		$bugreport['cyversion'] = $_row['cyversion'];
			
		$bugreport['os'] = $_row['os'];
		$bugreport['description'] = $_row['description'];		
	}

	// Get attached file IDs if any
	$query = "SELECT file_id, file_name FROM bug_file,attached_files where file_id = file_auto_id and bug_id =$bugid";
	
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
	
	if (@ mysql_num_rows($result) != 0) { 
		$_row = @ mysql_fetch_array($result);
		$bugreport['file_id'] = $_row['file_id'];
		$bugreport['file_name'] = $_row['file_name'];
	}
	
	return $bugreport;
}


?>
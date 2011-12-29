<?php 
	//include "logininfo.inc";
	include "functions.php"; 
?>

<?php 
	$pageTitle = "Bug Report Administration";
	showPageHeader($pageTitle);
	
	$connection = getDBConnection('edit');// staff permission
?>
<div id="bugreporttitle">
Bug Report Administration
</div>

<div id="tableheader"> 
<div class="id">ID</div>
<div class="name">Name</div>
<div class="date">Date</div>
<div class="description">Decription</div>
<div class="delete">Delete</div>
<div class="lastmodified">Last Modified</div>
</div>

<?php 
	// Check if the reporter already existed in table 'reporter'
	$dbQuery = "SELECT * FROM bugs,reporter where bugs.reporter_id = reporter.reporter_auto_id order by bug_auto_id";
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) != 0) {
		// 
		while($_row = @ mysql_fetch_array($result))
		{	    
			$bug_auto_id = $_row['bug_auto_id'];			
			$name = $_row['name'];
			$date = $_row['date'];
			$description = $_row['description'];
			$lastModified = $_row['editdat'];
				
			?>
			<div class="bugreportrow">
				<div class="id"><?php echo $bug_auto_id; ?></div>			
				<div class="name"><?php echo $name; ?></div>
				<div class="date"><?php echo $date; ?></div>
				<div class="description"><?php echo $description; ?></div>
				<div class="delete"><?php echo "Delete"; ?></div>
				<div class="lastmodified"><?php echo $lastModified; ?></div>	
			</div>
			<?php
			
			
			
		}
	}
	
?>

<?php 
	showPageTail();	
	///////////////////// End of page ////////////////////////////////////
?>

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
   <h1>Bug Report Administration</h1>
</div>

<div id="bugreport_tableheader"> 
<div class="id">ID</div>
<div class="name">Name</div>

<div class="date">Date</div>
<div class="description">Description</div>
<div class="view">View</div>
<div class="delete">Delete</div>
<!-- <div class="lastmodified">Last Modified</div>  -->
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
			$date = $_row['sysdat'];
			$description = $_row['description'];
			if (strlen($description) > 50){
				$description = substr($description, 0, 49)."...";
			}
			
			$delete = "<a href=\"bugreportdelete.php?bugid=".$bug_auto_id."\">Delete</a>";
			$view = "<a href=\"bugreportview.php?bugid=".$bug_auto_id."\" target=\"_blank\">View</a>";
			
			$lastModified = $_row['editdat'];
				
			?>
			<div class="bugreportrow">
				<div class="id"><?php echo $bug_auto_id; ?></div>			
				<div class="name"><?php echo $name; ?></div>
				
				<div class="date"><?php echo $date; ?></div>
				<div class="description"><?php echo $description; ?></div>
				<div class="view"><?php echo $view; ?></div>
				<div class="delete"><?php echo $delete; ?></div>
				<!-- 
				<div class="lastmodified"><?php echo $lastModified; ?></div>	
			 -->
			</div>
			<?php
			
			
			
		}
	}
	
?>
<div id="seperator"></div>
<?php 
	showPageTail();	
	///////////////////// End of page ////////////////////////////////////
?>

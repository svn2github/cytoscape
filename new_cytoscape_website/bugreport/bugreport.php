<?php

// mode = 'new', Data is submited by user
// mode = 'edit', Cytostaff edit the data in bugs DB
$mode = 'new'; // by default it is 'new'

// If bugid is provided through URL, it is in edit mode
$bugid = getBugID($_GET, $_POST);

if ($bugid != NULL && $bugid != 0) {
	$mode = 'edit';
}

$pageTitle = getPageTitle($mode);

showPageHeader($pageTitle);
?>

<div class="blockfull">

<?php

$tried = NULL;
if (isset ($_POST['tried'])) {
	$tried = 'yes';
}

$connection = getDBConnection($mode);

// initialize the variables
$bugReport = NULL;

// Case for 'edit', pull data out of DB for the given bugID
if (($tried == NULL) && ($mode == 'edit')) {
	$bugReport = getBugReport($connection);	
}

// Remember the form data. If form validation failed, these data will
// be used to fill the refreshed form. If pass, they will be saved into
// database

//include "formUserInput_remember.inc";
$bugReport = getBugReportFromForm($_GET, $_POST, $_FILES, $_SERVER);

//////////////////////// Form validation ////////////////////////
$validated = isUserInputValid($bugReport);

/////////////////////// Form definition ////////////////////////
if (!(($tried != NULL && $tried == 'yes') && $validated)) {
	?>	<div class="BugReport">
	      <h2>Cytoscape Bug Report</h2>
	<?php
		showForm($bugReport);
	?>
	    </div>
	<?php 
}
else {
	////////////////////////// form processing /////////////////////////
	// if mode = 'new', takes the details of the bug from user with status = 'new'.
	// if mode = 'Edit', update the bug info in bugs DB

	// In case of edit, do updating
	if ($mode == 'edit') {
		updateBug($connection, $bugReport);
	}
	
	if ($mode == 'new') {
		//process the data and Save the data into DB.		
		submitNewBug($connection, $bugReport);
	} 
}
?>

<script src="http://cytoscape.org/js/footer.js"></script> 
</body>
</html>





<?php 

function isUserInputValid($userInput) {
	if ($userInput == NULL){
		return false;
	}
	
	return true;
}


function updateBug($connection, $bugReport){
	echo "<br>Entering updateBug() ....<br>";
}


function submitNewBug($connection, $bugReport){

	echo "<br>Entering submitNewBug ...<br>";
	
	// Load attached files first
	if ($bugReport['attachedFiles'] != NULL){
		echo "<br>file attached ...<br>";
		echo "<br>"+$bugReport['attachedFiles']['tmp_name']+"<br>";
	}
	else {
		echo "<br>no file attached ...<br>";		
	}
		
	// Check if the reporter already existed in table 'reporter'
	$dbQuery = "SELECT reporter_auto_id FROM reporter WHERE email ='" .$bugReport['email']."'";
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	$reporter_auto_id = null;
	if (@ mysql_num_rows($result) != 0) {
		// the reporter already existed
			$the_row = @ mysql_fetch_array($result);
			$reporter_auto_id = $the_row['reporter_auto_id'];
	}

	// the reporter is new, add it to DB
	if ($reporter_auto_id == null){
		// Insert a row into table reporter
		$dbQuery = "INSERT INTO reporter (name, email) Values ('".$bugReport['name']."','".$bugReport['email']."')";		
		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();
		$reporter_auto_id = mysql_insert_id($connection);
	}
		
	echo "<br>reporter_auto_id = ".$reporter_auto_id."<br>";
	
	
	
	
	
}


function showForm($userInput) {
	?>
      <form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="submitbug" id="form1">
<label for="tfName">Name</label>
                    <input name="tfName" type="text" id="tfName" value="<?php echo $userInput['name']; ?>" />
        <div>   

        <div>
          
          <label for="tfEmail">Email</label>
          <input name="tfEmail" type="text" id="tfEmail" value="<?php echo $userInput['email']; ?>" />
          
          * Optional,
          If you want feedback 
          
        </div>

	<div>
	  <label for="cyversion">Cytoscape version</label>
	    <input name="tfCyversion" type="text" id="cyversion" value="<?php echo $userInput['cyversion']; ?>" />
	</div>

	<div>
	  <label for="os">Operating system</label>
	  <select name="cmbOS" id="os">
	    <option  <?php if ($userInput['os'] == 'Windows') echo "selected=\"selected\""; ?> >Windows</option>
	    <option <?php if ($userInput['os'] == 'Linux') echo "selected=\"selected\""; ?>>Linux</option>
	    <option <?php if ($userInput['os'] == 'Mac') echo "selected=\"selected\""; ?>>Mac</option>
	  </select>
	</div>

    <div>
            <label for="taDescription">Problem description</label>
        </div>
        <div>
            <textarea name="taDescription" id="taDescription" cols="80" rows="10"><?php echo $userInput['description']; ?></textarea>
        </div>

        <div>

        Optional, Attachments (Session files, data files, screen-shots, etc.)
        </div>
          <input type="file" name="attachments" id="attachments" />
          
        </div>
        
        <!-- 
        <div>
        <input name="ufile[]" type="file" id="ufile[]" size="50" />
        </div>
         -->
         
        <div>
        <input name="tried" type="hidden" value="yes" />
        </div>
        <div>
        <input type="submit" name="btnBubmit" id="btnSubmit" value="Submit" />
        </div>
      </form>


	<?php 
}


function getBugReportFromForm($_GET, $_POST, $_FILES, $_SERVER){
	
	$bugReport = NULL;
	
	if (isset ($_POST['tfName'])) {
		$bugReport['name'] =$_POST['tfName']; 
	}

	if (isset ($_POST['tfEmail'])) {
		$bugReport['email'] = addslashes($_POST['tfEmail']);
	}
	
	if (isset ($_GET['cyversion'])) {
		$bugReport['cyversion'] = addslashes($_GET['cyversion']);
	}
	
	if (isset ($_POST['tfCyversion'])) {
		$bugReport['cyversion'] = addslashes($_POST['tfCyversion']);
	}
	
	
//	if (isset ($_POST['cmbOS'])) {
//		$bugReport['os'] = $_POST['cmbOS'];
//	}
	$bugReport['os'] = getOSFromUserAgent($_SERVER);
		
	if (isset ($_POST['taDescription'])) {
		$bugReport['description'] = addslashes($_POST['taDescription']);
	}	

	if (isset ($_FILES['attachments'])) {
		$bugReport['attachedFiles'] = $_FILES['attachments'];		
	}
	
	return $bugReport;
}

function getOSFromUserAgent($_SERVER){
	
	$os = "unknown";
		
	$userAgent = $_SERVER["HTTP_USER_AGENT"];
	
	if (strpos($userAgent, 'Linux') ? true : false){
		$os = "Linux";
	}
	else if (strpos($userAgent, 'Macintosh') ? true : false){
		$os = "Mac";
	}
	else if (strpos($userAgent, 'Windows') ? true : false){
		$os = "Windows";	
	}
	
	return $os;
}


function getBugID($_GET, $_POST){
	$bugid = NULL; // for edit mode only
	
	if (isset ($_GET['bugid'])) {
		$bugid = ($_GET['bugid']);
	}
	if (isset ($_POST['bugid'])) { // hidden field
		$bugid = ($_POST['bugid']);
	}
	return $bugid;	
}


function getDBConnection($mode) {
	// Include the DBMS credentials
	include 'db.inc';
		
	// Connect to the MySQL DBMS
	if ($mode == 'edit') {
		if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
			showerror();
	} 
	else // $mode == 'new'
	{		
		if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
			showerror();
	}

	// Use the bugs database
	if (!mysql_select_db($dbName, $connection)) {
		showerror();
	}
	return $connection;
}


function getPageTitle($mode) {
	// Set the page title based on the mode
	if ($mode == 'new') {
		$pageTitle = 'Submit bug to Cytoscape';
	} 
	else
	{
		if ($mode == 'edit') {
			$pageTitle = 'Edit bug in bugs DB';
		} else {
			exit ('Unknown page mode, mode must be either new or edit');
		}
	}
	return $pageTitle;
}


function showPageHeader($pageTitle) {
?>
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html xmlns="http://www.w3.org/1999/xhtml">
	    <head>
	    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	    <link href="http://cytoscape.org/css/main.css" type="text/css" rel="stylesheet" media="screen">
	    <title><?php echo $pageTitle;?></title>
	    <script type="text/javascript" 
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
	    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
	    <script type="text/javascript" src="http://cytoscape.org/js/menu_generator.js"></script>
	    
	    </head>
	
	    <body>
	<div id="container">
	<script src="http://cytoscape.org/js/header.js"></script>

<?php 
}
?>



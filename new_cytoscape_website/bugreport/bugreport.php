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
$userInput = getUserInputFromForm($_POST, $_FILES);

//////////////////////// Form validation ////////////////////////
$validated = isUserInputValid($userInput);

/////////////////////// Form definition ////////////////////////
if (!(($tried != NULL && $tried == 'yes') && $validated)) {
	?>	<div class="BugReport">
	      <h2>Cytoscape Bug Report</h2>
	<?php
		showForm($userInput);
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
            <label for="taDescription">Problem Description</label>
        </div>
        <div>
            <textarea name="taDescription" id="taDescription" cols="80" rows="10"><?php echo $userInput['description']; ?></textarea>
        </div>

        <div>

        Attachments (Session files, data files, screen-shots, etc.)
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


function getUserInputFromForm($_POST, $_FILES){
	
	$userInput = NULL;
	
	if (isset ($_POST['tfName'])) {
		$userInput['name'] =$_POST['tfName']; 
	}
	
	if (isset ($_POST['tfEmail'])) {
		$userInput['email'] = addslashes($_POST['tfEmail']);
	}
	
	if (isset ($_POST['tfCyversion'])) {
		$userInput['cyversion'] = addslashes($_POST['tfCyversion']);
	}
	
	if (isset ($_POST['cmbOS'])) {
		$userInput['os'] = $_POST['cmbOS'];
	}
	
	if (isset ($_POST['taDescription'])) {
		$userInput['description'] = addslashes($_POST['taDescription']);
	}	

	if (isset ($_FILES['attachments'])) {
		$userInput['attachedFiles'] = $_FILES['attachments'];
	}
	
	return $userInput;
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



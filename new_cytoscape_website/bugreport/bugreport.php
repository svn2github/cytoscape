<?php 
	//include "logininfo.inc";
	include "functions.php"; 
?>


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
		$submitResult1 = submitNewBug($connection, $bugReport);
		submitNewBug2Remine( $bugReport, $submitResult1);
	} 
}
?>

<?php 
	showPageTail();	
	///////////////////// End of page ////////////////////////////////////
?>



<?php 

function submitNewBug2Remine( $bugReport, $submitResult) {
	// Save the bug report in a tmp file 'newBug.jason' in jason format
	$myFile = "_newBug.json";
	$fh = fopen($myFile, 'w') or die("can't open file");
	
	$description = '\\nOS: '.$bugReport['os'].'\nCytoscape version: '.$bugReport['cyversion'].'\\n\\n'.$bugReport['description'];
	
	if ($submitResult != null){
		$description = $description."\\n\\n\\nAttached file is at ".$submitResult."\\n\\n\\n";
	}

	$description = $description.'\\n\\n\\nReported by: '.$bugReport['name'].'\nE-mail: '.$bugReport['email'];
		
	$json = 	
		"{
				\"issue\": {
				\"project_id\": \"cytoscape3\",
				\"subject\": \"".$bugReport['cysubject']."\",
				\"description\": \"".$description."\"
				}
		}";	
	
		
	fwrite($fh, $json);
		
	fclose($fh);
	
	// submit the new bug to redmine (Cytosape bug tracker)
	system("./run_curl.sh > _reportOutput.txt");
}



function isUserInputValid($userInput) {
	if ($userInput == NULL){
		return false;
	}
	
	return true;
}


function updateBug($connection, $bugReport){
	echo "<br>Entering updateBug() ....<br>";
}


// This is a security check, restrict number of bugs a user can submit within a day
function getReportCountToday($connection, $bugReport) {

	$bugCount = 0;
	
	$ip = $bugReport['ip_address'];

	$query = "Select bug_auto_id from bugs where ip_address='$ip'  and sysdat > DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
	
	// Run the query
	if (!($result = @ mysql_query($query, $connection)))
		showerror();
				
	$bugCount =mysql_num_rows($result);	

	return $bugCount;
}


function submitNewBug($connection, $bugReport){
	
	$bugCount = getReportCountToday($connection, $bugReport);
	
	if ($bugCount > 50){
		echo "<br><br>Sorry, you report is rejected, because you can not report more than 50 bugs within 24 hours.<br><br>";
	
		return;
	}
	
	// Step 1: load attached file
	$file_auto_id = null;
	// Load attached files first
	if ($bugReport['attachedFiles'] != NULL && $bugReport['attachedFiles']['name'] != NULL){
				
		$name = $bugReport['attachedFiles']['name'];
		$type = $bugReport['attachedFiles']['type'];
		$md5 = $bugReport['attachedFiles']['md5'];
		$content = $bugReport['attachedFiles']['fileContent'];
		
		$dbQuery = "INSERT INTO attached_files VALUES ";
		$dbQuery .= "(0, '$name', '$type', '$content', '$md5')";
		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();

		$file_auto_id = mysql_insert_id($connection);
	}

	// Step 2: get the reporter id
	$reporter_auto_id = null;
	// Check if the reporter already existed in table 'reporter'
	$dbQuery = "SELECT reporter_auto_id FROM reporter WHERE email ='" .$bugReport['email']."'";
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

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
		
	// Step 3: add a report to table "bugs"
	$bug_auto_id = null;
	
	$cyversion = $bugReport['cyversion'];
	$os = $bugReport['os'];
	$subject = $bugReport['subject'];
	$description = $bugReport['description'];
	$ip_address = $bugReport['ip_address'];
	$remote_host = $bugReport['remote_host'];
		
	$dbQuery = "";
	if ($reporter_auto_id == NULL){
		$dbQuery = "INSERT INTO bugs (cyversion, os, subject, description,ip_address,remote_host,sysdat) Values ('".
		$dbQuery .= "'$cyversion',"."'$os',"."'$subject',"."'$description',"."'$ip_address',"."'$remote_host'".",now())";	
	}
	else {
		$dbQuery = "INSERT INTO bugs (reporter_id, cyversion, os,subject, description, ip_address,remote_host,sysdat) Values (".
		$dbQuery .= "$reporter_auto_id".","."'$cyversion',"."'$os',"."'$subject',"."'$description',"."'$ip_address',"."'$remote_host'".",now())";	
	}
	
	// Run the query
	if (!(@ mysql_query($dbQuery, $connection)))
		showerror();
	$bug_auto_id = mysql_insert_id($connection);
	
	// step 4: add file record to table "bug_file" if any
	if ($file_auto_id != NULL){
		$dbQuery = "INSERT INTO bug_file (bug_id, file_id) Values ($bug_auto_id, $file_auto_id)";
	// Run the query
	if (!(@ mysql_query($dbQuery, $connection)))
		showerror();
	}
	
	// Step 5: Send e-mail notfication to staff about the new bug submission
	sendNotificationEmail($bugReport);	
	
	// If this bug report contains attachment, return a URL to access the bug 
	$retValue = "";
	if ($file_auto_id != null){
		// There are attachment in this bug report
		$retValue = "http://chianti.ucsd.edu/cyto_web/bugreport/bugreportview.php?bugid=".$bug_auto_id;
	}
	
	return $retValue;
}


function sendNotificationEmail($bugReport) {
		
	include 'cytostaff_emails.inc';

	$from = $cytostaff_emails[0];
	$to = "";

	for ($i=1; $i<count($cytostaff_emails); $i++){
         	$to = $to . $cytostaff_emails[$i] . " ";
	}
		
	$subject = "[cytoweb-bug] New bug submitted by ".$bugReport['name'];
	
	$body = $bugReport['description']."\n\nAdmin URL: http://chianti.ucsd.edu/cyto_web/bugreport/bugreportadmin.php";
	
	?>
	Thank you for submitting bug report to Cytoscape, Cytoscape staff will review your report.
	After your report is verified, Cytoscape staff will fix it in the next release of Cytoscape.
	Thank you again for making better Cytoscape. 
	<?php
	
	$headers = "From: " . $from . "\r\n"; 

	// Send e-mail to staff now
	if (mail($to, $subject, $body, $headers)) {
  		//echo("<p>New bug report e-mail was sent to Cytostaff!</p>");
 	} else {
  		echo("<p>Failed to send a notification e-mail to cytostaff...</p>");
 	}	
}


function showForm($userInput) {
	?>
      <form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="submitbug" id="form1">
<label for="tfName">Name</label>
                    <input name="tfName" type="text" id="tfName" value="<?php if (isset($userInput['name'])) echo $userInput['name']; ?>" />
        <div>   

        <div>
          
          <label for="tfEmail">Email</label>
          <input name="tfEmail" type="text" id="tfEmail" value="<?php if (isset($userInput['email'])) echo $userInput['email']; ?>" />
          
          * Optional,
          If you want feedback 
          
        </div>

	<div>
	  <label for="cyversion">Cytoscape version</label>
	    <input name="tfCyversion" type="text" id="tfCyversion" value="<?php if (isset($userInput['cyversion'])) echo $userInput['cyversion']; ?>" />
	</div>

	<div>
	  <label for="os">Operating system</label>
	    <input name="tfOS" type="text" id="tfOS" value="<?php if (isset($userInput['os'])) echo $userInput['os']; ?>" />
	</div>

<!-- 
	<div>
	  <label for="os">Operating system</label>
	  <select name="cmbOS" id="os">
	    <option  <?php if ($userInput['os'] == 'Windows') echo "selected=\"selected\""; ?> >Windows</option>
	    <option <?php if ($userInput['os'] == 'Linux') echo "selected=\"selected\""; ?>>Linux</option>
	    <option <?php if ($userInput['os'] == 'Mac') echo "selected=\"selected\""; ?>>Mac</option>
	  </select>
	</div>
 -->
 	<div>
	  <label for="cysubject">Subject</label>
	    <input name="tfSubject" type="text" id="cysubject" value="<?php if (isset($userInput['cysubject'])) echo $userInput['cysubject']; ?>" />
	</div>

 
 
    <div>
            <label for="taDescription">Problem description</label>
        </div>
        <div>
            <textarea name="taDescription" id="taDescription"><?php if (isset($userInput['description'])) echo $userInput['description']; ?></textarea>
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
	
	// Get cyversion from URL
	if (isset ($_GET['cyversion'])) {
		$bugReport['cyversion'] = addslashes($_GET['cyversion']);
	}
	
	if (isset ($_POST['tfCyversion'])) {
		$bugReport['cyversion'] = addslashes($_POST['tfCyversion']);
	}

	if (isset ($_POST['tfSubject'])) {
		$bugReport['cysubject'] = addslashes($_POST['tfSubject']);
	}
	
	$bugReport['os'] = getOSFromUserAgent($_SERVER);
	
	if (isset ($_GET['os'])) {
		$bugReport['os'] = addslashes($_GET['os']);
	}
	
	if (isset ($_POST['os'])) {
		$bugReport['os'] = addslashes($_POST['os']);
	}
		
	if (isset ($_POST['taDescription'])) {
		$bugReport['description'] = addslashes($_POST['taDescription']);
	}	

	if (isset ($_FILES['attachments'])) {
		if ($_FILES['attachments']['name']!= NULL){ // a file is selected
			$bugReport['attachedFiles']['name'] = $_FILES['attachments']['name'];		
			$bugReport['attachedFiles']['type'] = $_FILES['attachments']['type'];

			$bugReport['attachedFiles']['md5'] = md5_file($_FILES['attachments']['tmp_name']);;		
			
			// Get file content
			$fileHandle = fopen($_FILES['attachments']['tmp_name'], "r");
			$fileContent = fread($fileHandle, $_FILES['attachments']['size']);
			$fileContent = addslashes($fileContent);
			
			$bugReport['attachedFiles']['fileContent'] = $fileContent;		
		}
	}
	
	if (isset ($_SERVER['REMOTE_ADDR'])) {
		$bugReport['ip_address'] = $_SERVER['REMOTE_ADDR'];
		$bugReport['remote_host'] = gethostbyaddr($bugReport['ip_address']);
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

?>



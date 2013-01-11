<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link href="http://cytoscape.org/css/main.css" type="text/css" rel="stylesheet" media="screen">
    <title>Send approval E-mail to developer</title>
    <script type="text/javascript" 
src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
    <script type="text/javascript" src="http://cytoscape.org/js/menu_generator.js"></script>
    
    </head>

<body bgcolor="#ffffff">
<script src="http://cytoscape.org/js/header.js"></script>

<h1>&nbsp;&nbsp;Send Approval E-mail to Developer</h1>


<?php
if (isset ($_GET['versionid'])) {
	$versionID = $_GET['versionid'];
}
if (isset ($_POST['versionID'])) { // hidden field
	$versionID = $_POST['versionID'];
}


// Include the DBMS credentials
include 'db.inc';

// Connect to the MySQL DBMS
if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
		showerror();
// Use the CyPluginDB database
if (!mysql_select_db($dbName, $connection))
	showerror();


// initialize the variables
$releaseNote = NULL;
$releaseNoteURL = NULL;
$fileUpload = NULL;
$jarURL = NULL;
$sourceURL = NULL;
$license_required_checked = NULL;
$reference = NULL;
$comment = NULL;
$license = NULL;
$license_required = NULL;
$contactName = NULL;
$contactEmail = NULL;
$themeOnly = NULL;

include 'getplugindatafromdb.inc';

$contactName = $db_contactName;
$contactEmail = $db_contactEmail;

$pluginName = $db_name;

$subject = "Your plugin ".$pluginName." has been accepted by Cytoscape web site";


$message = "";
if ($contactName == null || strlen($contactName)==0 ){
	$message = "Hi,\n\n";
}
else {
	$message = "Dear ".$contactName.",\n\n";
}

$message .= "We are pleased to inform you that your plugin ".$pluginName . " has been accepted by Cytoscape web site. It should be now ".
		"available through the Plugin Manager of Cytoscape. It will also be listed in the Cytoscape App store web site at http://apps.cytoscape.org . ".
		"Please note that the App store is hosted in a different server, so there will be a delay one day or more for your app to be available in App store\n\nThanks,\n\nCytostaff"

?>



<form method="post" action="sendApprovalEmail.php">

  <p>&nbsp;</p>
  <table width="300" border="0">
    <tr>
      <td width="10"><blockquote>
        <p>&nbsp;</p>
      </blockquote></td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td>Name: </td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><input type="text" name="contactName" size="60" value="<?php echo $contactName ?>" /></td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td>Email:</td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><input type="text" name="email" size="60" value="<?php echo $contactEmail ?>"/></td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td>Subject:</td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><input name="subject" type="text" id="subject" size="80" value ="<?php echo $subject ?>" /></td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td>Mail Message: </td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><textarea name="notes" rows="10" cols="80"><?php echo $message ?></textarea></td>
    </tr>
    <tr>
    <td>&nbsp;</td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><input name="submit" type="submit" value="Send Mail" /></td>
    </tr>
  </table>
  <p>&nbsp;</p>
</form>



<br />
<script src="http://cytoscape.org/js/footer.js"></script> 

</body>
</html>


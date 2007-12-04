<?php
/*
 * Created on Nov 29, 2007
 * Content within the form of user input
 */ 
if (isset ($_FILES['filePlugin'])) {
	$fileUpload = $_FILES['filePlugin'];
}

if (isset ($_POST['taReleaseNote'])) {
	$releaseNote = addslashes($_POST['taReleaseNote']);
}

if (isset ($_POST['tfReleaseNoteURL'])) {
	$releaseNoteURL = $_POST['tfReleaseNoteURL'];
}

if (isset ($_POST['tfSourceURL'])) {
	$sourceURL = $_POST['tfSourceURL'];
}

if (isset ($_POST['taReference'])) {
	$reference = addslashes($_POST['taReference']);
}

if (isset ($_POST['taLicense'])) {
	$license = addslashes($_POST['taLicense']);
}
if (isset ($_POST['chkLicense_required'])) {
	$license_required_checked = "checked";
	$license_required = "yes";
} else {
	$license_required = "no";
}
if (isset ($_POST['taComment'])) {
	$comment = addslashes($_POST['taComment']);
}

//Contact
if (isset ($_POST['tfContactName'])) {
	$contactName = addslashes($_POST['tfContactName']);
}
if (isset ($_POST['tfContactEmail'])) {
	$contactEmail = addslashes($_POST['tfContactEmail']);
}
 
?>

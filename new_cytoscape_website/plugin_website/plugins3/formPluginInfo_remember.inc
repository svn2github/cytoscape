<?php
/*
 * Created on Nov 29, 2007
 * Content within the form of user input
 */ 
if (isset ($_POST['tfName'])) {
	$name = $_POST['tfName'];
}

if (isset ($_POST['tfVersion'])) {
	$version = $_POST['tfVersion'];
}

if (isset ($_POST['taDescription'])) {
	//Enclding the string for SQL
	$description = addslashes($_POST['taDescription']);	
}

if (isset ($_POST['tfProjectURL'])) {
	$projectURL = $_POST['tfProjectURL'];
}

if (isset ($_POST['optCategory'])) {
	$category = $_POST['optCategory'];
}

if (isset ($_POST['tfMonth'])) {
	$month = $_POST['tfMonth'];
}

if (isset ($_POST['tfDay'])) {
	$day = $_POST['tfDay'];
}

if (isset ($_POST['tfYear'])) {
	$year = $_POST['tfYear'];
	$releaseDate = $year . '-' . $month . '-' . $day;
}

if (isset ($_POST['tfCyVersion'])) {
	$cyVersion = $_POST['tfCyVersion'];
}

//Authors
if (isset ($_POST['tfNames0'])) {
	$names[0] = addslashes($_POST['tfNames0']);
}
if (isset ($_POST['tfEmail0'])) {
	$emails[0] = $_POST['tfEmail0'];
}
if (isset ($_POST['tfAffiliation0'])) {
	$affiliations[0] = addslashes($_POST['tfAffiliation0']);
}
if (isset ($_POST['tfAffiliationURL0'])) {
	$affiliationURLs[0] = $_POST['tfAffiliationURL0'];
}

if (isset ($_POST['tfNames1'])) {
	$names[1] = addslashes($_POST['tfNames1']);
}
if (isset ($_POST['tfEmail1'])) {
	$emails[1] = $_POST['tfEmail1'];
}
if (isset ($_POST['tfAffiliation1'])) {
	$affiliations[1] = addslashes($_POST['tfAffiliation1']);
}
if (isset ($_POST['tfAffiliationURL1'])) {
	$affiliationURLs[1] = $_POST['tfAffiliationURL1'];
}
 
?>

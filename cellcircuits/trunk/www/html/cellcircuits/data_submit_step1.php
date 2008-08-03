<?php

$tried = NULL;
if (isset ($_POST['tried'])) {
	$tried = 'yes';
}

// initialize the variables
$data['pubMedID'] = NULL;

if (isset ($_POST['tfPubMedID']) && !empty($_POST['tfPubMedID'])) {
	$data['pubMedID'] = $_POST['tfPubMedID'];
}

//////////////////////// Form validation ////////////////////////
$validated = true;
if ($tried != NULL && $tried == 'yes') {
	$validated = vaildatePMID($data);
}
//////// End of form validation ////////////////////


/////////////////////////////////  Form definition //////////////////////////

if (!($tried && $validated)) {
?>
		<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		
		<title>Submit My Data to CellCircuits web site</title>
		
		<style type="text/css">
		<!--
		.style1 {color: #FF0000}
		-->
		</style>
		</head>
		
		<body>

		<p>&nbsp;</p>
		<h1 align="center"><strong>Submit My Data to CellCircuits web site</strong> </h1>
		<p>&nbsp;</p>
		
		<form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="form1" id="form1">
		<p>&nbsp;</p>
		<table width="691" border="0">
		  <tr>
			<th width="92" scope="col">&nbsp;</th>
			<td width="569" scope="col"><label></label></td>
			<th width="8" scope="col">&nbsp;</th>
		  </tr>
		  <tr>
			<th scope="row">PubMed_ID<span class="style1">*</span></th>
			<td><label>
			  <input name="tfPubMedID" type="text" id="tfPubMedID" value ="<?php if ($data['pubMedID'] != NULL) { echo $data['pubMedID'];} ?>" />
			</label></td>
			<td>&nbsp;</td>
		  </tr>
		  <tr>
		    <th scope="row">&nbsp;</th>
		    <td>&nbsp;</td>
		    <td>&nbsp;</td>
	      </tr>
		  <tr>
			<th scope="row">&nbsp;</th>
			<td><input type="submit" name="Submit" value="Continue" /><label></label>			 </td>
			<td>&nbsp;</td>
		  </tr>
		</table>
		<input name="tried" type="hidden" id="tried" value="yes">
		<p>&nbsp;</p>
		
</form>
        </body>
</html>

<?php
}
else {
	header('location:data_submit_step2.php?pmid='.$data['pubMedID']);
}

//========================
function vaildatePMID($data) {
	$msg = "";
	// PubMedID must be not NULL, an integer
	if ($data['pubMedID'] == NULL) {
		$msg .= "Error: PubMed_ID is a required field!<br>";
	}
	else if (!is_numeric (trim($data['pubMedID'])) ) {
		$msg .= "Error: PubMed_ID must be an integer!<br>";	
	}

	if ($msg != "") {
		echo $msg;
		return false;
	}
	return true;	
}
?>
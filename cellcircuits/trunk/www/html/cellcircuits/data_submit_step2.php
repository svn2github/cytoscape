<?php
include_once 'cc_utils.php';
include_once 'go_utils.php';

// Include the DBMS credentials
include_once 'db.php';
include_once 'ZipFileParser.php';

$mode = 'new'; // by default it is 'new'

if (isset($_GET['mode']) && $_GET['mode'] == 'edit') {
	$mode = 'edit';
}
if (isset($_POST['mode']) && $_POST['mode'] == 'edit') {
	$mode = 'edit';
}

if (!($mode == 'new'||$mode == 'edit')) {
	echo " unknow mode! mode should be new/edit";
	exit();
}

// Set the page title based on the mode, 'new' is the default
$pageTitle = 'Submit My Data to CellCircuits web site';
if ($mode == 'edit') {
	$pageTitle = 'Edit Data in CellCircuits DB';
} 

//
//$rawdata_id = NULL;
$publication = "None";

// Case for "new" mode
if (isset ($_GET['pmid'])) {
	$data['pmid'] = $_GET['pmid'];
	$data['pubmed_xml_record'] =  getpubmed_xml_record($data['pmid']);
}
if (isset ($_POST['pmid'])) {
	$data['pmid'] = $_POST['pmid'];
	$data['pubmed_xml_record'] =  getpubmed_xml_record($data['pmid']);
}

// Case for "edit" mode
if (isset ($_GET['rawdata_id'])) {
	//$rawdata_id = $_GET['rawdata_id'];
	$data['rawdata_id']= $_GET['rawdata_id'];
}
if (isset ($_POST['rawdata_id'])) { // hidden field
	//$rawdata_id = $_POST['rawdata_id'];
	$data['rawdata_id']= $_POST['rawdata_id'];
}

// Detect the action button clicked
$btnSubmit = NULL;
if (isset ($_POST['btnSubmit'])) {
	$btnSubmit = $_POST['btnSubmit'];
}	
if ($btnSubmit == 'Go Back') {
	header('location:data_submit_step1.php');
	exit();
}

$tried = NULL;
if (isset ($_POST['tried'])) {
	$tried = 'yes';
}

// initialize the variables
$data['dataFile'] = NULL;
$data['contact'] = NULL;
$data['email'] = NULL;
$data['comment'] = NULL;

if (isset ($_POST['tfPubMedID']) && !empty($_POST['tfPubMedID'])) {
	$data['pmid'] = $_POST['tfPubMedID'];
}

if (isset ($_FILES['dataFile'])&& !empty ($_FILES['dataFile']['name'])) {
	$data['dataFile'] = $_FILES['dataFile'];
}

if (isset ($_POST['taComment'])) {
	$data['comment'] = addslashes($_POST['taComment']);
}

//Contact
if (isset ($_POST['tfContact'])) {
	$data['contact'] = addslashes($_POST['tfContact']);
}
if (isset ($_POST['tfEmail']) && !empty($_POST['tfEmail'])) {
	$data['email'] = $_POST['tfEmail'];
}
			
// Case for 'edit', pull data out of DB for the given dataID
if ($mode == 'edit') {
	//$db_data = getDataFromDB($rawdata_id, $connection);		
	$db_data = getDataFromDB($data['rawdata_id'], $connection);		
}

//////////////////////// Form validation ////////////////////////
$validated = true;

if ($mode == 'new' && $tried != NULL && $tried == 'yes') {
	$validated = isValidUserInput($data, $connection);
}

//////// End of form validation ////////////////////


/////////////////////////////////  Form definition //////////////////////////

if (!($tried && $validated)) {

?>
		<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
		<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		
		<title><?php echo $pageTitle;?></title>
		
		<style type="text/css">
		<!--
		.style1 {color: #FF0000}
		-->
		</style>
		</head>
		
		<body>

		<p>&nbsp;</p>
		<h1 align="center"><strong> <?php echo $pageTitle;?></strong> </h1>
		<p>&nbsp;</p>
		
		<form action="<?php echo $_SERVER['PHP_SELF'] ?>" method="post" enctype="multipart/form-data" name="form1" id="form1">
		<p>&nbsp;</p>
		<table width="790" border="0">
		  <tr>
			<th width="105" scope="col">&nbsp;</th>
			<td width="644" scope="col"><label></label></td>
			<th width="27" scope="col">&nbsp;</th>
		  </tr>
		  <?php if ($mode == 'edit') {
			?>		  
		  <tr>
			<th scope="row">PubMed_ID</th>
			<td><label>
			  <input name="tfPubMedID" type="text" id="tfPubMedID" value ="<?php if ($db_data['pmid'] != NULL) { echo $db_data['pmid'];} ?>" />
			</label></td>
			<td>&nbsp;</td>
		  </tr>
		  <?php
		  }
		  ?>
		  <tr>
		    <th scope="row">Publication</th>
		    <td><?php if ($mode == 'new') { echo convert_xml2html($data['pubmed_xml_record'],'pubmedref_to_html_full.xsl');}  else { echo convert_xml2html($db_data['pubmed_xml_record'], 'pubmedref_to_html_full.xsl');}  ?>	</td>
		    <td>&nbsp;</td>
	      </tr>
		  <tr>
		    <th scope="row">&nbsp;</th>
		    <td>&nbsp;</td>
		    <td>&nbsp;</td>
	      </tr>
		  <tr>
			<th scope="row">Contact_person</th>
			<td><label>
			<?php
				$tmpStr = "";
				if ($mode == 'new' && $data['contact'] != NULL ) {
					$tmpStr = $data['contact'];
				}
				if ($mode == 'edit' && $db_data['contact_person'] != NULL ) { 
					$tmpStr = $db_data['contact_person'];
				}
			?>					
			<input name="tfContact" type="text" id="tfContact" size="60" value = "<?php echo $tmpStr; ?>" />			
			</label></td>
			<td>&nbsp;</td>
		  </tr>
		  <tr>
			<th scope="row">E_mail<span class="style1">*</span></th>
			<td><label>
			  <?php 
			  $tmpStr = "";
			  if ($mode == 'new' && $data['email'] != NULL) {
			  	$tmpStr = $data['email'];
			  }
			  if ($mode == 'edit' && $db_data['email'] != NULL) { // edit
			  	 $tmpStr = $db_data['email'];			  
			  }
			  ?>
              <input name="tfEmail" type="text" id="tfEmail" value ="<?php echo $tmpStr; ?>" size="40" />			
            (Not made public)</label></td>
			<td>&nbsp;</td>
		  </tr>
		<?php
		  if ($mode == 'edit') {
		  ?>
		  <tr>
		    <th scope="row">&nbsp;</th>
		    <td>If no file is provided, the file will not be updated </td>
		    <td>&nbsp;</td>
	      </tr>
		  <tr>
		  <?php
		  }
		  ?>
			<th scope="row">Data file<span class="style1">*</span></th>
			<td><input name="dataFile" type="file" id="dataFile" size="60" />
			<a href="zipFormatDefinition.html">Data format</a></td>
			<td>&nbsp;</td>
		  </tr>
		  <tr>
			<th scope="row">Comment</th>
			<td><label>
			  <?php 
			  $tmpStr = "";
			  if ($mode == 'new' && $data['comment'] != NULL) {
			  	$tmpStr = stripslashes($data['comment']); 
			  }
			  else if ( $mode == 'edit' && $db_data['comment'] != NULL) { // edit
			  	$tmpStr = stripslashes($db_data['comment']);			  
			  }
			  ?>
			  <textarea name="taComment" cols="69" rows="5" id="taComment" ><?php echo $tmpStr; ?></textarea>
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
			<td><p>Please verify the publication is correct! </p>
		    <p>&nbsp;</p></td>
			<td>&nbsp;</td>
		  </tr>
		  <tr>
			<th scope="row">&nbsp;</th>
			
			<td>
			<?php 
			
			if ($mode == 'new') {
				?>
					<input name="btnSubmit" type="submit" id="btnSubmit" value="Submit" />
				  &nbsp;&nbsp;	
					<input name="btnSubmit" type="submit" id="btnSubmit" value="Go Back" />
				<?php
			}
			else {	 // mode = 'edit'
			?>		
				  <input name="btnSubmit" type="submit" id="btnSubmit" value="update" />
				 <?php
			}
			?>			</td>
			<td>&nbsp;</td>
		  </tr>
		</table>
		<input name="tried" type="hidden" id="tried" value="yes">
		<input name="pmid" type="hidden" id="pmid" value="<?php echo $data['pmid']; ?>">
		<input name="mode" type="hidden" id="mode" value="<?php echo $mode; ?>">
		<input name="rawdata_id" type="hidden" id="rawdata_id" value="<?php echo $data['rawdata_id']; ?>">
		<p>&nbsp;</p>
		
		</form>
        <p>
		  <?php

} //  end of Form definition
else 
{   ///////////////// main logic ===form processing  ///////////////////////////
	//if ($btnSubmit == 'Submit') {
		//echo "clickedButton = 'Submit'";
	//}
		if ($mode == 'new') {
			insertData2DB($data, $connection);
			//Email notification
			
			?>
Thanks you for submitting your cell circuits data. Cell Circuits staff will review your data and publish it on the cell circuits web site.</p>
        <p>Go back to <a href="index.html">CellCircuits main page</a></p>
        <p>
          <?php
		 	// Send a confirmation e-mail to user, also cc to staff
		 	//sendConfirmationEmail($data);
		}
		else { // edit
			if (updateData2DB($db_data, $data, $connection))
			{
			?>
Data base is updated successfully.</p>
        <p>Go back to <a href="cc_admin.php">publicatin management</a></p>
        <p>
          <?php
			}
			else {
			?>
Failed to update the Database!</p>
        <p>Go back to <a href="cc_admin.php">publicatin management</a></p>
        <p>
          <?php
			
			}
		}

} // End of main logic


/////////////////////////////////////////////////////////////////////////////////////

function getpubmed_xml_record($pmid) {
	//Retrive XML record from PubMed
	$url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id=".$pmid."&retmode=xml&rettype=xml";
	$xml_doc = file_get_contents($url);
	return $xml_doc;	
}//

/*
function convert_xml_html($xml_doc) {
	if ($xml_doc == "\n<ERROR>Empty id list - nothing todo</ERROR>\n") {
		$publication = "No record was found in PubMed for pmid =".$pmid;;
		return $publication;
	}


	$publication = "	
<li>
	Solomon IC. Modulation of gasp frequency by activation of pre-Bötzinger complex in vivo. 
	<i>Journal of neurophysiology, </i>
	<b>
	87(3)</b>:1664-8, (2002). PubMed ID: 
	<a href=\"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=11877539\">11877539</a>
</li>";
	return $publication;
}
*/

function sendConfirmationEmail($data) {
	include 'staff_emails.php';

	$staff_emails = getStaffEmails();
	$from = $staff_emails[0];
	
	$to = $data['email'];// Author e-mail contact
	$bcc = "";
	for ($i=0; $i<count($staff_emails); $i++){
        	$bcc = $bcc . $staff_emails[$i] . " ";
	}
	$subject = "Your cell circuits data regarding pmid = " . $data['pmid'];
	$body = $data['contact'].",\n\nThank you for submitting your cellcircuits data. " .
        	"CellCircuits staff will review your data and publish it on the CellCircuits website." .
        	"\n\nCellCircuits team";

	$headers = "From: " . $from . "\r\n"; 
	if ($bcc != "") {
		$headers = $headers . "BCC: " . $bcc;
	}

	if (trim($to) == "") {
		// in case user did not provide an e-mail address, notify the staff
        	$to = $staff_emails[0];
        	$body = $body . "\n\nNote: User did not provide an e-mail address!";
	}

	if (mail($to, $subject, $body, $headers)) {
  		//echo("<p>Confirmation e-mail was sent!</p>");
 	} else {
  		echo("<p>Failed to send a confirmation e-mail...</p>");
 	}
} // sendCirmationEmail()


// Retrive a record from DB, given a rawdata_id
function getDataFromDB($rawdata_id, $connection) {
	$dbQuery = "select * from submission_data ";
	$dbQuery .= "where raw_data_auto_id = ".$rawdata_id;

	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) != 1) {
		return NULL;			
	}
	
	$_row = @ mysql_fetch_array($result);

	$record = NULL;
	$record['raw_data_auto_id'] = $_row["raw_data_auto_id"];
	$record['pmid'] = $_row["pmid"];
	$record['pubmed_xml_record'] = $_row["pubmed_xml_record"];
	$record['contact_person'] = $_row["contact_person"];
	$record['email'] = $_row["email"];
	$record['data_file_id'] = $_row["data_file_id"];
	$record['comment'] = stripslashes($_row["comment"]);
	$record['status'] = $_row["status"];
	$record['time_stamp'] = $_row["time_stamp"];
				
	return $record;
}


function updateData2DB($db_data, $data, $connection) {
	/*
	echo "<br>updateData2DB()<br>";
	print_r(array_keys($db_data));
	echo "<br>";
	print_r(array_keys($data));
	echo '<br>';
	
	echo "".$db_data['raw_data_auto_id'].'<br>';
	echo "".$db_data['pmid'].'<br>';
	echo "".$db_data['pubmed_xml_record'].'<br>';
	echo "".$db_data['contact_person'].'<br>';
	echo "".$db_data['email'].'<br>';
	echo "".$db_data['comment'].'<br>';
	echo "".$db_data['data_file_id'].'<br>';

	echo "".$data['rawdata_id'].'<br>';
	echo "".$data['pmid'].'<br>';
	//echo "".$data['pubmed_xml_record'].'<br>';
	echo "".$data['contact'].'<br>';
	echo "".$data['email'].'<br>';
	echo "".$data['comment'].'<br>';
	//echo "".$data['data_file_id'].'<br>';
	echo "dataFile_name = ".$data['dataFile']['name'].'<br>';
*/
	// If data file is provided, update it with the same data_file_id in DB
 	if ($data['dataFile']['name'] != "") {
		$file_name = $data['dataFile']['name'];
		$file_type = $data['dataFile']['type'];
		
		$data_fileHandle = fopen($data['dataFile']['tmp_name'], 'r');
		$dataContent = fread($data_fileHandle, $data['dataFile']['size']);
		$dataContent =  addslashes($dataContent);

		$dbQuery = "update raw_files set ";
		$dbQuery .= "file_name ='$file_name', file_type='$file_type', data='$dataContent' ";
		$dbQuery .= "where raw_file_auto_id=".$db_data['data_file_id'];

		//echo "<br>dbQuery = " . $dbQuery . "<br>";
		
		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();
	}

	// 
	$dbQuery_prefix = "update submission_data set ";
	$dbQuery_suffix = " where raw_data_auto_id =".$data['rawdata_id'];
	
	$dbQuery1 = "";
	if ($db_data['pmid'] != trim($data['pmid'])) {
		$tmpStr = trim($data['pmid']);
		$dbQuery1 .= "pmid='$tmpStr'," ;
		$pubmed_xml_record = addslashes(getpubmed_xml_record($data['pmid']));
		$dbQuery1 .= "pubmed_xml_record='$pubmed_xml_record'";
	}
	if ($db_data['contact_person'] != trim($data['contact'])) {
			if ($dbQuery1 != "") {
			$dbQuery1 .= ",";
		}
		$tmpStr = trim($data['contact']);
		$dbQuery1 .= "contact_person='$tmpStr' " ;
	}
	if ($db_data['email'] != trim($data['email'])) {
		if ($dbQuery1 != "") {
			$dbQuery1 .= ",";
		}
		$tmpStr = trim($data['email']);
		$dbQuery1 .= "email='$tmpStr'" ;
	}
	if ($db_data['comment'] != trim($data['comment'])) {
			if ($dbQuery1 != "") {
			$dbQuery1 .= ",";
		}
		$tmpStr = trim($data['comment']);
		$dbQuery1 .= "comment='$tmpStr' " ;
	}

	if ($dbQuery1 == "") {
		return true;
	}
	
	//$dbQuery1 .= ", time_stamp = '12345'";
	
	$dbQuery = $dbQuery_prefix.$dbQuery1.$dbQuery_suffix;
	//echo "<br>dbQuery = " . $dbQuery . "<br>";
		
	// Run the query
	if (!(@ mysql_query($dbQuery, $connection)))
		showerror();

	if (mysql_error()) {
		return false;	
	}

	// If pmid is updated, also update the table publications
	if ($db_data['pmid'] != trim($data['pmid'])) {
	
		$pub_record = getPublicationRecord($data['rawdata_id'], $connection);
		
		if ($pub_record != -1) { // -1 means "not found", do nothing
			// update the publication record
			$pubmed_xml_record = getpubmed_xml_record($data['pmid']);
			
			if ($pubmed_xml_record != "") {
				$pubmed_html_full =  addslashes(convert_xml2html($pubmed_xml_record, './pubmedref_to_html_full.xsl'));
				$pubmed_html_medium = addslashes(convert_xml2html($pubmed_xml_record, './pubmedref_to_html_medium.xsl'));
				$pubmed_html_short = addslashes(convert_xml2html($pubmed_xml_record, './pubmedref_to_html_short.xsl'));	
			}
			else {
				$pubmed_html_full =  "Not available";
				$pubmed_html_medium = "Not available";
				$pubmed_html_short = "Not available";		
			}
			
			$pmid =trim($data['pmid']);
			$pubmed_xml_record = addslashes($pubmed_xml_record);
			$dbQuery =  "update publications set pmid = $pmid, pubmed_xml_record  = '$pubmed_xml_record', ";
			$dbQuery .= "pubmed_html_full = '$pubmed_html_full', pubmed_html_medium = '$pubmed_html_medium', pubmed_html_short = '$pubmed_html_short' ";
			$dbQuery .= "where rawdata_id =".$data['rawdata_id'];
								
			// Run the query
			if (!($result = @ mysql_query($dbQuery, $connection))) {
				showerror();
				return false;
			}
		}
	}
		
	return true;
}//


function insertData2DB($data, $connection) {
	
		$data_file_auto_id = -1;

		if ($data['dataFile'] != NULL) {
			$dataFileName = $data['dataFile']['name'];
			$file_type = $data['dataFile']['type'];

			$data_fileHandle = fopen($data['dataFile']['tmp_name'], 'r');
			$dataContent = fread($data_fileHandle, $data['dataFile']['size']);
			
			//$fileSize = $data['dataFile']['size'];
			//echo "dataContent size = $fileSize<br>";

			$dataContent =  addslashes($dataContent);

			$dbQuery = "INSERT INTO raw_files VALUES ";
			$dbQuery .= "(0, '$dataFileName','$file_type', '$dataContent')";
			//echo "<br>dbQuery = " . $dbQuery . "<br>";
			// Run the query
			if (!(@ mysql_query($dbQuery, $connection)))
				showerror();

			$data_file_auto_id = mysql_insert_id($connection);
		}

		////
		$pmid = $data['pmid'];
		$pubmed_xml_record = addslashes($data['pubmed_xml_record']);
		$contact = $data['contact'];
		$email = $data['email'];
		$comment = addslashes($data['comment']);
		
		$dbQuery = 'insert into submission_data ( raw_data_auto_id, pmid, pubmed_xml_record, contact_person, email, data_file_id, comment) values ';
		$dbQuery .= "(0, '$pmid','$pubmed_xml_record','$contact', '$email','$data_file_auto_id', '$comment')";

		//echo "<br>dbQuery = " . $dbQuery . "<br>";
		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();
	
} // End of insertData2DB


// This function tests whether the email address is valid  
function isValidEmail($email){
      $pattern = "^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,3})$";
      if (eregi($pattern, $email)){
         return true;
      }
      else {
         return false;
      }   
}


function isValidUserInput($data, $connection){
	$msg = "";
			
	if ($data['email'] == NULL) {
		$msg .= "Error: E-mail is a required field!<br>";
	}
	else if (!isValidEmail($data['email'])) {
		$msg .= "Error: Invalid E-mail address!<br>";
	}
	
	if ($data['dataFile'] == NULL) {
		$msg .= "Error: Data file (in .zip format) is a required field!<br>";
	}
	else if (!isValidZipFile($data['dataFile']['tmp_name'], $connection)){
		// Make sure it is a zip file, non-empty, with correct contents, sif and image directory inside
		$msg .= "Invalid data file!<br>";
	}

	if ($msg != "") {
		echo $msg."<br>";
		return false;
	}
	return true;
} // End of function isValidUserInput()


function isValidZipFile($zipTmpFileName, $connection){
	$isValid = true;
	
	//echo "zipTmpFileName = $zipTmpFileName<br>";
	$zipFileParser = new ZipFileParser($zipTmpFileName);

	//
	$publication_url = $zipFileParser->getPublication_url();
	//echo "publication_url =",$publication_url,"<br>";

	if ($publication_url == "") {
		$isValid = false;
		echo "Error: publication_url is required<br>";

	}
	$sifFileArray = $zipFileParser->getSifFileArray();
	$sifFileCount = count($sifFileArray);
	if ($sifFileCount == 0) {
		$isValid = false;
		echo "Error: No sif file is found<br>";
	}
	//echo "Number of sif files = ",$sifFileCount,"<br>";
	
	$imgFileArray = $zipFileParser->getImgFileArray();
	$imgFileCount = count($imgFileArray);
	if ($imgFileCount == 0) {
		$isValid = false;
		echo "Error: No image file is found<br>";
	}
	//echo "Number of img files = ",$imgFileCount,"<br>";
		
	$thumFileArray = $zipFileParser->getThumImgFileArray();
	$thumFileCount = count($thumFileArray);
	if ($thumFileCount == 0) {
		$isValid = false;
		echo "Error: No thum file is found<br>";
	}
	//echo "Number of thum img files = ",$thumFileCount,"<br>";
	
	if ($sifFileCount != $imgFileCount  || $imgFileCount != $thumFileCount) {
		$isValid = false;
		echo "Error: Number of sif files must match number of image/thumImage files<br>";
	}
	
	// validate organisim
	$organismArray = $zipFileParser->getOrganismArray();

	if (count($organismArray) > 0) {
		$speciesStr = "";	
		
		for ($i=0; $i<count($organismArray); $i++) {
			$organism = $organismArray[$i];
			
			$species = 'unknown';	
			// eg. Convert the format 'Saccharomyces_cerevisiae_Homo_sapiens' into 'Saccharomyces cerevisiae,Homo sapiens'
			if ($organism != NULL) {
				$tmpArray = split('_',$organism);
				if (count($tmpArray)%2 == 0) {
					$species = "";
					$j=0;
					while (true) {
						$species .= $tmpArray[$j].' '.$tmpArray[$j+1];
						$j += 2;
						if ($j>= count($tmpArray)) {
							break;
						}
						$species .= ',';	
					}	
				}
			}
			
			$speciesStr .= strtolower($species);			
			if ($i != (count($organismArray) -1)) { // do not add "," for last item
				$speciesStr .= ",";
			}
		}
	
		$tmpArray = split(',',$speciesStr);
		for ($i=0; $i<count($tmpArray); $i++) {
			$distinctSpeciesArray[$tmpArray[$i]] = 0;
		}
	
		$distinctSpecies = array_keys($distinctSpeciesArray);
		
		//  get the species ID for each species,  if not find, print out warnig message
		for ($i=0; $i<count($distinctSpecies); $i++) {
			$tmpArray = split(' ',$distinctSpecies[$i]);
			$species_genus = $tmpArray[0];
			$species_species = $tmpArray[1];
			$speciesID = getSpeciesID($species_genus, $species_species, $connection);
			if ($speciesID == "") {
				$isValid = false;
				echo "<b>Error:</b> Can not find the species <b>",$distinctSpecies[$i],"</b> in GO Database! Most likely there is typo in the name.\n<br>";
			}
		}
	}

	if (!$isValid) {
		echo "Please look at <a href = \"http://www.cellcircuits.org/search/zipFormatDefinition.html\">this page</a> for data format definition<br>";
	}
	//return false;
	return $isValid;
}          

?>

</p>
</body>
</html>

<?php
// Get the list of sif file names for the given pub_id
// The file names will be the sif file name without file extension '.sif'
function getSifList($pub_id, $connection) {
	// Get the file names of the sif files
	$dbQuery  = "SELECT file_name from network_file_info, network_files ";
	$dbQuery .= "WHERE network_file_info.network_file_id = network_files.id AND network_file_info.publication_id = $pub_id";
	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		return false;
	}

	$network_file_basenames = null;
	while ($_row = @ mysql_fetch_array($result)) {
		$network_file_name = $_row["file_name"];
		// remove extension .sif
		//$tmpArray = split("\.", $network_file_name);		
		//$network_file_basenames[] = $tmpArray[0];
		$network_file_basenames[] = substr($network_file_name, 0, -4); 
	}
	return $network_file_basenames;
}


function convert_xml2html($xml_doc, $xsl_template_file) {
	if ($xml_doc == "\n<ERROR>Empty id list - nothing todo</ERROR>\n") {
		$publication = "No record was found in PubMed for pmid =".$pmid;;
		return $publication;
	}

	// This  is a work-around, the bes way is to install the xslt module for PHP
	// See URL for instruction ?????

	//1. write the xml_doc into a file "_tmpXML.txt"
	$fp = fopen("./_tmpXML.txt","w");
	fwrite($fp, $xml_doc);

	fclose($fp);

	//2. Exectute xsltproc command in a shell
	//$xsl_template_file = 'pubmedref_to_html.xsl'
	passthru("/usr/bin/xsltproc -o _tmpHTML.txt $xsl_template_file _tmpXML.txt");

	// 3. read the file "_tmpHTML.txt"
	$fp = fopen("./_tmpHTML.txt","r");
	$publication = "";
	while(true)
	{
		$line = fgets($fp);
		if($line == null)break;
		$publication .= $line;
		//echo $line;
	}
	fclose($fp);

	return $publication;
}


function getModelIDs($publication_id, $connection) {
	$dbQuery= "select id from model where pub=".$publication_id;

	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	//$record_count = @ mysql_num_rows($result);
	
	$modelIDs = NULL;
	while ($_row = @ mysql_fetch_array($result)) {
		//print_r(array_keys($_row));
		$modelIDs[] = $_row["id"];
	}

	return $modelIDs;
}


// Get model_id from model table, given pub (eq. pub_id) and name (model name, eq, base of sif file name) 
function getModelId($pub, $name, $connection) {
	//echo "Entering getModelId() ...\n";

	$dbQuery= "select id from model where pub='$pub' and name = '$name'";
	//echo "\ndbQuery =$dbQuery\n";
	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		return NULL;
	}
	
	return @ mysql_result($result, 0, "id");	
}


// Get file IDs from network_file_info table for the given publication_id
function getFileIDs($publication_id, $connection) {
	$dbQuery= "select network_file_id, image_file_id, thum_image_file_id from network_file_info where publication_id=".$publication_id;

	//echo "dbQuery=\n".$dbQuery."\n";
	// Run the query
	if (!($dataRecords = @ mysql_query($dbQuery, $connection)))
		showerror();

	$record_count = @ mysql_num_rows($dataRecords);
	
	//$fileIDs = NULL;
	$network_files = null;
	$image_files = null;
	$thum_image_files = null;
	while ($_row = @ mysql_fetch_array($dataRecords)) {
		//print_r(array_keys($_row));
		$network_files[] = $_row["network_file_id"];
		$image_files[] = $_row["image_file_id"];		
		$thum_image_files[] = $_row["thum_image_file_id"];	
		#echo "image_file_id = ", $_row["image_file_id"],"<br>";	
		#echo "thum_image_file_id = ", $_row["thum_image_file_id"],"<br>";	
	}

	$fileIDs['network_files'] = $network_files;
	$fileIDs['image_files'] = $image_files;
	$fileIDs['thum_image_files'] = $thum_image_files;

	return $fileIDs;
}


// Get publication record based on the given rawdata_id
function getPublicationRecord($rawdata_id, $connection) {

	// Get publicatin_id from table "publications"
	$dbQuery = "select publication_auto_id, pmid, cover_image_id, pdf_file_id, supplement_file_id from publications where rawdata_id=".$rawdata_id;

	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if( @ mysql_num_rows($result) == 0) {
		// Not found
		return -1;
	}

	$record['publication_auto_id'] = @ mysql_result($result, 0, "publication_auto_id");
	$record['pmid'] = @ mysql_result($result, 0, "pmid");
	$record['cover_image_id'] = @ mysql_result($result, 0, "cover_image_id");
	$record['pdf_file_id'] = @ mysql_result($result, 0, "pdf_file_id");
	$record['supplement_file_id'] = @ mysql_result($result, 0, "supplement_file_id");

	return $record;
}


//Update the status value in table submission_data
function updateStatus($rawdata_id, $connection, $newStatus) {
	$dbQuery = "UPDATE submission_data SET status ='$newStatus' ";
	$dbQuery .= "WHERE raw_data_auto_id=".$rawdata_id;
	
	// Run the query
	if (!(@ mysql_query($dbQuery, $connection)))
		showerror();	
		
	if (mysql_error($connection) != "") {
		return false;
	}
	return true;
}


// Retrive a file record, given tableName and file_id 
function getFileFromTable($table_name, $file_id, $connection) {
	$dbQuery = "select * from $table_name where ";
	if ($table_name == 'cover_image_files') {
		$dbQuery .= "cover_image_file_auto_id = '$file_id'";	
	}
	else if ($table_name == 'pdf_files') {
		$dbQuery .= "pdf_file_auto_id = '$file_id'";	
	}
	else { // table = network_files or network_image_files
		$dbQuery .= "id = '$file_id'";
	}

	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if( @ mysql_num_rows($result) == 0) {
		// Not found
		return NULL;
	}

	$record['file_name'] = @ mysql_result($result, 0, "file_name");
	$record['file_type'] = @ mysql_result($result, 0, "file_type");
	$record['data'] = @ mysql_result($result, 0, "data");

	return $record;	
}

?>

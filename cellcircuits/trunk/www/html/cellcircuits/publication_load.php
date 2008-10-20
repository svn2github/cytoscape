<?php
include_once 'db.php';
include_once 'cc_utils.php';
include_once 'go_utils.php';
include_once 'ZipFileParser.php';

// get rawdata_id
$debug = false;
if ($debug) {
	$rawdata_id = 3;
}
else {
	if (isset($_GET['rawdata_id'])) {
		$rawdata_id = $_GET['rawdata_id'];
	}
	else {
		echo "Unknown data set id";
		exit();
	}
}

if (alreadyLoaded($rawdata_id, $connection)) {
	?>
	This publicatin has already been loaded! Please remove it before load it again<br>
	<?php
	exit();
}

// Pull raw data from tables -- submission_data and raw_files
$rawdata = getRawData($rawdata_id, $connection);

$isDataValid = validateData($rawdata, $connection);
if (!$isDataValid) {
	?>
	Invalid data! Check the above message for detail.<br>
	<?php
	exit();
}

//echo "\n".$rawdata['raw_data_auto_id']."<br>";
//echo "\npmid=".$rawdata['pmid']."\n";

$success_step1 = db_load_step1($rawdata, $connection);
if (!$success_step1) {
	?>
<p>Failed to load step1<br>
  <?php
}

$success_step2 = db_load_step2($rawdata_id, $connection);
if (!$success_step2) {
	?>
  Failed to load step2 <br>
  <?php
}

// Update the status value for this data set
if ($success_step1&&$success_step2) {
	$newStatus = 'published';
}
else {
	$newStatus = 'load_fail';
}
$success_updateStatus = updateStatus($rawdata_id, $connection, $newStatus);
if (!$success_updateStatus) {
	?> 
  Failed to update status<br>
  <?php
}

if ($success_step1 && $success_step2 && $success_updateStatus) {
	?>
	<br>
	<br>
	Load success, Go back to the page --<a href="cc_admin.php"> publication management</a>
	<?php
}
else {
	?>
	<br>
	<br>
	Load failed! Please unload it and Correct the errors in Zip file and reload!
	<?php
}

/////End of load ///////////////////////////

function alreadyLoaded($rawdata_id, $connection) {

	$dbQuery = "select * from publications where rawdata_id =$rawdata_id";
	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if( @ mysql_num_rows($result) > 0) {
		// found
		return true;
	}

	return false;
}


// validate the species in the content of the zip file
function  validateData($rawdata, $connection) {
	$isDataValid = true;

	// 1. Put the zip stream in a tmp file
	$tmp_filename = "_tmpZipFile.zip";
	$fp = fopen($tmp_filename, 'w');
	if (fwrite($fp, $rawdata['data']) === FALSE) {
		echo "Cannot write to ".$tmp_filename;
		exit;
	}
	fclose($fp);
	
	// 2. Get distinct species
	$zipFileParser = new ZipFileParser($tmp_filename);

	$organismArray = $zipFileParser->getOrganismArray();
	
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
	
	// 3. get the species ID for each species,  if not find, print out warnig message
	for ($i=0; $i<count($distinctSpecies); $i++) {
		$tmpArray = split(' ',$distinctSpecies[$i]);
		$species_genus = $tmpArray[0];
		$species_species = $tmpArray[1];
		$speciesID = getSpeciesID($species_genus, $species_species, $connection);
		if ($speciesID == "") {
			$isDataValid = false;
			echo "<b>Error:</b> Can not find the species <b>",$distinctSpecies[$i],"</b> in GO Database! Most likely there is typo in the name.\n<br>";
		}
	}

	return $isDataValid;
}


///////////////////////////////////////////////////////////////////////////////
// The function db_load_step1() will extract all the contents of a zip file and
// Load all the contents in zip data into Database table
///////////////////////////////////////////////////////////////////////////////
function db_load_step1($rawdata, $connection) {

	$success = true;
	
	// Put the zip stream in a tmp file
	$tmp_filename = "_tmpZipFile.zip";
	
	$fp = fopen($tmp_filename, 'w');
		
	if (fwrite($fp, $rawdata['data']) === FALSE) {
		echo "Cannot write to ".$tmp_filename;
		exit;
	}
	
	fclose($fp);
		
	$zipFileParser = new ZipFileParser($tmp_filename);
	
	// Load cover image file, if any
	$cover_image_file_id = loadFile2DB('cover_image_files', $zipFileParser->getCoverImage(), $connection);

	// Check Error message	
	if (mysql_error($connection) != "") $success = false;

	// Load PDF file, if any
	$pdf_file_id = loadFile2DB('pdf_files', $zipFileParser->getPDF(), $connection);

	// Check Error message	
	if (mysql_error($connection) != "") $success = false;
	
	// Load Supplement material file, if any
	$supplement_material_file_id = -1;
	if ($zipFileParser->getSupplement_material_file() != NULL) {
		$supplement_material_file_id = loadFile2DB('supplement_material_files', $zipFileParser->getSupplement_material_file(), $connection);
	}
	// Check Error message	
	if (mysql_error($connection) != "") $success = false;
	
	$publication_url = $zipFileParser->getPublication_url();	
	$supplement_url = $zipFileParser->getSupplement_url();
	
	// Load the table -- publications
	$publication_auto_id =loadPublications($rawdata,$cover_image_file_id,$pdf_file_id,$publication_url,$supplement_material_file_id,$supplement_url, $connection);

	// Check Error message	
	if (mysql_error($connection) != "") $success = false;
	
	// Loop through to load all the network files and network image files
	$organismArray = $zipFileParser->getOrganismArray();
	$sifFileArray = $zipFileParser->getSifFileArray();
	$imgFileArray = $zipFileParser->getImgFileArray();
	$thmImgFileArray = $zipFileParser->getThumImgFileArray();
	
	for ($i=0; $i<count($sifFileArray); $i++) {
		// Load the table -- network_files
		$sifFile = $sifFileArray[$i];
		$sif_file_id = loadFile2DB('network_files', $sifFile, $connection);

		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	
		// Load the table -- network_image_files
		$imgFile = $imgFileArray[$i];
		$img_file_id = loadFile2DB('network_image_files', $imgFile, $connection);

		// Load the table -- network_thum_image_files
		$thmImgFile = $thmImgFileArray[$i];
		$thmImg_file_id = loadFile2DB('network_thum_image_files', $thmImgFile, $connection);
		
		
		
		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	
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
		// Use low case for species
		$species = strtolower($species);
		// Load the table -- network_file_info
		$dbQuery  = "INSERT INTO network_file_info ";
		$dbQuery .= "VALUES (0, '$publication_auto_id', '$species','sif','$sif_file_id', '$img_file_id','$thmImg_file_id')";
	
		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();

		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	}	
	
	// Load the table -- legends	
	$legendFileArray = $zipFileParser->getLegendFileArray();
	for ($i=0; $i<count($legendFileArray); $i++) {
		$legendFile = $legendFileArray[$i];
		
		if (!loadLegendFile2DB($publication_auto_id, $legendFile, $connection)) {
			$success = false;
		}
	}
	
	return $success;
}

/*
function loadThmImage2DB($imgFile, $img_file_id, $connection) {
	// write the image into a tmp file
	$fileName = $imgFile['fileName'];
	$tmpfname = tempnam("/tmp", $fileName);

	$handle = fopen($tmpfname, "w");
	fwrite($handle, $imgFile['content']);
	fclose($handle);
	
	// Rescale the image
	passthru("convert -resize 100x100 $fileName smallimage");
	
	unlink($tmpfname);

	// Load the thm image into DB
}
*/

function loadLegendFile2DB($publication_id, $legendFile, $connection) {
		$fileName = $legendFile['fileName'];
		$fileType = $legendFile['fileType'];
		$data = addslashes($legendFile['content']);
				
		$dbQuery  = "INSERT INTO legend_files ";
		$dbQuery .= "VALUES (0, '$publication_id', '$fileName', '$fileType', '$data')";
	
		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();

		// Check Error message	
		if (mysql_error($connection) != "") return false;
		return true;
}


function loadPublications($rawdata,$cover_image_file_id,$pdf_file_id, $publication_url, $supplement_material_file_id,$supplement_url, $connection) {
	
	$rawdata_id = $rawdata['raw_data_auto_id'];
	$pmid = $rawdata['pmid'];
	$pubmed_xml_record = addslashes($rawdata['pubmed_xml_record']);
	
	// we should use different XSLT templates for XML2HTML convertion
	if ($pubmed_xml_record != "") {
		$pubmed_html_full =  addslashes(convert_xml2html($rawdata['pubmed_xml_record'], './pubmedref_to_html_full.xsl'));
		$pubmed_html_medium = addslashes(convert_xml2html($rawdata['pubmed_xml_record'], './pubmedref_to_html_medium.xsl'));
		$pubmed_html_short = addslashes(convert_xml2html($rawdata['pubmed_xml_record'], './pubmedref_to_html_short.xsl'));
		$pubmed_html_advsearch = addslashes(convert_xml2html($rawdata['pubmed_xml_record'], './pubmedref_to_html_advanced_search_full.xsl'));	
	}
	else {
		$pubmed_html_full =  "Not available";
		$pubmed_html_medium = "Not available";
		$pubmed_html_short = "Not available";
		$pubmed_html_advsearch  = "Not available";		
	}
	
	//echo "<br>--<br>".$pubmed_html_full."<br>--<br>";
	
	$dbQuery  = "INSERT INTO publications (publication_auto_id, rawdata_id, pmid, pubmed_xml_record, pubmed_html_full, 
	pubmed_html_medium, pubmed_html_short,pubmed_html_advsearch, pub_url,supplement_file_id, supplement_url,cover_image_id, pdf_file_id) ";
	$dbQuery .= "VALUES (0, '$rawdata_id','$pmid', '$pubmed_xml_record', '$pubmed_html_full','$pubmed_html_medium','$pubmed_html_short','$pubmed_html_advsearch','$publication_url','$supplement_material_file_id', '$supplement_url','$cover_image_file_id', '$pdf_file_id')";
	
	// Run the query
	if (!(@ mysql_query($dbQuery, $connection)))
		showerror();

	$publication_auto_id = mysql_insert_id($connection);
	return $publication_auto_id;
}


function loadFile2DB($db_table, $dataArray, $connection) {
	if ($dataArray == NULL || count($dataArray['content']) == 0) return -1;
	
	$fileName = $dataArray['fileName'];
	$fileType = $dataArray['fileType'];
	$fileContent = addslashes($dataArray['content']);

	$dbQuery = "INSERT INTO $db_table ";
	$dbQuery .= "VALUES (0, '$fileName','$fileType', '$fileContent')";

	// Run the query
	if (!($dataRecord = @ mysql_query($dbQuery, $connection)))
		showerror();
	
	//return the file_auto_id
	return mysql_insert_id($connection);
}


function getRawData($rawdata_id, $connection) {
	$dbQuery =  "select * from submission_data, raw_files ";
	$dbQuery .= "where raw_data_auto_id=$rawdata_id and submission_data.data_file_id = raw_files.raw_file_auto_id";
	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		return NULL;
	}
	
	$record['fileName'] = @ mysql_result($result, 0, "file_name");
	$record['fileType'] = @ mysql_result($result, 0, "file_type");
	$record['data'] = @ mysql_result($result, 0, "data");
	$record['raw_data_auto_id'] = @ mysql_result($result, 0, "raw_data_auto_id");
	$record['pmid'] = @ mysql_result($result, 0, "pmid");
	$record['pubmed_xml_record'] = @ mysql_result($result, 0, "pubmed_xml_record");
	
	return $record;
}

////////////////////////////////////////////////////
// This function db_load_step2() will populate four tables -- model, gene_model, model_similarity and enrichment
// based on the data in tables populated at previous step.
//////////////////////////////////////////////////////

function db_load_step2($rawdata_id, $connection) {
	//echo "Entering db_load_step2()...\n";
	$success = true;
	
	// Get publication ID and pmid
	$publicationRecord = getPublicationRecord($rawdata_id, $connection);
	$pub_id = $publicationRecord['publication_auto_id'];
	
	if (!populateModelTable($pub_id, $connection)) {
		$success = false;
	}

	if (!populateGeneModelTable($pub_id, $connection))
	{
		$success = false;
	}
	
	// load table model_similarity
	$cmd = "perl ./load_model_similarity_table.pl $pub_id";	
	
	//echo "$cmd<br>";
	
	passthru($cmd);
	
	// load table enrichment
	$cmd = "./load_enrichment_table.sh $pub_id > ./load_enrichment_table_output.txt &";
	
	system($cmd); 
	
	if ($success){
//	if (0){
		?>
</p>
All the tables except "enrichment" were loaded!
</p>
<p>The job to load enrichment table has been submitted, it will be done in 0.5~2 hours. You will get e-mail notification!
</p>
<p>
  <?php
	}
	
	
	return $success;
}

function getRefinedGeneSet($geneArray) {

	for ($i =0; $i < count($geneArray);$i++) {
		//echo "geneArray[$i] = $geneArray[$i]\n";
		$gene_symbols = split('\|',$geneArray[$i]);
		for ($j=0; $j<count($gene_symbols); $j++) {
				$refined_gene_set[$gene_symbols[$j]] = "";
		}
	}
	
	$refined_set = array_keys($refined_gene_set);
	
	//for ($i=0; $i<count($refined_set); $i++) {
	//	echo "refined_set[$i] = $refined_set[$i]\n";
	//}
	
	return $refined_set;
}


function populateGeneModelTable($pub_id, $connection) {
	//echo "Entering populateGeneModelTable() ...\n";
	
	$success = true;

	// get the list of sif file names (without extesion .sif) for this publication
	$network_file_basenames = getSifList($pub_id, $connection);

	// for each model (sif file), do the following
	for ($i=0; $i<count($network_file_basenames); $i++) {

		//The name here actually is the model name
		$name = $network_file_basenames[$i];
		echo "populateGeneModelTable(): <br>model name = $name<br>";

		// 1. Get the list of genes for this sif file	
		$geneSet_from_one_sif_file = getGenesFromSif($pub_id, $name, $connection);
				
		// remove duplicated genes, caused by gene symbol such as "gene_symbol1|gene_symbol2"
		$geneArray = getRefinedGeneSet(array_keys($geneSet_from_one_sif_file));
			
		$geneCount = count($geneArray);
		echo "There are $geneCount genes in the model\n<br>";	
		//foreach ($geneArray as $a_gene) {
		//	echo "geneArray: gene= $a_gene<br>";
		//}
		
		// 2. Get the species related to this sif file
		$speciesStr = getModelSpecies($pub_id, $name,$connection);
		//echo "speciesStr = $speciesStr\n";
		
		// 3. Determine the gene list, which are missing from GO Database
		//echo "Determine the gene list, which are missing from GO Database\n";
		$speciesArray = split(",",$speciesStr);
				
		// A gene may be from more than one species, check, if each gene exists in GO DB, if not add it to GO
		for ($k=0; $k<count($speciesArray);$k++) {
			$theSpecies = trim($speciesArray[$k]);
			//echo "theSpecies = ".$theSpecies."\n";
			
			$tmpArray2 = split(" ", $theSpecies);
			$species_genus = $tmpArray2[0];
			$species_species = $tmpArray2[1];
			
			$missingGenes = getMissingGenes($geneArray,$theSpecies, $connection);
		
			// Add missing genes to GO database
			if (count($missingGenes) >0) {
				echo "<br>Model $name: Number of missing genes = ".count($missingGenes)."<br>\n";
				echo "Add missing genes to GO<br>\n";			
			}
			
			for ($m=0; $m<count($missingGenes); $m++) {
				addMissingGene2GO($species_genus, $species_species, $missingGenes[$m], $connection);			
			}

			// There should be no missing genes now for this model (i.e. this sif file) and this species
			// Get gene_product_id of GO for all genes for this model (i.e. from this sif file)
			$gene_product_ids = NULL; 
						
			for ($n=0; $n<count($geneArray); $n++) {
				$gene_product_ids[] = getGeneProductID($geneArray[$n], $species_species, $species_genus, $connection);
				//echo "geneArray[$n] =".$geneArray[$n]." ----".$gene_product_ids[$n]."\n";
			}
			
			//echo "Now begin to populate the gene_model table for $theSpecies<br>\n";			
				
			// populate the gene_model table
			$model_id = getModelId($pub_id, $name, $connection);
			for ($p =0; $p<count($gene_product_ids); $p++) {
				$gene_product_id = $gene_product_ids[$p];
				if (trim($gene_product_id) == "") {
					echo "ERROR: Gene --- $genes[$p] --- Gene_product_ID is empty, it is not added to gene_model table<br>\n";
					continue;
				}
				$dbQuery =  "INSERT INTO gene_model (model_id, gene_product_id) ";
				$dbQuery .= "VALUES ($model_id, $gene_product_id) ";
			
				//echo "dbQuery = $dbQuery\n";
			
				// Run the query
				if (!($result = @ mysql_query($dbQuery, $connection)))
					showerror();					
			} // end of populate the model table
			//echo "End of populating the model table for $theSpecies<br>\n";			
		} // end of for each species
	} // End of out for-loop -- each model

	return $success;
}


////////////////////////
function populateModelTable($pub_id, $connection) {
	//echo "Entering populateModelTable() ...\n";

	$network_file_basenames = getSifList($pub_id, $connection);
	
	// Populate the model table
	for ($i=0; $i<count($network_file_basenames); $i++) {

		$name = $network_file_basenames[$i];
		//echo "name = $name\n";
		$dbQuery  = "INSERT INTO model (pub, name) ";
		$dbQuery .= "VALUES ('$pub_id','$name')";
	
		// Run the query
		if (!(@ mysql_query($dbQuery, $connection)))
			showerror();
	}

	// Check Error message	
	if (mysql_error($connection) != "") return false;
		return true;
} // end of function populateModelTable()


?>
</p>

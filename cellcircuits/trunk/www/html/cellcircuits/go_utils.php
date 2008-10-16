<?php
function addMissingGene2GO($species_genus, $species_species,$missingGene, $connection) {
	echo "go_utils.addMissingGene2GO() ... -$species_genus - $species_species - $missingGene ...<br>\n";

	// Note: missing gene already have a _HUMAN if species is 'homo sapiens'

	$species_id = getSpeciesID($species_genus, $species_species, $connection);
	//echo "species_id = $species_id\n";
	
	// Add an entry to the dbxref for the new gene
	$xref_key = $species_genus."_".$species_species."_".$missingGene;
	$dbQuery = "INSERT INTO dbxref (xref_dbname, xref_key) ";
	$dbQuery .= "VALUES ('cellcircuits_ucsd','$xref_key')"; // 'cellcircuits_ucsd' is the pretended DB name
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();
	$dbxref_id = mysql_insert_id($connection);
	//$dbxref_id = 3970881;
	//echo "dbxref_id = $dbxref_id\n";
	
	// Add an entry to gene_product table
	$dbQuery = "INSERT INTO gene_product (symbol,dbxref_id,species_id) ";
	$dbQuery .= "VALUES ('$missingGene',$dbxref_id,$species_id)";
		
	//echo "dbQuery  = \n$dbQuery \n";
		
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	$gene_product_auto_id = mysql_insert_id($connection);
	//$gene_product_auto_id = 3790639;
	//echo "gene_product_id = $gene_product_auto_id\n";
	
	// Get term ids for the terms -- 'biological process unknown','molecular function unknown','cellular component unknown'
	$termIDs_of_unknown = getTermIDs_unknown($connection);

	//insert_into_association
	for ($i=0; $i<count($termIDs_of_unknown); $i++) {
		$termID = $termIDs_of_unknown[$i];
		$dbQuery = "INSERT INTO association (term_id, gene_product_id) ";
		$dbQuery .= "VALUES ($termID,$gene_product_auto_id)";
		// Run the query
		if (!($result = @ mysql_query($dbQuery, $connection)))
			showerror();
	}

}


// Add three new terms to the term table of GO, if there are not there yet
// Because no all genes can be found in GO database, but they still need annonation
// And their terms will be "unknown"
function addUnknownTerm2Go($connection) {
	// Note: execute the following three queries after the fresh update of GO DB
	$dbQuery1= "insert term (name, term_type, acc) values ('biological process unknown','biological_process','GO:0000004')";
	$dbQuery2= "insert term (name, term_type, acc) values ('molecular function unknown','molecular_function','GO:0005554')";
	$dbQuery3= "insert term (name, term_type, acc) values ('cellular component unknown','cellular_component','GO:0008372')";
}


// Get term ids for the terms -- 'biological process unknown','molecular function unknown','cellular component unknown'
function getTermIDs_unknown($connection) {	
	$names = array('biological process unknown','molecular function unknown','cellular component unknown');
	$ids = NULL;
					  
	for ($i=0; $i<count($names); $i++) {
		$name = $names[$i];
		$dbQuery = "SELECT id from term WHERE name = '$name'";
		// Run the query
		if (!($result = @ mysql_query($dbQuery, $connection)))
			showerror();	
			
		if( @ mysql_num_rows($result) == 0) {
			$ids[] = NULL;continue;
		}
		else {
			$ids[] = @ mysql_result($result, 0, "id");
		}
	}
	return $ids;
}


/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
function getSpeciesID($species_genus, $species_species, $connection) {
	$dbQuery =  "SELECT id FROM species ";
	$dbQuery .= "WHERE species.genus = '$species_genus' AND species.species = '$species_species'";

	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	$species_id = @ mysql_result($result, 0, "id");

	return $species_id;
}


// Given publication and a sif file name (without extesion .sif), get the set of genes from sif file
function getGenesFromSif($pub_id, $name, $connection) {
	$dbQuery  = "SELECT data from network_file_info, network_files ";
	$dbQuery .= "WHERE network_file_info.network_file_id = network_files.id AND network_file_info.publication_id = $pub_id ";
	$dbQuery .= "and network_files.file_name = '$name.sif'";
	//echo "\n".$dbQuery."\n\n";
	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		return NULL;
	}

	$sif_data = @ mysql_result($result, 0, "data");

	$lines = split("\n", $sif_data);

	$geneSet['placeholder'] = 'placeholder';
	
	for ($i=0; $i<count($lines); $i++) {
		$line = trim($lines[$i]);
		//echo $line."\n";
		if ($line == "") {
			continue;
		}
		//$tmpArray = split("\t ", $line);
		$tmpArray = explode("\t", $line);
		
		if (count($tmpArray) == 1) {
			$gene1 = trim($tmpArray[0]);
			if (!in_array($gene1, $geneSet)) {
				$geneSet[$gene1] = 'A';
			}		
		}
		if (count($tmpArray) == 3) {
			$gene1 = trim($tmpArray[0]);
			$gene2 = trim($tmpArray[2]);
			
			if (!array_key_exists($gene1, $geneSet)) {
				$geneSet[$gene1] = 'A';
			}
			if (!array_key_exists($gene2, $geneSet)) {
				$geneSet[$gene2] = 'A';
			}
			
		}
	} // end of for loop

	// remove the placeholder
	array_splice($geneSet, 0,1);
	
	return $geneSet;
}


function getMissingGenes($geneArray,$theSpecies, $connection) {
	//echo "Entering getMissingGenes() ...\n";
	$missingGenes = NULL;
	
	$tmpArray = split(" ", $theSpecies);
	$species_genus = $tmpArray[0];
	$species_species = $tmpArray[1];

	$isHomoSapiens = false;
	if (strcasecmp($theSpecies, 'homo sapiens') == 0) {
		$isHomoSapiens = true;
	}
	
	//$array_of_keys = array_keys($geneSet);
	
	//$gene_symbols = NULL;
	//for ($i=0; $i < count($geneArray); $i++) {
	//	$tmpArray = split("\|", $geneArray[$i]);
	//	for ($j =0; $j < count($tmpArray); $j++) {
	//		$gene_symbols[] = $tmpArray[$j];
	//	}
	//}
		
	//for ($i=0; $i<count($geneArray);$i++) {
	//	echo "geneArray[$i] = $geneArray[$i]\n";
	//}
	
	for ($j=0; $j< count($geneArray); $j++) {
		$gene_symbol = $geneArray[$j];
		if ($isHomoSapiens) {
			// The _HUMAN suffix is a GO cinvention
			$gene_symbol .= '_HUMAN';
		}
		
		$gene_product_id = getGeneProductID($gene_symbol, $species_species, $species_genus, $connection);

		if (trim($gene_product_id) == "") {
			//echo "Found missing gene\n";
			$missingGenes[] = $gene_symbol;
		}
	}
	//echo "\n";
	//for ($i=0; $i<count($missingGenes);$i++) {
	//	echo "missingGenes[$i] = $missingGenes[$i]\n";
	//}


	return $missingGenes;
}//


function string_ends_with($string, $ending){
    $len = strlen($ending);    
	$string_end = substr($string, strlen($string) - $len);     
	return $string_end == $ending;
}

// Get the gene_product_id from GO DB
function getGeneProductID($gene_symbol, $species_species, $species_genus, $connection) {

	// gene_symbol might be in the format like "PFL2345C" or "PF14_0294|YPL140"
	$gene_symbols = split("\|", $gene_symbol);

	if ($species_species == 'sapiens' && $species_genus == 'homo') {
		for ($i=0; $i < count($gene_symbols); $i++) {
			if (!string_ends_with($gene_symbols[$i], '_HUMAN')) {
				$gene_symbols[$i] .= '_HUMAN';		
			}
		}
	}

	$gene_symbols_in = "(";
	for ($i=0; $i < count($gene_symbols); $i++) {
		$gene_symbols_in .= "'$gene_symbols[$i]'";
		if ($i != (count($gene_symbols) -1)) {
			$gene_symbols_in .= ",";
		}
	}
	$gene_symbols_in .= ")";

	//echo "gene_symbols_in = $gene_symbols_in\n";

	$dbQuery =  "SELECT gene_product.id as gene_product_id ";
	$dbQuery .= "FROM gene_product, species ";
	$dbQuery .= "WHERE gene_product.species_id = species.id AND ";
	$dbQuery .= "species.genus = '$species_genus' AND species.species = '$species_species' AND gene_product.symbol in $gene_symbols_in";
	//echo "\n<br>dbQuery = \n$dbQuery<br>\n\n";
		
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();
		
	$gene_product_id = @ mysql_result($result, 0, "gene_product_id");

	//echo "gene_product_id = $gene_product_id\n";
	
	return $gene_product_id;
}



// Given pub_id, and model name (sif file without .sif extension), 
// retrive the species associated with this model, 
// the return value look like 'Saccharomyces cerevisiae,Homo sapiens'
function getModelSpecies($pub_id, $name,$connection) {
	$dbQuery = "SELECT species FROM network_file_info, network_files ";
	$dbQuery .= "WHERE network_file_info.network_file_id = network_files.id AND network_file_info.publication_id = $pub_id ";
	$dbQuery .= "and network_files.file_name = '$name.sif'";
		
	//echo "\n$dbQuery\n";	
		
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		return NULL;
	}

	$speciesStr = @ mysql_result($result, 0, "species");

	return $speciesStr;
}

/*
//
function getLastDbxrefIdx($connection) {
	$dbQuery =  "SELECT max(id) as max FROM dbxref";
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	$last_dbxref_idx = @ mysql_result($result, 0, "max");

	return 	$last_dbxref_idx;
}
*/




//////////////////////////////////////////////

//$publication_id =10;

//$networkFileList = getNetworkFileList($publication_id, $connection);


function getNetworkFileList($publication_id, $connection) {
	$fileIDs = getFileIDs($publication_id, $connection);


	$ids = "";	
	if (count($fileIDs['network_files']) >0) {
		$ids .= $fileIDs['network_files'][0];
	}
	for ($i=1; $i<count($fileIDs['network_files']); $i++) {
		$ids .=','.$fileIDs['network_files'][$i];
	}

	echo "network_files_IDs = ".$ids;

	$record = getFileFromTable('network_files', $fileIDs['network_files'][0], $connection);
	echo "file_name = ".$record['file_name']."\n";
	echo "file_type = ".$record['file_type']."\n";
	echo "file_data = \n".$record['data']."\n";

}


?>


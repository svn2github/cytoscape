<?php
include_once 'db.php';
include_once 'cc_utils.php';

/////////////////////////////////

function unload_one_publication($rawdata_id, $connection) {
	$success = true;
	
	$publicationRecord = getPublicationRecord($rawdata_id, $connection);
	
	//echo "unload_one_publication(): publicationRecord['publication_auto_id'] = ".$publicationRecord['publication_auto_id']."\n";
	
	if ($publicationRecord['publication_auto_id'] != NULL && $publicationRecord['publication_auto_id'] != "") {
		$success = unload_derivedData($publicationRecord, $connection);
	}
	
	// Set status = "unpublished" in submission_data table
	if ($success) {
		$newStatus = 'unpublished';
	}
	else {
		$newStatus = 'unload_fail';
	}
	$success_updateStatus = updateStatus($rawdata_id, $connection, $newStatus);
	if (!$success_updateStatus) {
		?>Failed to update status<?php
	}

	return $success && $success_updateStatus;	
}


function unload_derivedData($publicationRecord, $connection) {
	// Delete all calculated data from four tables
	// Tabless --- model
	//			gene_model
	//			model_similarity
	//			enrichment
	$success_deleteModelData = deleteModelData($publicationRecord, $connection);
	if (!$success_deleteModelData) {
		?>Failed to delete model data<?php
	}

	// Delete all data extracted from the zip data
	// Tables --- cover_image_files
	//			pdf_files
	//			network_file_info
	//			network_file
	//			network_image_files
	//			network_thum_image_files
	//			supplement_material_files
	//			legends
	//			publications
	$success_deleteExtractedData = deleteExtractedData($publicationRecord, $connection);
	if (!$success_deleteExtractedData) {
		?>Failed to delete extract data<?php
	}
			
	return $success_deleteExtractedData && $success_deleteModelData;
}

////////////////////////////////////////
////////////////////////////////////////
function deleteModelData($publicationRecord, $connection) {
	$success = true;
	$modelIDs = getModelIDs($publicationRecord['publication_auto_id'], $connection);

	// 1. Delete from table model
	$dbQuery = "delete from model where pub=".$publicationRecord['publication_auto_id'];
	// Run the query
	if (!@ mysql_query($dbQuery, $connection))
		showerror();

	// Check Error message	
	if (mysql_error($connection) != "") $success = false;
		
	// 2. Delete from table gene_model	
	for ($i=0; $i<count($modelIDs); $i++) {
		$modelID = $modelIDs[$i];
		$dbQuery = "delete from gene_model where model_id=".$modelIDs[$i];

		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
	}
	// Check Error message	
	if (mysql_error($connection) != "") $success = false;

	// 3. Delete from table similarity
	for ($i=0; $i<count($modelIDs); $i++) {
		$modelID = $modelIDs[$i];
		$dbQuery = "delete from model_similarity where model_id_a=$modelID OR model_id_b=$modelID";
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
	}
	// Check Error message	
	if (mysql_error($connection) != "") $success = false;

	// 4. Delete from table enrichment
	for ($i=0; $i<count($modelIDs); $i++) {
		$modelID = $modelIDs[$i];
		$dbQuery = "delete from enrichment where model_id=$modelID";
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
	}
	// Check Error message	
	if (mysql_error($connection) != "") $success = false;


	return $success;
} // End of deleteModelData



////////////////////////////////

function deleteExtractedData($publicationRecord, $connection) {
	$success = true;

	// 1. Delete cover_image_file
	if ($publicationRecord['cover_image_id'] != -1) {
		$dbQuery = "delete from cover_image_files where cover_image_file_auto_id=".$publicationRecord['cover_image_id'];
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
	}
	// Check Error message	
	if (mysql_error($connection) != "") $success = false;

	// 2. Delete pdf_file
	if ($publicationRecord['pdf_file_id'] != -1) {
		$dbQuery = "delete from pdf_files where pdf_file_auto_id=".$publicationRecord['pdf_file_id'];
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	}

	// 3. Delete network_files and network_image_files for this publication
	
	// 3.0 Get the list of network files and list of network image files from network_file_info table
	$fileIDs = getFileIDs($publicationRecord['publication_auto_id'], $connection);

	// 3.1 Delete network files for this publication
	$ids = "";	
	if (count($fileIDs['network_files']) >0) {
		$ids .= $fileIDs['network_files'][0];
	}
	for ($i=1; $i<count($fileIDs['network_files']); $i++) {
		$ids .=','.$fileIDs['network_files'][$i];
	}
	if ($ids != "") {
		$dbQuery = "delete from network_files where id in ($ids)";
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	}

	// 3.2 Delete image files for this publication
	$ids = "";	
	if (count($fileIDs['image_files']) >0) {
		$ids .= $fileIDs['image_files'][0];
	}
	for ($i=1; $i<count($fileIDs['image_files']); $i++) {
		$ids .=','.$fileIDs['image_files'][$i];
	}
	if ($ids != "") {
		$dbQuery = "delete from network_image_files where id in ($ids)";
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	}

	// 3.3 Delete thum image files for this publication
	$ids = "";	
	if (count($fileIDs['thum_image_files']) >0) {
		$ids .= $fileIDs['thum_image_files'][0];
	}
	
	for ($i=1; $i<count($fileIDs['thum_image_files']); $i++) {
		$ids .=','.$fileIDs['thum_image_files'][$i];
	}
	if ($ids != "") {
		$dbQuery = "delete from network_thum_image_files where id in ($ids)";
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	}

	// 3.4 Delete network_file_info
	if ($publicationRecord['publication_auto_id'] != -1) {
		$dbQuery = "delete from network_file_info where publication_id=".$publicationRecord['publication_auto_id'];
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	}	
		
	// 4. Delete from legend, to-do
	if ($publicationRecord['publication_auto_id'] != -1) {
		$dbQuery = "delete from legend_files where pub_id=".$publicationRecord['publication_auto_id'];
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	}

	// 5. Delete from supplement_material_files	
	if ($publicationRecord['supplement_file_id'] != -1) {
		$dbQuery = "delete from supplement_material_files where id=".$publicationRecord['supplement_file_id'];
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	}
	
	
	// 6. Delete from publications
	if ($publicationRecord['publication_auto_id'] != -1) {
		$dbQuery = "delete from publications where publication_auto_id=".$publicationRecord['publication_auto_id'];
		// Run the query
		if (!@ mysql_query($dbQuery, $connection))
			showerror();
		// Check Error message	
		if (mysql_error($connection) != "") $success = false;
	}	
	
	return $success;
}


?>

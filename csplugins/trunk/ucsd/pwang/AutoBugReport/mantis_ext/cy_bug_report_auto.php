<?php
# This script process bug report from Cytoscape automatic reporter and add the bug
# to bug tracker if it is a new one. If it is a duplication of existing, add
# a note to the existing one
#     Author : Peng-Liang Wang
#     Date:    Spetember 25, 2008

require_once( 'core.php' );
$t_core_path = config_get( 'core_path' );
require_once( $t_core_path.'string_api.php' );
require_once( $t_core_path.'file_api.php' );
require_once( $t_core_path.'bug_api.php' );
require_once( $t_core_path.'custom_field_api.php' );

//echo "Received a bug report<b>";

// work-around: set ($g_script_login_cookie, $g_cache_current_user_id), let the user have the 
// same permission as logged directly from a web broswer
$user = gpc_get_string( 'user', '' ); //guest/automaticBugReporter 
getCookieStr($user);

// Create a bug data object
$t_bug_data = createBugData();

//printBugData($t_bug_data);

if(!isValidBugData($t_bug_data)) {
	echo "invalid bug data<b>";
	exit(1);
}

$bug_id = getBugID($t_bug_data);
if ( $bug_id == -1) {
	# This is a new bug, create the bug
	echo "This is a new bug";
	//$t_bug_id = bug_create( $t_bug_data );
}
else {
	// this is a duplicated bug, add a new note
	echo "This is a duplicated bug";
	updateBug($bug_id, $t_bug_data);
}

echo "success";
// End of main logic


// ==============================================================================
// ==============================================================================

function updateBug($bug_id, $bug_data) {
	echo "update the bug -- bug_id = $bug_id";


}


function getBugID($bug_data) {
	$id = -1;
	// Query the DB with the summary and project_id
	$summary = $bug_data->summary;
	$project_id = $bug_data->project_id;
	
	if ( function_exists( 'db_is_connected' ) && db_is_connected() ) { 
		$query = sprintf('SELECT id FROM %s WHERE summary = \'%s\' AND project_id =\'%s\'',
								config_get( 'mantis_bug_table' ), $summary, $project_id );
       $result = db_query($query );   
       if ( 1 == db_num_rows( $result ) ) {
		   $row = db_fetch_array( $result );
		   $id = $row['id'];
       }
	}

	return $id;
}


function isValidBugData($bug_data) {
	if ($bug_data->summary == "") {
		return false;
	}	
	if ($bug_data->project_id == NULL) {
		return false;
	}	
	if ($bug_data->category == 'unknown') {
		return false;
	}	

	return true;
}


function createBugData() {
	$t_bug_data = new BugData;
	
	$t_bug_data->build				= gpc_get_string( 'build', '' );
	$t_bug_data->platform			= gpc_get_string( 'platform', '' );
	$t_bug_data->os					= gpc_get_string( 'os', '' );
	$t_bug_data->os_build			= gpc_get_string( 'os_build', '' );
	$t_bug_data->version			= gpc_get_string( 'cytoscape_version', '' );
	$t_bug_data->profile_id			= gpc_get_int( 'profile_id', 0 );
	$t_bug_data->handler_id			= gpc_get_int( 'handler_id', 0 );
	$t_bug_data->view_state			= gpc_get_int( 'view_state', config_get( 'default_bug_view_status' ) );

	// category -- determined by ErrorCode of the bug
	$category = getCategory(gpc_get_string( 'summary', 'unknown' )); //e.g. "Plugin", "API", "UI"
	$t_bug_data->category				= $category;
	
	$t_bug_data->reproducibility		= gpc_get_int( 'reproducibility', config_get( 'default_bug_reproducibility' ) );
	$t_bug_data->severity				= MINOR;
	$t_bug_data->priority				= gpc_get_int( 'priority', config_get( 'default_bug_priority' ) );
	$t_bug_data->summary				= gpc_get_string( 'summary','' );
	$t_bug_data->summary			= trim( $t_bug_data->summary );
		
	$t_bug_data->description			= gpc_get_string( 'description','' );
	$t_bug_data->steps_to_reproduce	= gpc_get_string( 'steps_to_reproduce', config_get( 'default_bug_steps_to_reproduce' ) );
	$t_bug_data->additional_information	= gpc_get_string( 'additional_info', config_get ( 'default_bug_additional_info' ) );

	$f_file					= gpc_get_file( 'file', null ); #@@@ (thraxisp) Note that this always returns a structure
															# size = 0, if no file
	$f_report_stay			= gpc_get_bool( 'report_stay', false );
	
	// Cytoscape_version ==> Project_name ==> Project_id
	$t_bug_data->project_id			= getProjectID(gpc_get_string( 'cytoscape_version', '' )); //gpc_get_int( 'project_id' );

	$t_bug_data->reporter_id		= auth_get_current_user_id();

	$t_bug_data->target_version		= access_has_project_level( config_get( 'roadmap_update_threshold' ), $t_bug_data->project_id ) ? gpc_get_string( 'target_version', '' ) : '';

	return $t_bug_data;
}


function getCategory($summary) {
	$category = 'unknown';
	// TO-DO: Determie category by summary (title of the bug)
	
	
	$category = "API"; // For test only
	return $category;  
}

function getProjectID($cytoscape_version) {
	// TO-DO
	// Cytoscape version ==> project_name
	$project_name = "Cytoscape test";
	if ($cytoscape_version == "3.1") {
		$project_name = "Cytoscape 3.1";
	}

	// project_name ==> project_id
	if ( function_exists( 'db_is_connected' ) && db_is_connected() ) { 
       $query = "SELECT id FROM mantis_project_table WHERE name = '$project_name'";

       $result = db_query($query);   
       if ( 1 == db_num_rows( $result) ) {
		   $row = db_fetch_array( $result );
		   $id = $row['id'];
       }
	   else {
	   	return NULL;
	   }
	}
	return $id;
}

function getCookieStr($user) {
	global $g_script_login_cookie, $g_cache_current_user_id;

	if ( function_exists( 'db_is_connected' ) && db_is_connected() ) { 
       $query = sprintf('SELECT id, cookie_string FROM %s WHERE username = \'%s\'',
								config_get( 'mantis_user_table' ), $user );
       $result = db_query($query );   
       if ( 1 == db_num_rows( $result ) ) {
		   $row = db_fetch_array( $result );
		   $t_cookie = $row['cookie_string'];
		   $g_cache_current_user_id = $row['id'];
       }
	}
	$g_script_login_cookie = $t_cookie;
}

# This function is for debug only
function printBugData($bugData){
	//echo "build = ", $bugData->build;
	//echo "platform = ", $bugData->platform;
	echo "os = ", $bugData->os;
	echo "os_build = ", $bugData->os_build;
	echo "version = ", $bugData->version;
	//echo "profile_id = ", $bugData->profile_id;
	//echo "handler_id = ", $bugData->handler_id;
	echo "view_state = ", $bugData->view_state;
	echo "category = ", $bugData->category;
	//echo "severity = ", $bugData->severity;
	//echo "priority = ",$bugData->priority;
	echo "summary = ", $bugData->summary;
	echo "description = ", $bugData->description;
	echo "steps_to_reproduce = ", $bugData->steps_to_reproduce;
	echo "additional_information = ", $bugData->additional_information;
	//echo "f_file = ", "";
	//echo "f_report_stay = ", ;
	echo "project_id = ", $bugData->project_id;
	//echo "target_version = ", $bugData->target_version;
}

?>

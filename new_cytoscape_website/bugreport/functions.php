<?php
	// Include the DBMS credentials
	//include 'db.inc';

function showPageHeader($pageTitle) {
?>
	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html xmlns="http://www.w3.org/1999/xhtml">
	    <head>
	    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	    <link href="http://cytoscape.org/css/main.css" type="text/css" rel="stylesheet" media="screen" />
	    <link href="css/bugreport.css" type="text/css" rel="stylesheet" media="screen" />
	    
	    <title><?php echo $pageTitle;?></title>
	    <script type="text/javascript" 
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
	    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
	    <script type="text/javascript" src="http://chianti.ucsd.edu/cyto_web/bugreport/js/menu_generator.js"></script>
	    
	    </head>
	
	    <body>
	<div id="container">
	
	<script src="http://cytoscape.org/js/header.js"></script>
    
<?php 
}


function showPageTail() {
	?>
	<script src="http://cytoscape.org/js/footer.js"></script> 
	</body>
	</html>
	<?php 
}



function getDBConnection($mode) {
	// Include the DBMS credentials
	require_once 'db.inc';
		
	if ($mode == NULL || ($mode != 'new' && $mode != 'edit')){
		$mode = 'new';//user rpermission, 'edit' is for staff permission
	}
	
	// Connect to the MySQL DBMS
	if ($mode == 'edit') {
		if (!($connection = @ mysql_pconnect($dbServer, $cytostaff, $cytostaffPass)))
			showerror();
	} 
	else // $mode == 'new'
	{		
		if (!($connection = @ mysql_pconnect($dbServer, $dbUser, $dbPass)))
			showerror();
	}

	// Use the bugs database
	if (!mysql_select_db($dbName, $connection)) {
		showerror();
	}
	return $connection;
}

?>
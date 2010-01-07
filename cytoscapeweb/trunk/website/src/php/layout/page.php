<?php 
	// declarations
	include("php/layout/declaration.php");
		
	// page
	include("php/layout/header.php");
	
	if( file_exists($include) ){
		include($include);
	} else {
		include("php/content/error_page.php");
	}
	
	include("php/layout/footer.php");
?>
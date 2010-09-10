<?php
/*
 * Created on Nov 29, 2007
 * Content within the form of user input
 */
 	if (($mode == 'new') && empty ($_POST['tfContactEmail'])) {
		$validated = false;
?>
		<br><strong>Error: E-mail is a required field.</strong><br>
		<?php
	}
	else {
			if(!eregi("^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,3})$", $_POST['tfContactEmail'])) {
				$validated = false;
	?>
			<strong>Error: Invalid email address.</strong>
			<?php
			}
	}
	
	if (isset($isPluginURLSubmission) && ($isPluginURLSubmission) ) {
		//echo "isPluginURLSubmission == true<br>";
		if (empty($projectURL)) {
		$validated = false;
?>
		<strong>Error: Project URL is required.</strong>
		<?php
			
		}
		if (empty($cyVersion)) {
		$validated = false;
?>
		<strong>Error: Cytoscape version is required.</strong>
		<?php
			
		}
	}
	else if (($mode == 'new') && empty ($_FILES['filePlugin']['name'])) {
		//if mode == 'new', A jar/zip file is required
		//if mode == 'edit', if a jar/zip file is not provided,
		// the existing file will not be updated. 

		$validated = false;
?>
		<strong>Error: A jar/zip file is required.</strong>
		<?php
	}
 
 ?>
 
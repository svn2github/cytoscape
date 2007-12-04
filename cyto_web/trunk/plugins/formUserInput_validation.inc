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
	//if mode == 'new', A jar/zip file is required
	//if mode == 'edit', if a jar/zip file is not provided,
	//the existing file will not be updated. 
	if (($mode == 'new') && empty ($_FILES['filePlugin']['name'])) {
		$validated = false;
?>
		<strong>Error: A jar/zip file is required.</strong>
		<?php
	}
 
 ?>
 
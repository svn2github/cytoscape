<?php
/*
 * Created on Nov 29, 2007
 * Content within the form of user input
 */
//if ($tried != NULL && $tried == 'yes') {

	if (empty ($_POST['tfName'])) {
		$validated = false;
?>
		<strong>Error: Plugin_name is a required field.</strong><br>
		<?php
	}
	if (empty ($_POST['tfVersion'])) {
		$validated = false;
?>
		<strong>Error: Version is a required field.</strong><br>
		<?php
	}
	if (empty ($_POST['taDescription'])) {
		$validated = false;
?>
		<strong>Error: Description is a required field.</strong><br>
		<?php
	}
	if ($category == "Please choose one") {
		$validated = false;
?>
		<strong>Error: Category is a required field.</strong><br>
		<?php
	}

	// validate the release date
	//if (!(empty ($month) && empty ($day) && empty ($year))) {
		if (!((strspn($month, "0123456789") == strlen($month)) && (strlen($month) > 0) && (strlen($month) < 3))) {
			$validated = false;
?>
			<strong>Invalid release month</strong> <br>
			<?php
		}
		if (!((strspn($day, "0123456789") == strlen($day)) && (strlen($day) > 0) && (strlen($day) < 3))) {
			$validated = false;
?>
			<strong>Invalid release day </strong><br>
			<?php
		}
		if (!((strspn($year, "0123456789") == strlen($year)) && (strlen($year) > 0) && (strlen($year) == 4))) {
			$validated = false;
?>
			<strong>Invalid release year </strong><br>
			<?php
		}
	//}
	
	//Validate the Cytoscape version
	// value of $cyVersion must have the pattern like '2.5,2.6,2.61,2.7'
	// ??? validation code here
	
	
//} 
// End of form validation
 
 ?>
 
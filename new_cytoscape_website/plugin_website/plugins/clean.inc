<?php



// Secure the user data by escaping characters and shortening the input string
function cleanLength($input, $maxlength) {
	$input = substr($input, 0, $maxlength);
	$input = EscapeShellCmd($input);
	return ($input);
}

function cleanInt($input) {
	if ( preg_match("/^\d+$/", $input) ) {
		return ($input);
	} else {
		return (0); 
	}
}

function cleanVersion($input) {
	if ( preg_match("/^\d+\.\d+(\.\d+)*$/", $input) ) {
		return ($input);
	} else {
		return (NULL);
	}
}
?>

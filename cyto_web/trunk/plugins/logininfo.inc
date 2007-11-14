<?php
	$auth_ok = 0;

	$valid_user = "cytostaff";
	$valid_pass = "XXXXXX";

	$user = "";
	$pass = "";
	if (isset ($_SERVER['PHP_AUTH_USER'])) {
		$user = $_SERVER['PHP_AUTH_USER'];
		$pass = $_SERVER['PHP_AUTH_PW'];
	}

	if (($user == $valid_user) && ($pass == $valid_pass)) {
		$auth_ok = 1;
	}

	if (!$auth_ok) {
    	header('WWW-Authenticate: Basic realm="My Realm"');
    	header('HTTP/1.0 401 Unauthorized');
		die('Not authorized');
	}

	// If arrives here, is a valid user
?> 

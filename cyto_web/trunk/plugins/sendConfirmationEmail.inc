<?php
// Send a confirmation e-mail to user
// Also send e-mails to notify cytostaff that new plugin is uploaded
	
function sendConfirmartionEmail($yourName, $email, $pluginName, $pluginDescription) {
		
	include 'cytostaff_emails.inc';

	$from = $cytostaff_emails[0];
	$to = $email;// Author e-mail contact
	$bcc = "";
	for ($i=0; $i<count($cytostaff_emails); $i++){
        	$bcc = $bcc . $cytostaff_emails[$i] . " ";
	}
	$subject = "Your plugin -- " . $pluginName;
	$body = $yourName.",\n\nThank you for submitting " . $pluginName . " to Cytoscape. " .
		"\n\nDescription:\n\n".$pluginDescription."\n\n".
        	"Cytoscape staff will review your plugin and publish it on the Cytoscape website." .
        	"\n\n\nCytoscape team";

	$headers = "From: " . $from . "\r\n"; 
	if ($bcc != "") {
		$headers = $headers . "BCC: " . $bcc;
	}

	if (trim($to) == "") {
		// in case user did not provide an e-mail address, notify the cytostaff
        	$to = $cytostaff_emails[0];
        	$body = $body . "\n\nNote: User did not provide an e-mail address!";
	}

	if (mail($to, $subject, $body, $headers)) {
  		//echo("<p>Confirmation e-mail was sent!</p>");
 	} else {
  		echo("<p>Failed to send a confirmation e-mail...</p>");
 	}
}
?>	

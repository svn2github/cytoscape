<?php
include_once 'db.php';
include_once 'cc_utils.php';

if ($argc != 2 ) {
	echo "\tusage: php $argv[0] <pub_id>\n";
	exit;
}

$pub_id = $argv[1];
$pub_html = getPublicationHTML($connection, $pub_id);

//echo "pub_id = $pub_id\n";

sendNotificationEmail($pub_id, $pub_html);

function sendNotificationEmail($pub_id, $pub_html) {
	include 'staff_emails.php';

	$staff_emails = getStaffEmails();
	$from = $staff_emails[0];
	
	$to = $staff_emails[0] ;
	$bcc = "";
	for ($i=0; $i<count($staff_emails); $i++){
        	$bcc = $bcc . $staff_emails[$i] . " ";
	}
	$subject = "CellCircuits enrichment table has been loaded for pub_id = $pub_id";

	$body = "\nA note to CellCircuits administrators:\n\tThe backend job to load enrichment table for publication (pub_id = $pub_id) is done!";
	$body .= "\n\n".$pub_html."\n";
	$headers = "From: " . $from . "\r\n"; 
	if ($bcc != "") {
		$headers = $headers . "BCC: " . $bcc;
	}

	//echo "$body\n";

	if (trim($to) == "") {
        	$to = $staff_emails[0];
	}

	if (mail($to, $subject, $body, $headers)) {
  		echo("<p>Notificatio e-mail was sent!</p>");
 	} else {
  		echo("<p>Failed to send a notification e-mail...</p>");
 	}
} // sendNotificationEmail()


function getPublicationHTML($connection, $pub_id){

	$dbQuery  = "SELECT * from publications where publication_auto_id = $pub_id";
	
	// Run the query
	if (!($result = @ mysql_query($dbQuery, $connection)))
		showerror();

	if (@ mysql_num_rows($result) == 0) {
		return NULL;
	}

	$pub_xml = NULL;
	// should be only one record
	while ($_row = @ mysql_fetch_array($result)) {
		$pub_xml = $_row["pubmed_xml_record"];
	}

	$pub_html = convert_xml2html($pub_xml, 'pubmedref_to_enrichment_notification.xsl');

	return $pub_html;
}


?>
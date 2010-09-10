<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Sendemail Script</title>
</head>
<body>

<!-- Reminder: Add the link for the 'next page' (at the bottom) -->
<!-- Reminder: Change 'YourEmail' to Your real email -->

<?php
$ip = $_POST['ip'];
$httpref = $_POST['httpref'];
$httpagent = $_POST['httpagent'];

$visitor = $_POST['visitor'];
$visitormail = $_POST['visitormail'];
$subject = $_POST['subject'];
$notes = $_POST['notes'];

if (eregi('http:', $notes)) {
	die("Do NOT try that! ! ");
}
if (!$visitormail == "" && (!strstr($visitormail, "@") || !strstr($visitormail, "."))) {
	echo "<h2>Use Back - Enter valid e-mail</h2>\n";
	$badinput = "<h2>Feedback was NOT submitted</h2>\n";
	echo $badinput;
	die("Go back! ! ");
}

if (empty ($visitor) || empty ($visitormail) || empty ($notes)) {
	echo "<h2>Use Back - fill in all fields</h2>\n";
	die("Use back! ! ");
}

$todayis = date("l, F j, Y, g:i a");

$notes = stripcslashes($notes);

$message = " $todayis [PST] \n
Message: $notes \n
From: $visitor ($visitormail)\n
Additional Info : IP = $ip \n
Browser Info: $httpagent \n
Referral : $httpref \n
";

$from = "From: $visitormail\r\n";

include 'cytostaff_emails.inc';

$from = $cytostaff_emails[0];
$to = ""; 
for ($i = 0; $i < count($cytostaff_emails); $i++) {
	$to = $to . $cytostaff_emails[$i] . " ";
}

$headers = "From: pwang@chianti.ucsd.edu\r\n";
$subject = "[Contact us] ".$subject;

//mail("YourEmail", $subject, $message, $from);
if (!mail($to, $subject, $message, $headers)) {
	die ("<p>Failed to send this e-mail...</p>");
}
?>


<p align="center">
Date: <?php echo $todayis ?>
<br />
Thank You : <?php echo $visitor ?> ( <?php echo $visitormail ?>) Your message has been sent.
<br />


</body>
</html>

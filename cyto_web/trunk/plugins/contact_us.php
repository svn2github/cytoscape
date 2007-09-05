<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Contact Cytoscape staff</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/cyto_web/css/cytoscape.css">
	<link rel="shortcut icon" href="/cyto_web/images/cyto.ico">
</head>
<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title">Contact Cytoscape staff</div>
</div>

<?php include "../nav.php"; ?>


<form method="post" action="sendmail_from_contact_form.php">

  <p>&nbsp;</p>
  <table width="200" border="0">
    <tr>
      <td width="10"><blockquote>
        <p>&nbsp;</p>
      </blockquote></td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><?php
$ipi = getenv("REMOTE_ADDR");
$httprefi = getenv ("HTTP_REFERER");
$httpagenti = getenv ("HTTP_USER_AGENT");
?>
        <input type="hidden" name="ip" value="<?php echo $ipi ?>" />
        <input type="hidden" name="httpref" value="<?php echo $httprefi ?>" />
        <input type="hidden" name="httpagent" value="<?php echo $httpagenti ?>" />
Your Name: </td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><input type="text" name="visitor" size="40" /></td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td>Your Email:</td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><input type="text" name="visitormail" size="40" /></td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td>Subject:</td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><input name="subject" type="text" id="subject" size="50" /></td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td>Mail Message: </td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><textarea name="notes" rows="10" cols="50"></textarea></td>
    </tr>
    <tr>
      <td width="10">&nbsp;</td>
      <td><input name="submit" type="submit" value="Send Mail" /></td>
    </tr>
  </table>
  <p>&nbsp;</p>
</form>


<?php include "../footer.php"; ?>
<br>
</body>
</html>


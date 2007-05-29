<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape 2.x Plugins</title>
	<link rel="stylesheet" type="text/css" media="screen" href="/css/cytoscape.css">
	<link rel="shortcut icon" href="images/cyto.ico">
	<SCRIPT LANGUAGE="JavaScript" SRC="mktree.js"></SCRIPT>
</head>
<body bgcolor="#ffffff">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tbody>
		<tr>
			<td width="10">&nbsp;
			</td>
			<td valign="bottom">
				<h1>Cytoscape 2.x Plugins Release Note</h1>
			</td>
		</tr>
	</tbody>
</table>
<?php include "../nav.php"; ?>

<?php
$data = base64_decode(($_GET['data']));
$data = unserialize($data);

?>
<br>
<div id="indent">
	<p><strong>Name:</strong> <?php echo $data[0] ?></p>
	<p><strong>Version: </strong><?php echo $data[1] ?></p>
	<p><big><b>Release Note:</b></big><br><?php echo $data[2] ?></p>
  <p>&nbsp;</p>
</div>

<?php include "../footer.php"; ?>
<br>
</body>
</html>

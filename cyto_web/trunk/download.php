<? include "config.php"; ?>	
<?php
	#  Get User URL Parameters
	$name = $_REQUEST["name"];
	$org = $_REQUEST["org"];
	$email = $_REQUEST["email"];
	$accept = $_REQUEST["accept"];
	$submit  = $_REQUEST["submit"];
	$contact = $_REQUEST["contact"];
	
	#  Bare Bones Form Validation
	$error_flag = false;
	if (isset($submit)) {
		if ($name=="") {
			$name_error = "Incomplete";
			$error_flag = true;
		}
		if ($org =="") {
			$org_error = "Incomplete";
			$error_flag = true;
		}
		if ($email=="") {
			$email_error = "Incomplete";
			$error_flag = true;
		}
		if ($accept=="") {
			$accept_error = "Unchecked";
			$error_flag = true;
		}		
	}
	
	#  Determine the Download File and Page Title
	$file = $_REQUEST["file"];
	$found = false;
	foreach ($release_array as $fileId => $num) {
		if ($file == $fileId) {
			$title = "Download Cytoscape $release_array[$file]";
			$found = true;
			break;
		}
	}

	# if the user navigates directly to download.php file won't be set,
	# so set it to the latest version
	$rra = array_flip($release_array);
	if ( !$found ) {
		$file = $rra[$latest_version]; 
		$title = "Download Cytoscape $latest_version";
	} 

	if (isset($submit) && $error_flag == false) {
		$title = "Thank you!";
	}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta content="text/html; charset=ISO-8859-1" http-equiv="content-type">
	<title><?= $title ?></title> 
	<link href="css/cytoscape.css" media="screen" type="text/css" rel="stylesheet">
	<link href="images/cyto.ico" rel="shortcut icon">
</head>
<body bgcolor="#ffffff">
<div id=container>
<table summary="" cellspacing="0" cellpadding="2" border="0" id="feature">
	<tbody>
		<tr>
			<td width="10">
				&nbsp;
			</td>
			<td valign="center">
				<h1><?= $title ?></h1> 
			</td>
		</tr>
	</tbody>
</table>

<? include "nav.php"; ?>
<?php
	#  Write to Data File
	if (isset($submit) && $error_flag == false) {
		$now = date("F j, Y, g:i a");  
		$ip = getenv(REMOTE_ADDR);
		$contactStr = "NO_EMAIL";
		if (isset($contact)) {
			$contactStr = "YES_EMAIL";
		}
		$log = "$now\t$ip\t$file\t$name\t$org\t$email\t$contactStr\n";
		$fr = fopen($cyto_data, 'a');
		fputs($fr, $log);
		fclose($fr);
?>
	<? include "download_$file.php"; ?>	
<? }  else { ?>
	
<div id="indent">
		<p>
			Cytoscape is available as a platform-independent open-source Java application, released under the
			terms of the <a href="http://www.gnu.org/copyleft/lesser.html">LGPL</a>. 
			To obtain Cytoscape, you must:
		</p>
		<ol>
			<li>
				Read the license agreement that follows and make sure you agree with its terms.
			</li>
			<li>
				If so, fill in the application form.
				<strong><u>This information is used to understand the Cytoscape user base and 
				will not be used for other purposes.</u></strong>
			</li>
			<li>
				Click the Proceed button to be transferred to the download page.
			</li>
		</ol>
		<center>
		<table width=100%>
			<TR>
			<TD WIDTH=100></TD>
			<TD><form>
				<textarea wrap="1" cols="72" rows="12" name="license agreement">
<? include "license.txt"; ?>
				</textarea>
			</form>
			<form action="download.php" method="POST">
				<input type="hidden" name="file" value="<?= $file ?>">
				<br>
				<table border="0">
					<tbody>
						<tr>
							<td align="right">
								Name:
							</td>
							<td>
								<input size="30" type="text" name="name" value="<?= $name ?>" >
							</td>
							<td>
								<FONT COLOR=RED><?= $name_error ?></FONT>
								<br>
							</td>
						</tr>
						<tr>
							<td align="right">
								Organization:
							</td>
							<td>
								<input size="30" type="text" name="org" value="<?= $org ?>">
							</td>
							<td>
								<FONT COLOR=RED><?= $org_error ?></FONT>
								<br>
							</td>
						</tr>
						<tr>
							<td align="right">
								Email:
							</td>
							<td>
								<input size="30" type="text" name="email" value="<?= $email ?>">
							</td>
							<td>
								<FONT COLOR=RED><?= $email_error ?></FONT>
								<br>
							</td>
						</tr>
						<tr>
							<td>
								<br>
							</td>
							<td>
								<br>
								I accept the terms of the license
								<br>
								agreement specified above: <input type="checkbox" name="accept">
							</td>
							<td>
								<FONT COLOR=RED><?= $accept_error ?></FONT>
								<br>
							</td>
						</tr>
						<tr>
							<td>
								<br>
							</td>
							<td>
								<br>
								We enjoy keeping in touch with our users.
								<br>May we add you to our mailing list?
								<input type="checkbox" name="contact">
							</td>
						</tr>						
						<tr>
							<td>
								<br>
							</td>
							<td>
								<br>
								<center>
									<input value="Proceed to Download" type="submit" name="submit">
								</center>
							</td>
						</tr>
					</tbody>
				</table>
			</form>
			</TD>
			</TR>
			</TABLE>
</div>
<p>
	&nbsp;
</p>
<p>
</p>
<? } ?>
</div>
<? include "footer.php"; ?>
</div>
</body>
</html>

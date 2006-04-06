<? include "config.php"; ?>	
<?php
	#  Get User URL Parameters
	$name = $_REQUEST["name"];
	$org = $_REQUEST["org"];
	$email = $_REQUEST["email"];
	$accept = $_REQUEST["accept"];
	$submit  = $_REQUEST["submit"];
	
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
	if ($file == "litsearch_v2") {
		$title = "Download Agilent Literature Search Version 2.0";
	} else if ($file == "cyto2_1") {
		$title = "Download Cytoscape 2.1";
	} else if ($file == "cyto2") {
		$title = "Download Cytoscape 2.0";
	} else if ($file == "cyto1") {
		$title = "Download Cytoscape 1.1";
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

<?php
	#  Write to Data File
	if (isset($submit) && $error_flag == false) {
		$now = date("F j, Y, g:i a");  
		$ip = getenv(REMOTE_ADDR);
		$log = "$now\t$ip\t$file\t$name\t$org\t$email\n";
		$fr = fopen($litsearch_log, 'a');
		fputs($fr, $log);
		fclose($fr);
?>
	<? include "download_$file.php"; ?>	
<? }  else { ?>
	
<div id="indent">
		<p>
                <p>Welcome to the download page for Agilent Laboratories' Agilent 
                Literature Search Software, a meta-search tool for automatically querying multiple 
                text-based search engines (both public and proprietary) in order to aid biologists 
                faced with the daunting task of manually searching and extracting associations among 
                genes/proteins of interest. </p>
                <p>Agilent Literature Search Software can be used in conjunction 
                with <a href="http://www.cytoscape.org/index.php">Cytoscape</a> v2.2, which provides a 
                means of generating an overview network view of gene/protein associations.</p>
                <p class="bodycopy">Agilent Literature Search Software plugin is available under the 
                terms of the <b>Agilent License Agreement below</b> . T
                o obtain Agilent Literature Search Software plugin, you must: </p>

		</p>
		<ol>
			<li>
				Read the license agreement that follows and make sure you agree with its terms.
			</li>
			<li>
				If so, fill in the application form.
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
AGILENT TECHNOLOGIES, INC. SOFTWARE LICENSE AGREEMENT ATTENTION:
DOWNLOADING, COPYING, PUBLICLY DISTRIBUTING, OR USING THIS SOFTWARE IS SUBJECT TO THE AGREEMENT SET FORTH BELOW. TO DOWNLOAD, STORE, INSTALL, OR RUN THE SOFTWARE, YOU MUST FIRST AGREE TO AGILENT&#8217;S SOFTWARE LICENSE AGREEMENT BELOW. IF YOU HAVE READ, UNDERSTAND AND AGREE TO BE BOUND BY THE SOFTWARE LICENSE AGREEMENT BELOW, YOU SHOULD CLICK ON THE &#8220;AGREE&#8221; BOX AT THE BOTTOM OF THIS PAGE. THE SOFTWARE WILL THEN BE DOWNLOADED TO OR INSTALLED ON YOUR COMPUTER. IF YOU DO NOT AGREE TO BE BOUND BY THE SOFTWARE LICENSE AGREEMENT BELOW, YOU SHOULD CLICK ON THE &#8220;DO NOT AGREE&#8221; BOX AT THE BOTTOM OF THIS PAGE AND CANCEL THE DOWNLOAD OR INSTALLATION OF THE SOFTWARE. IF YOU HAVE PURCHASED THE SOFTWARE FROM AGILENT, YOU MAY RETURN THE SOFTWARE FOR A FULL REFUND, OR, IF THE SOFTWARE IS SUPPLIED AS PART OF ANOTHER PRODUCT, YOU MAY RETURN THE ENTIRE PRODUCT FOR A FULL REFUND. 
Software. &#8220;Software&#8221; means one or more computer programs in object code format, whether stand-alone or bundled with other products, and related documentation. It does NOT include programs in source code format. License Grant. Agilent grants you a non-exclusive license to download one copy of the Software, and to store or run that copy of the Software for internal use and purposes in accordance with this Agreement and the documentation provided with the Software. Such documentation may include license terms provided by Agilent&#8217;s third party suppliers, which will apply to the use of the Software and take precedence over these license terms. In the absence of documentation specifying the applicable license, you may store or run one copy of the Software on one machine or instrument. If the Software is otherwise licensed for concurrent or network use, you may not allow more than the maximum number of authorized users to access and use the Software concurrently. 
License Restrictions. You may make copies or adaptations of the Software for archival purposes or when copying or adaptation is an essential step in the authorized use of the Software, but for no other purpose. You must reproduce all copyright notices in the original Software on all permitted copies or adaptations. You may not copy the Software onto any public or distributed network. Upgrades. This license does not entitle you to receive upgrades, updates or technical support. Such services may be purchased separately. 
Ownership. The Software is owned and copyrighted by Agilent or its third party suppliers. Agilent and its third party
suppliers retain all right, title and interest in the Software. Agilent and its third party suppliers may protect their respective rights in the Software in the event of any violation of this Agreement. 
No Disassembly. You may not disassemble or otherwise modify the Software without written authorization from Agilent, except as permitted by law. Upon request, you will provide Agilent with reasonably detailed information regarding any permitted disassembly or modification. 
High Risk Activities. The Software is not specifically written, designed, manufactured or intended for use in the planning, construction, maintenance or direct operation of a nuclear facility, nor for use in on line control or fail safe operation of aircraft navigation, control or communication systems, weapon systems or direct life support systems. 
Transfer. You may transfer the license granted to you here provided that you deliver all copies of the Software to the transferee along with this Agreement. The transferee must accept this Agreement as a condition to any transfer. Your license to use the Software will terminate upon transfer. 
Termination. Agilent may terminate this license upon notice for breach of this Agreement. Upon termination, you must immediately destroy all copies of the Software. 
Export Requirements. If you export, re-export or import Software, technology or technical data licensed hereunder, you assume responsibility for complying with applicable laws and regulations and for obtaining required export and import authorizations. Agilent may terminate this license immediately if you are in violation of any applicable laws or
regulations. 
U.S. Government Restricted Rights. Software and technical data rights granted to the federal government include only those rights customarily provided to end user customers. Agilent provides this customary commercial license in Software and technical data pursuant to FAR 12.211 (Technical Data) and 12.212 (Computer Software) and, for the
Department of Defense, DFARS 252.227-7015 (Technical Data &#8211; Commercial Items) and DFARS 227.7202-3 (Rights in Commercial Computer Software or Computer Software Documentation). NO WARRANTY. TO THE EXTENT ALLOWED BY LOCAL LAW, AND EXCEPT TO THE EXTENT AGILENT HAS PROVIDED A SPECIFIC WRITTEN WARRANTY APPLICABLE TO THIS PRODUCT, THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, WHETHER ORAL OR WRITTEN, EXPRESS OR IMPLIED. AGILENT SPECIFICALLY DISCLAIMS ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. SHOULD THE SOFTWARE PROVE DEFECTIVE, YOU ASSUME THE ENTIRE RISK AND COST RESULTING FROM OR RELATING TO THE DEFECT. SOME JURISDICTIONS DO NOT ALLOW EXCLUSIONS OF IMPLIED WARRANTIES OR CONDITIONS, SO THE ABOVE EXCLUSION MAY NOT APPLY TO YOU. YOU MAY HAVE OTHER RIGHTS THAT VARY
ACCORDING TO LOCAL LAW. LIMITATION OF LIABILITY. TO THE EXTENT ALLOWED BY LOCAL LAW, IN NO EVENT WILL AGILENT OR ITS SUBSIDIARIES, AFFILIATES OR SUPPLIERS BE LIABLE FOR DIRECT, SPECIAL, INCIDENTAL, CONSEQUENTIAL OR OTHER DAMAGES (INCLUDING LOST PROFIT, LOST DATA, OR DOWNTIME COSTS), ARISING OUT OF THE USE, INABILITY TO USE, OR THE RESULTS OF USE OF THE SOFTWARE, WHETHER BASED IN WARRANTY, CONTRACT, TORT OR OTHER LEGAL THEORY, AND WHETHER OR NOT ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
YOUR USE OF THE SOFTWARE IS ENTIRELY AT YOUR OWN RISK. SOME JURISDICTIONS DO NOT ALLOW THE EXCLUSION OR LIMITATION OF LIABILITY FOR DAMAGES, SO THE ABOVE LIMITATION MAY NOT APPLY TO YOU. Applicable Law.
Disputes arising in connection with this Agreement will be governed by the laws of the United States and of the State of New York, without regard to choice of law provisions. The United Nations Convention for Contracts for the International Sale of Goods will not apply to this Agreement. 
Unenforceability. To the extent that any provision of this Agreement is determined to be illegal or unenforceable, the remainder of this Agreement will remain in full force and effect. 
Entire Agreement. This Agreement constitutes the entire agreement between you and Agilent, and supersedes any previous communications, representations or agreements between the parties, whether oral or written, regarding transactions hereunder except for a specific warranty issued by Agilent with regard to this product. Your additional or different terms and conditions will not apply. This Agreement may not be changed except by an amendment signed by an authorized
representative of each party. 
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

</body>
</html>

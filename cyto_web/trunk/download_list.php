<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
		<title>
			 Download Cytoscape
		</title>
		<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css" />
		<link rel="shortcut icon" href="images/cyto.ico" />
	</head>
	<body bgcolor="#FFFFFF">
		<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
			<tr>
				<td width="10">
					&nbsp;
				</td>
				<td valign="center">
					<h1>
						Download Cytoscape
					</h1>
				</td>
			</tr>
		</table>
<? include "nav.php"; ?>
<div  id="indent">
			<P>
			<big><b>For Users:  Download Cytoscape Releases</b></big>
			<P>You can download Cytoscape from the links below.

			<P>To download Cytoscape, you will be required to read and agree with
			our license terms, and supply basic contact information, including name,
			organization and email address.


			<UL>
			<!--
				<LI>
					Download Cytoscape 2.2 [Binaries and/or Source Files]
				</LI>
		    -->
		    
		    <LI>
					<A HREF="download.php?file=cyto2_2">Download Cytoscape 2.2</A>
					[Binaries and/or Source Files]
			</LI>
				<LI>
					<A HREF="download.php?file=cyto2_1">Download Cytoscape 2.1</A>
					[Binaries and/or Source Files]
				</LI>
			<LI><A HREF="download.php?file=cyto2">Download Cytoscape 2.0</A>
				[Binaries and/or Source Files]
			<LI><A HREF="download.php?file=cyto1">Download Cytoscape 1.1</A>
				[Binaries and/or Source Files]
			</UL>

			<P>
			<big><A NAME="cvs"><b>For Developers:  Download Latest Source Code from our CVS Server</A></b></big>
			<P>Instructions for downloading the latest Cytoscape source code
			from our CVS server are provided below.
			When prompted for a password for anonymous, simply press the
			Enter key.
			<P>cvs -d :pserver:anonymous@bordeaux.ucsd.edu:/cvsdir5 login
			<BR>cvs -d :pserver:anonymous@bordeaux.ucsd.edu:/cvsdir5 co cytoscape
			<BR>cvs -d :pserver:anonymous@bordeaux.ucsd.edu:/cvsdir5 logout
			</P>
</div>
<? include "footer.php"; ?>
	</body>
</html>

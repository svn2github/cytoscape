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
		<div id="topbar">
			<div class="title">Download Cytoscape</div>
		</div>
<? include "nav.php"; ?>
<?include "config.php";?>
<div  id="indent">
			<P>
			<big><b>For Users:  Download Cytoscape Releases</b></big>
			<P>You can download Cytoscape from the links below.

			<P>To download Cytoscape, you will be required to read and agree with
			our license terms, and supply basic contact information, including name,
			organization and email address.


			<UL>

			<?
			foreach ($release_array as $dir => $num) {
				print "<li><a href=\"download.php?file=$dir\">Download Cytoscape $num</a></li>\n";
			}
			?>

			</ul>

			<P>
			<big><A NAME="cvs"><b>For Developers:  Download Latest Source Code from our Subversion Server</A></b></big>
			<P>Instructions for downloading the latest Cytoscape source code
			from our <a href="http://subversion.tigris.org">Subversion</a> server are provided below.

			<P>
			<ul><li> svn checkout http://chianti.ucsd.edu/svn/cytoscape/trunk cytoscape</li></ul>
			</P>
			Read a 
			<a href="http://svnbook.red-bean.com">book</a>
			about 
			<a href="http://subversion.tigris.org">Subversion</a>.<p>
</div>
<? include "footer.php"; ?>
	</body>
</html>

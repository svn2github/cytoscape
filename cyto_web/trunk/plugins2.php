<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape 2.0 PlugIns</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css">
	<link rel="shortcut icon" href="images/cyto.ico">
</head>
<body bgcolor="#ffffff">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tbody>
		<tr>
			<td width="10">
				&nbsp;
			</td>
			<td valign="center">
				<h1>Cytoscape 2.0 PlugIns</h1>
			</td>
		</tr>
	</tbody>
</table>

<? include "nav.php"; ?>

<br>
<div id="indent">
	<big><b>About Cytoscape PlugIns:</b></big>
	<p>
		Cytoscape includes a flexible PlugIn architecture that enables developers to add extra functionality beyond that provided in the
		core. PlugIns also provide a convenient place for testing out new Cytoscape features. As more PlugIns become
		available, they will be listed on this page. Check back often!
	</p
	<p><b>Note that PlugIns on this page will only work with Cytoscape 2.0</b>.  We also maintain a list of 
	<A HREF="plugins1.php">Cytoscape 1.1 PlugIns</A>.
	<P>If you are interested in building your own Cytoscape 2.0 PlugIn, check out the
		<a href="http://cytoscape.systemsbiology.net/Cytoscape2.0/plugin/pluginTutorial/pluginTutorial.html">Cytoscape 2.0 PlugIn Tutorial</A>
	</p>
	<p>
		<big><b>Current Cytoscape 2.0 PlugIns:</b></big>
		<br>
	</p>
	<table style="margin-left: 30;  margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small"
		cellpadding=5 cellspacing=5 width=100%>
			<tbody>
				<tr>
					<td width="20%" valign="top">
						<B>cPath PlugIn</B> <font size="-1">
							<br>
							Version: 1 Beta
							<br>
							Release Date: July 13, 2004</font>
					</td>
					<td width="60%" valign="top">
						The cPath PlugIn enables Cytoscape users to query, retrieve and visualize interactions
						retrieved from the <A HREF="http://cbio.mskcc.org/cpath">cPath database</A>.
						<p>
							This product includes software developed by the Apache Software Foundation (<a href="http://www.apache.org/">http://www.apache.org</a>).
						</p>
						<p>
							Released by: Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan Kettering Cancer Center.
						</p>
					</td>
					<td width="20%" valign="top">
						[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/README.txt">Release Notes</a>]
						<br>
						[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/cpath.tar.gz">Download .tar.gz</a>]
						<br>
						[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/cpath.zip">Download .zip</a>]
					</td>
				</tr>
				<tr><td colspan=3><HR></TD></TR>
				<tr>
					<td width="20%" valign="top">
						<b>MCODE PlugIn</B><font size="-1">
							<br>
							Version: 1
							<br>
							Release Date: July 15, 2004</font>
					</td>
					<td width="60%" valign="top">
						The MCODE Cytoscape PlugIn finds clusters (highly interconnected regions) in any network loaded into Cytoscape.
						Depending on the type of network, clusters may mean different things. For instance, clusters in a protein-protein
						interaction network have been shown to be protein complexes and parts of pathways. Clusters in a protein similarity
						network represent protein families.
						<p>
							Released by: Gary Bader, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan Kettering Cancer Center.
						</p>
					</td>
					<td width="20%" valign="top">
						[<a href="http://www.cbio.mskcc.org/~bader/software/mcode/index.html">MCODE PlugIn Web Site</a>]
						<br>
						[<a href="http://www.cbio.mskcc.org/~bader/software/mcode/mcode_v1.zip">Download .zip</a>]
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<? include "footer.php"; ?>
<br>
</body>
</html>

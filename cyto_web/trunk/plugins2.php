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
		<a href="http://cytoscape.systemsbiology.net/Cytoscape2.0/plugin/pluginTutorial/pluginTutorial.html">Cytoscape 2.0 PlugIn Tutorial</A>,
and the 
<a href="http://cytoscape.systemsbiology.net/Cytoscape2.0/plugin/index.html">Cytoscape 2.0 PlugIn Writer Documentation</A>.
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
							Released by: Gary Bader, Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan Kettering Cancer Center.
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
  <br>

	</p>
		<big><b>Experimental Cytoscape 2.0 PlugIns:</b></big>
		<br>
	</p>
	<table style="margin-left: 30;  margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small"
		cellpadding=5 cellspacing=5 width=100% bgcolor="ebebff" >
			<tbody >
        
                                                                                                  


				<tr >
					<td width="20%" valign="top" >
						<b>Extras PlugIn</B><font size="-1">
							<br>
							Version: .1 BETA
							<br>
							Release Date: August 2, 2004</font>
					</td>
					<td width="60%" valign="top">
                                                                                          This PlugIn is a testing ground for what will likely later become seperate plugins.  Any code should be considered highly experimental and full of bugs. However, bugs that are reported will likely be fixed, though perhaps not in a timely fashion.  Currently this plugin provides the following:<br>
<ul><li>"destroy" -- tests removal of a netwwork and all nodes and edges not referenced by any other networks.</li>
                                                                                          <li>"Group" -- creates a box around a group of nodes that will always contain those nodes.  Operates on the selected nodes</li>
                                                                                          <li>"First Neighbors" -- Opens a dialog that allows you to add nodes and edges to a network from the pool of all nodes, or only from a selected network, filters are optional.</li>
                                                                                          </ul>
                                                                                          <p>
							Released by: Rowan Christmas, Aitchison Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
						</p>
					</td>
					<td width="20%" valign="top">
					 [<a href="ftp://baker.systemsbiology.net/pub/xmas/plugins/rowan.jar">Download PlugIn .jar</a>]
					</td>
				</tr>                                                                                      
    	<tr><td colspan=3><HR></TD></TR>

				<tr>
					<td width="20%" valign="top">
						<b>Save PlugIn</B><font size="-1">
							<br>
							Version: .1 BETA
							<br>
							Release Date: August 3, 2004</font>
					</td>
					<td width="60%" valign="top">
                                                                                          This plugin implements the saving of Cytoscape session. Specifically it saves all of the networks that you have open, and the layout of all the networks that have views.  It also saves all of the data for all the nodes and edges in your networks, even those nodes and edges that are not in any network.  It saves files as a zip file, and the contents are easily inspected after unzipping.  The files "nodes.txt" and "edges.txt" contain all of the data for nodes and edges. <br><br>
Also included is a spreadsheet importer.
						<p>
							Released by: Rowan Christmas, Aitchison Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
						</p>
					</td>
					<td width="20%" valign="top">
					 [<a href="ftp://baker.systemsbiology.net/pub/xmas/plugins/loader.jar">Download PlugIn .jar</a>]<br>
           [<a href="ftp://baker.systemsbiology.net/pub/xmas/plugins/course.cytoproj.zip">Download Test Project</a>]
					</td>
				</tr>        
					</td>
				</tr>                                                                                      
    	<tr><td colspan=3><HR></TD></TR>

				<tr>
					<td width="20%" valign="top">

						<b>DataMatrix PlugIn</B><font size="-1">
							<br>
							Version: .8 BETA
							<br>
							Release Date: August 15, 2004</font>
					</td>
					<td width="60%" valign="top">
This plugin provides a number of integrated tools for exploring and visualizing experimental data in association with the Cytoscape network view.  
Read in tab-delimited text files, in which there is one row for each gene or protein, and as many columns as there are experimental conditions.  The following operations are then possible:

<ol>
  <li> examine the numerical data in a spreadsheet display
  <li> view x-y plots of selected rows
  <li> find other rows (other genes) with correlated profiles
  <li> selectively enable or disable columns of data
  <li> get selections from, or propogate selections to, the Cytoscape network
  <li> animate the Cytoscape network based upon the experimental data, mapping (for instance) log10 ratios to node color, and statistical
       significance to node size
  <li> for the skilled user:  use the python console, and write scripts to create custom operations on, or selections from, the data
</ol>
Full documentation is <a href="http://db.systemsbiology.org/cytoscape/tutorial/cy2/DataMatrixPlugin"> here</a>.

						<p>
							Released by: Paul Shannon, Baliga Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
						</p>
					</td>
					<td width="20%" valign="top">
					 [<a href="http://db.systemsbiology.net/cytoscape/ftp/DataMatrixPlugin.jar">Download PlugIn .jar</a>]<br>
					</td>
				</tr>        
<!----------------------
    	<tr><td colspan=3><HR></TD></TR>

				<tr>
					<td width="20%" valign="top">

						<b>Python Console PlugIn</B><font size="-1">
							<br>
							Version: .1 BETA
							<br>
							Release Date: August 12, 2004</font>
					</td>
					<td width="60%" valign="top">
For the skilled user, this plugin adds scripting to Cytoscape.  <a href="http://www.python.org">Python</a> is a very popular language,
not unlike Perl, but with (many of us feel) a much more natural syntax.  We have just begun to explore the possibilities Python
adds to Cytoscape; an inventory of these, along with demonstrations and tutorials will shortly appear below.

						<p>
							Released by: Paul Shannon, Baliga Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
						</p>
					</td>
					<td width="20%" valign="top">
					 [<a href="http://db.systemsbiology.net/cytoscape/ftp/PythonConsolePlugin.jar">Download PlugIn .jar</a>]<br>
					</td>
				</tr>        
---------------------->
      </tbody>
		</table>

  <br>
	<p>                                                                      


	</div>
</div>
<? include "footer.php"; ?>
<br>
</body>
</html>

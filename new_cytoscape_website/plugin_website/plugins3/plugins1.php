<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<meta name="robots" content="noindex" />
	<title>Cytoscape 1.1 PlugIns</title> 
	<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css">
	<link rel="shortcut icon" href="images/cyto.ico">
</head>
<body bgcolor="#ffffff">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tbody>
		<tr>
			<td width="10">&nbsp;
				 
			</td>
			<td valign="center">
				<h1>Cytoscape 1.1 PlugIns</h1> 
			</td>
		</tr>
	</tbody>
</table>
<? include "nav.php"; ?>
<br>
<div id="indent">
	<big><b>About Cytoscape PlugIns:</b></big> 
	<p>
		Cytoscape includes a flexible PlugIn architecture that enables developers to add extra functionality beyond that provided in the core. PlugIns also provide a convenient place for testing out new Cytoscape features. </p> 
	<p>
		<b>Note that PlugIns on this page will only work with Cytoscape 1.1. Refer to the current plugin page for a list of plugins registered with the plugin manager</b> <a href="http://cytoscape.org/plugins/index.php">Current Cytoscape 2.x Plugins</a>. 
	</p>
	<p><big><b>PlugIn License Policy:</b></big>
	<P>
	Although the Cytoscape core
application is distributed under a Library GNU Public License (LGPL),
plugins are separate works which use Cytoscape as a Java code library.
Plugins are therefore governed by independent software licenses
distributed with and specific to each plugin.  The Cytoscape project
has no intent to capture plugins under the license terms of the core
Cytoscape LGPL.	
	<p>
		<big><b>Current Cytoscape 1.1 PlugIns:</b></big> 
		<br>
	</p>
	<table style="margin-left: 30;  margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" width="95%" cellpadding="5" cellspacing="5" bgcolor="#ebebff">
		<tbody>
			<tr>
				<td width="20%" valign="top">
					<b>Biomodules</b> <font size="-1"> 
						<br>
						Version: 1.0 
						<br>
						Release Date: March 1, 2004 </font> 
				</td>
				<td width="40%" valign="top">
					<p>
						Biological modules are loose associations of preferred molecular interaction partners that perform a collective function. <i>Biomodules</i> is a Cytoscape plugin that identifies modules in molecular networks and analyzes their expression patterns and biological functions. This information is integrated, visualized, and analyzed in an interactive graphical representation. 
					</p>
					<p>
						Released by: <a href="http://labs.systemsbiology.net/galitski">Galitski Lab</a> at the <a href="http://www.systemsbiology.org">Institute for Systems Biology</a> 
					</p>
				</td>
				<td width="20%" valign="top">Verified to work in Cytoscape 1.1</td>				
				<td width="20%" valign="top">
					[<a href="http://labs.systemsbiology.net/galitski/projs/biomodules/index_biomodules.html">Biomodules Home Page</a>]<b> 
					</td>
				</b> 
			</td>
		</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>		
			<tr>
				<td width="20%" valign="top">
					<b>Data Services Plug-In</b> <font size="-1"> 
						<br>
						Version: 0.04 
						<br>
						Release Date: September 5, 2003 </font> 
				</td>
				<td width="40%" valign="top">
					This PlugIn enables Cytoscape to import/export to multiple file formats. 
					<p>
						The current release includes support for: 
					</p>
					<ul>
						<li>
							<a href="http://psidev.sourceforge.net/">Proteomics Standards Initiative Molecular Interaction (PSI-MI) XML Format.</a> 
						</li>
						<li>
							<a href="http://www.ncbi.nlm.nih.gov/projects/geo/info/soft2.html">NCBI GEO Simple Omnibus Format in Text (SOFT). </a> 
						</li>
					</ul>
					<p>
						This product includes software developed by the Apache Software Foundation (<a href="http://www.apache.org/">http://www.apache.org</a>). 
					</p>
					<p>
						Released by: Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan Kettering Cancer Center. 
					</p>
				</td>
				<td width="20%" valign="top">Verified to work in Cytoscape 1.1.1</td>
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/README.txt">Release Notes</a>] 
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/ds_plugin_04.tar.gz">Download .tar.gz</a>] 
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/ds_plugin_04.zip">Download .zip</a>] 
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

			<tr>
				<td width="20%" valign="top">
					<b>Expression Data Viewers</b> <font size="-1"> 
						<br>
						Version: 1.0 
						<br>
						Release Date: October 13, 2003 </font> 
				</td>
				<td width="40%" valign="top">
					This package contains two plugins. The first enables the user to view the expression profiles of selected genes in a Cytoscape network across all the experimental conditions. The second enables the user to find genes with a high degree of correlation with a node or set of nodes of interest. 
					<p>
						This package includes the Visad library (http://www.ssec.wisc.edu/~billh/visad.html). 
					</p>
					<p>
						Released by: <a href="http://www.systemsbiology.org">Institute for Systems Biology</a> 
					</p>
				</td>
				<td width="20%" valign="top">Verified to work in Cytoscape 1.1</td>				
				<td width="20%" valign="top">
					[<a href="http://db.systemsbiology.net:8080/cytoscape/plugins/expDataViewers/README.txt">Release Notes</a>] 
					<br>
					[<a href="http://db.systemsbiology.net:8080/cytoscape/plugins/expDataViewers/license.txt">License</a>] 
					<br>
					[<a href="http://db.systemsbiology.net:8080/cytoscape/plugins/expDataViewers/ExpDataViewers.jar">Download</a>] 
				</td>
			</tr>	
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>			
			<tr>
				<td width="20%" valign="top">
					<b>jActiveModules</b> <font size="-1"> 
						<br>
						Version: 1.0 
						<br>
						Release Date: February 20, 2004 </font> 
				</td>
				<td width="40%" valign="top">
					This plugin enables Cytoscape to search for significant networks as described in <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=12169552&amp;dopt=Abstract">Bioinformatics. 2002 Jul;18 Suppl 1:S233-40</a>. 
					<p>
						This product was developed using tools from the Apache Software Foundation (<a href="http://www.apache.org/">http://www.apache.org</a>). 
					</p>
					<p>
						Released by: <a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">The Ideker Lab</a>, Department of Bioengineering, UCSD 
					</p>
				</td>
				<td width="20%" valign="top">Verified to work in Cytoscape 1.1</td>
				<td width="20%" valign="top">
					[<a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/plugins/jActiveModules/README.txt">Release Notes</a>] 
					<br>
					[<a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/plugins/jActiveModules/jActiveModules.jar">Download</a>] 
					<br>
					[<a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/plugins/jActiveModules/jActiveModules.tgz">Source</a>] 
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>
			<tr>
				<td width="20%" valign="top">
					<b>pathBLAST</b> <font size="-1"> 
						<br>
						Version: 1.0 
						<br>
						Release Date: March 5, 2004 </font> 
				</td>
				<td width="40%" valign="top">
					This plugin to Cytoscape automates the process of aligning two protein-protein interaction networks and mining for evolutionarily conserved pathways, as described in <a href="http://www.pnas.org/cgi/content/abstract/100/20/11394">PNAS 2003 100: 11394-11399</a>. 
					<p>
						Released by: <a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">The Ideker Lab</a>, Department of Bioengineering, UCSD 
					</p>
				</td>
				<td width="20%" valign="top">Verified to work in Cytoscape 1.1</td>				
				<td width="20%" valign="top">
					[<a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/plugins/pathBlast/PathBlastManual.pdf">Release Notes</a>] 
					<br>
					[<a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/plugins/pathBlast/pathBlast.jar">Download</a>] 
					<br>
					[<a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/plugins/pathBlast/PathBlastTutorial.pdf">Tutorial</a>] 
					<br>
					[<a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/plugins/pathBlast/tutorialFiles.tar">Tutorial Files</a>] 
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>
			<tr>
				<td width="20%" valign="top">
					<b>SBML Reader</b> <font size="-1"> 
						<br>
						Version: 1.0 
						<br>
						Release Date: October 3, 2003 </font> 
				</td>
				<td width="40%" valign="top">
					This plugin allows you to load a level 1 <a href="http://sbml.org/"> SBML</a> file from Cytoscape's File menu, and to display the model as a Cytoscape network. You can also <a href="http://db.systemsbiology.net:8080/cytoscape/projects/static/sbmlReader/v1/cy.jnlp">run</a> Cytoscape with this plugin using Java Web Start, &nbsp; or view a <a href="http://db.systemsbiology.net:8080/cytoscape/plugins/sbmlReader/repressilator.jpg" target="aaa">snapshot</a> of a sample SBML model of the Repressilotor System (<a href="http://sbml.org/models/Models-published/Genetic-2000Elo/Genetic-2000Elo.html" target="aaa">Elowitz &amp; Leibler, 2000)</a>. 
					<p>
						Released by: <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a> 
					</p>
				</td>
				<td width="20%" valign="top">Verified to work in Cytoscape 1.1</td>				
				<td width="20%" valign="top">
					[<a href="http://db.systemsbiology.net:8080/cytoscape/plugins/sbmlReader/releaseNotes.html">Release Notes</a>] 
					<br>
					[<a href="http://db.systemsbiology.net:8080/cytoscape/plugins/sbmlReader/SBMLReader.jar">Download</a>] 
				</td>
			</tr>

	</tbody>
</table>
</div>
<? include "footer.php"; ?>
<br>
</body>
</html>

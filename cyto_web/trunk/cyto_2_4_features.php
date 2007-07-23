<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<! Created by kono@ucsd.edu for Cytoscape 2.4 release. >

<html>

	<head>
		<title>Cytoscape 2.4 Release Notes</title>
		<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css">
		<link rel="shortcut icon" href="images/cyto.ico">
	</head>

	<body bgcolor="#FFFFFF">
	<div id="topbar">
	    <div class="title">Cytoscape 2.4 Release Notes</div>
		</div>

		<? include "nav.php"; ?>
		<div id="indent">
			<p>Cytoscape version 2.4 is the latest release of the open source bioinformatics software platform for visualizing general networks, especially for molecular interaction networks.  Cytoscape can integrating these networks with any attributes, such as gene expression profiles and other state data.</p>
			<p>The 2.4 release of Cytoscape includes the following new features:</p>
			<ul>
				<li>
				<li><p><strong>Publication quality image generation</strong>. This includes:</p>
					<ul>
						<li>Node label position adjustment.
						<li>Automatic Visual Legend generator.
						<li>Node position fine-tuning by arrow keys.
						<li>The ability to override selected VizMap settings.
					</ul>
				<li><p><em><strong>Quick Find</strong></em> plugin.</p>
				<li><p><strong>New Cytoscape icons</strong> for a cleaner user interface.</p>
				<li><p><strong>Consolidated network import capabilities</strong>.</p>
					<ul>
						<li>Import network from remote data sources (through http or ftp).	
						<li>Default support for the following file formats:
							<ul>
								<li><a href="http://sbml.org/index.psp">Systems Biology Markup Language (SBML)</a>
								<li><a href="http://www.biopax.org/">BioPAX Level 1 and 2</a>
								<li><a href="http://psidev.sourceforge.net/mi/xml/doc/user/">PSI-MI (Level 1 and 2.5)</a>
								<li>Delimited text table (TAB delimited text file, CSV, etc.)
								<li>Excel (<em>.xls</em>) format file.						
							</ul>
					</ul>
				<li>
					<p><strong>New Ontology Server</strong>. This will eventually replace the BioDataServer. New Ontology Server features include:</p>
					<ul>
						<li>Native support for OBO format ontology files. Users can import many different ontologies in the OBO format.
						<li>Ability to visualize the ontology tree as a network (DAG).
						<li>Full support for Gene Association files.
					</ul>
				<li><p><strong>Support for Java SE 5</strong></p>.
				<li>Many, many bug fixes!
			</ul>
			
			<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
				<tr>
					<td><big><b>Publication quality image generation</b></big>
						<p>
							<ul>
								<li>Node label position adjustment - Node label position can be adjusted through graphical user interface.
								<li>Automatic Visual Legend generator - Visual legend can be created from VizMapper setting.
								<li>Node position fine-tuning by arrow keys
								<li>The ability to override selected VizMap settings - User can override VizMap from network view.
							</ul>
						</p>
					</td>
				</tr>
			</table>
			<p></p>
			<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
				<tr>
					<td><big><b>Quick Find Plugin</b></big>
						<p>
							Quick Find plugin is a simple selection plugin that enables users to quickly find nodes and edges through simple user interface.  You can interactively select nodes and edges using attributes.
						</p>
					</td>
				</tr>
			</table>
			<p></p>
			<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
				<tr>
					<td><big><b>New Cytoscape Icons</b></big>
						<p>
							Icons are replaced by new OpenOffice icon set for cleaner look. 
						</p>
					</td>
				</tr>
			</table>
			<p></p>
			<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
				<tr>
					<td><big><b>Consolidated network import capabilities</b></big>
						<p>
							<ul>
								<li>Import network from remote data sources (through http or ftp) - Users can load local and remote files seamlessly.	
								<li>Default support for popular file formats for intaraction data:
								<ul>
									<li>Standard XML formats
									<ul>
										<li><a href="http://sbml.org/index.psp">Systems Biology Markup Language (SBML)</a>
										<li><a href="http://www.biopax.org/">BioPAX Level 1 and 2</a>
										<li><a href="http://psidev.sourceforge.net/mi/xml/doc/user/">PSI-MI (Level 1 and 2.5)</a>
									</ul>
									<li>Free-Format text tables - Users can import both networks and attributes from these files:
									<ul>
										<li>Delimited text table (TAB delimited text file, CSV, etc.)
										<li>Excel (<em>.xls</em>) format file.
									</ul>						
								</ul>
							</ul>
						</p>
					</td>
				</tr>
			</table>
			<p></p>
			<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
				<tr>
					<td><big><b>New Ontology Server</b></big>
						<p>
							New Ontology server will eventually replace BioDataServer.  Instead of using special data structure, ontology data is stored in normal CyNetwork data structure.  Plugin writers can access this function through CyNetwork API or BioJava Ontology API.
							<ul>
								<li>Native support for OBO format ontology files - Users can import arbitrary OBO ontology file.  Both format version 1.0 and 1.2 are supported.
								<li>Ability to visualize the ontology tree as a network (DAG) - Since ontology uses CyNetwork data object, users and plugin writers can use Cytoscape's visualization functions for ontology DAGs. 
								<li>Full support for Gene Association files - All data in Gene Association files are imported directly into attributes.
							</ul>
						</p>
					</td>
				</tr>
			</table>
			<p></p>
			<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
				<tr>
					<td><big><b>Support for Java SE 5</b></big>
						<p>
							Cytoscape core uses new features introduced in Java SE 5.  Plugin writers can use those features. 
						</p>
					</td>
				</tr>
			</table>
			
			<p></p>
			<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
				<tr>
					<td><big><b>Core Plugins:</b></big>
						<p>Seven &quot;core&quot; plugins are bundled and distributed with Cytoscape 2.4. These plugins offer fundamental operations of value to many users, and are included in the basic distribution.</p>
						<center>
							<table style="margin-left: 30;margin-right:30;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
								<tr>
									<td>
										<p><b>Plugin Name</b></p>
									</td>
									<td width="172">
										<p><b>JAR file</b></p>
									</td>
									<td style="width: 281px;">
										<p><b>Description</b></p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>Filter</p>
									</td>
									<td width="172">
										<p>filter.jar</p>
									</td>
									<td width="281">
										<p>Provides filtering functionality; adds filter icon, filters menu and filtering dialog box.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>Attribute Browser</p>
									</td>
									<td width="172">
										<p>browser.jar</p>
									</td>
									<td width="281">
										<p>Provides attribute browser functionality; adds browser menu item under Data.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>Cytoscape Editor</p>
									</td>
									<td width="172">
										<p>CytoscapeEditor.jar</p>
									</td>
									<td width="281">
										<p>Provides network editor functionality; adds setEditor menu item under File.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>cPath</p>
									</td>
									<td width="172">
										<p>cpath.jar</p>
									</td>
									<td width="281">
										<p>Provides aility to directly query, retrieve and visualize interactions retrieved from the <a href="http://cbio.mskcc.org/cpath/cytoscape.do" target="_blank">cPath</a> database.</p>
									</td>
								</tr>
								<tr valign="top">
									<td style="width: 132px;">
										<p>yFiles Layouts</p>
									</td>
									<td width="172">
										<p>yLayouts.jar</p>
									</td>
									<td style="width: 281px;">
										<p>Provides yFiles layouts functionality; adds yFiles submenu to layout menu.</p>
											Layouts provided:<br>
											- Circular<br>
											- Organic<br>
											- Hierarchic<br>
											- Random<br>
											- MirrorX<br>
											- MirrorY<br>
											- Orthogonal<br>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>Linkout</p>
									</td>
									<td width="172">
										<p>linkout.jar</p>
									</td>
									<td width="281">
										<p>Provides a right-click menu of hyper links for nodes and edges.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>Graph Merge</p>
									</td>
									<td width="172">
										<p>GraphMerge.jar</p>
									</td>
									<td width="281">
										<p>Provides ability to merge networks and perform other set operations on networks.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>Manual Layout</p>
									</td>
									<td width="172">
										<p>ManualLayout.jar</p>
									</td>
									<td width="281">
										<p>Provides align functionality.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>Automatic Layout</p>
									</td>
									<td width="172">
										<p>AutomaticLayout.jar</p>
									</td>
									<td width="281">
										<p>Provides layout algorithms shown under menu items <i>JGraph Layouts</i> and <i>Cytoscape Layouts</i>. The code for these algorithms is all open source.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>Yeast context-sensitive menu</p>
									</td>
									<td width="172">
										<p>yeast-context.jar</p>
									</td>
									<td width="281">
										<p>Provides Yeast-specific web search capabilities.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>BioPax Import</p>
									</td>
									<td width="172">
										<p>biopax.jar</p>
									</td>
									<td width="281">
										<p>Plugin for importing network files in BioPAX format.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>SBML Import</p>
									</td>
									<td width="172">
										<p>SBMLReader.jar</p>
									</td>
									<td width="281">
										<p>Plugin for importing network files in SBML format.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>PSI-MI Import</p>
									</td>
									<td width="172">
										<p>psi_mi.jar</p>
									</td>
									<td width="281">
										<p>Plugin for importing network files in PSI-MI format.  Supports PSI-MI version 1.0 and 2.5.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>Table Import</p>
									</td>
									<td width="172">
										<p>TableImport.jar</p>
									</td>
									<td width="281">
										<p>Plugin for importing network and attribute files from text table and Excel Workbook.</p>
									</td>
								</tr>
								<tr valign="top">
									<td width="132">
										<p>Quick Find</p>
									</td>
									<td width="172">
										<p>quick_find.jar</p>
									</td>
									<td width="281">
										<p>Quick Find plugin.</p>
									</td>
								</tr>
							</table>
						</center>
					</td>
				</tr>
			</table>
		</div>
		<? include "footer.php"; ?>
	</body>

</html>

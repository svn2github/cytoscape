<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<title>Cytoscape 2.x Plugins</title>
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
			<td valign="bottom">
				<h1>Cytoscape 2.x Plugins</h1>
			</td>
		</tr>
	</tbody>
</table>
<? include "nav.php"; ?>
<br>
<div id="indent">
	<big><b>About Cytoscape Plugins:</b></big>
	<p>
		Cytoscape includes a flexible Plugin architecture that enables developers to add extra functionality beyond that provided in the core. Plugins also provide a convenient place for testing out new Cytoscape features. As more Plugins become available, they will be listed on this page, and posted to our <A HREF="http://groups-beta.google.com/group/cytoscape-announce">cytoscape-announce</A> mailing list.
    </p>
	<p>
	If you are interested in building your own Cytoscape Plugin, check out the <a href="pluginTutorial.php">Cytoscape Plugin Tutorial</a>, and the <a href="http://cytoscape.systemsbiology.net/Cytoscape2.0/plugin/index.html">Cytoscape Plugin Writer Documentation</a>.  We also maintain a list of <a href="plugins1.php">Cytoscape 1.1 Plugins</a>.
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
		<big><b>Current Cytoscape 2.x Plugins:</b></big>
		<br>
	</p>
</div>
<div id="indent">
	<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%" bgcolor="#ebebff">

		<tbody>
			<tr>
				<td width="20%" valign="top">
					<b>Agilent Literature Search</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: March 9, 2005</font>
				</td>
				<td width="40%" valign="top">
					
					<p><em>Agilent Literature Search</em> is a meta-search tool for automatically querying multiple text-based search engines in order to aid biologists faced with the daunting task of manually searching and extracting associations among genes/proteins of interest.  Computationally extracted associations are grouped into a network that is viewed and manipulated in Cytoscape.
					<p>The meta-search engine peforms Information Retrieval (IR) and Knowledge Extraction (KE),
					using PubMed, OMIM, and USPTO search engines
					to identify symbols, extract interactions, and
					generate putative networks from literature.
					
<p><em>Agilent Literature Search</em> provides an easy-to-use interface to its powerful querying capabilities. When a query is entered, it is submitted to multiple user-selected search engines, and the retrieved results (documents) are fetched from their respective sources. Each document is then parsed into sentences and analyzed for associations between biological constructs, such as protein-protein associations. 
<!-- <p><em>Agilent Literature Search</em> uses a set of lexicons for defining biomolecule names (and aliases) and association terms (verbs) of interest. -->
Associations extracted from these documents are then converted into interactions, which are further grouped into a network. 
The sentences and source hyperlinks for each association are further stored as attributes of the corresponding nodes and links in the network.
The networks can be viewed and manipulated in Cytoscape v2.1.


					
					
					
					<p>	Released by:  
						<a href="http://www.labs.agilent.com/research/mtl/projects/sysbio.html">Systems Biology project</a>, 
						<a href="http://www.labs.agilent.com/">Agilent Laboratories</a>,.
						<a href="http://www.agilent.com/">Agilent Technologies</a>.  
<!---                                        <p>     <a href="http://www.chem.agilent.com/scripts/PHome.asp">Agilent Technologies Life Science Products</a>  --->
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.1.
				</td>					
				<td width="20%" valign="top">
					[<a href="http://www.labs.agilent.com/research/mtl/projects/sysbio/sysinformatics/litsearch.html">Agilent Literature Search Web Site</a>]
					<br>
					[<a href="http://www.labs.agilent.com/research/mtl/projects/sysbio/sysinformatics/download.html">Download page</a>]
					<br>
					<br>
					<br>
					<br><br>
					<br>
					<br>
					<br>
					[<a href="http://www.labs.agilent.com/research/mtl/projects/sysbio/golsca.html">Agilent Technologies Life Science Products</a>]
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>		
			<tr>
				<td width="20%" valign="top">
					<b>cPath Plugin</b> <font size="-1">
						<br>
						Version: 2 Beta
						<br>
						Release Date: November 9, 2004</font>
				</td>
				<td width="40%" valign="top">
					The cPath Plugin enables Cytoscape users to query, retrieve and visualize interactions retrieved from the <a href="http://cbio.mskcc.org/cpath">cPath database</a>.  For a complete list of new features / bug fixes in the Beta 2 release,
please refer to the <a href="http://www.cbio.mskcc.org/cytoscape/cpath/beta2/README.txt">Release Notes</a>.

					<p>
						This product includes software developed by the Apache Software Foundation (<a href="http://www.apache.org/">http://www.apache.org</a>).
					</p>
					<p>
						Released by: Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.0, 2.1.
				</td>
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/beta2/README.txt">Release Notes</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.tar.gz">Download .tar.gz</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.zip">Download .zip</a>]
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>			
			<tr>
				<td width="20%" valign="top">
					<b>CytoTalk Plugin</b>
					<br>
					<font size="-1">Version: .1 BETA
						<br>
						Release Date: Sept. 3, 2004</font>
				</td>
				<td width="40%" valign="top">
					Dynamically interact with and manipulate the current network from an external process.
					<br>
					<br>
					This plugin runs a simple internal XML-RPC server from within Cytoscape that allows the current network and its various attributes to be manipulated from an external process that is XML-RPC capable. Examples include external <a href="http://perl.org">Perl</a>, <a href="http://python.org">Python</a>, <a href="http://www.r-project.org">the R statistical language</a>, UNIX shell scripts, C or C++ programs, or external Java processes. It even allows for other "plugins" to be written in these languages. The external process may be run on the same machine as Cytoscape, or anywhere else on the network.
					<ul>
						<li>
							<a href="http://db.systemsbiology.net/cytoscape/projects/static/dreiss/cytoTalk/CytoTalkHandler.html"> JavaDoc documentation of the client functions are available.</a></li>
							<li>
								<a href="http://db.systemsbiology.net/cytoscape/projects/static/dreiss/cytoTalk/CytoTalkClient.R">R</a> and <a href="http://db.systemsbiology.net/cytoscape/projects/static/dreiss/cytoTalk/CytoTalkClient.pm">Perl</a> client classes are also available. </li>
							<li>
								Some simple <a href="http://db.systemsbiology.net/cytoscape/projects/static/dreiss/cytoTalk/examples"> example Perl, Python, and R scripts</a> have been written. </li>
			</ul>
			Released by: David J. Reiss, Schwikowski Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
		</td>
		<td width="20%" valign="top">
				 Verified to work in 2.0, 2.1.
				</td>			
		<td width="20%" valign="top">
			[<a href="http://db.systemsbiology.net/cytoscape/projects/static/dreiss/cytoTalk/cytoTalk.jar">Download Plugin .jar</a>]
			<br>
		</td>
	</tr>			
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>
			<tr>
				<td width="20%" valign="top">
					<b>DataMatrix Plugin</b><font size="-1">
						<br>
						Version: .8 BETA
						<br>
						Release Date: August 15, 2004</font>
				</td>
				<td width="40%" valign="top">
					This plugin provides a number of integrated tools for exploring and visualizing experimental data in association with the Cytoscape network view. Read in tab-delimited text files, in which there is one row for each gene or protein, and as many columns as there are experimental conditions. The following operations are then possible:
					<ol>
						<li>examine the numerical data in a spreadsheet display</li>
						<li>view x-y plots of selected rows</li>
						<li>find other rows (other genes) with correlated profiles</li>
						<li>selectively enable or disable columns of data</li>
						<li>get selections from, or propagate selections to, the Cytoscape network</li>
						<li>animate the Cytoscape network based upon the experimental data, mapping (for instance) log10 ratios to node color, and statistical significance to node size</li>
						<li>for the skilled user: use the python console, and write scripts to create custom operations on, or selections from, the data</li>
					</ol>
					Full documentation is <a href="http://db.systemsbiology.net/cytoscape/tutorial/cy2/DataMatrixPlugin"> here</a>.
					<p>
						Released by: Paul Shannon, Baliga Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
					</p>
				</td>
				<td width="20%" valign="top">
				 Verified to work in 2.0. <A HREF="#more_info"><BR>Not tested in Cytoscape 2.1*</A>
				</td>	
				<td width="20%" valign="top">
					[<a href="http://db.systemsbiology.net/cytoscape/ftp/DataMatrixPlugin.jar">Download Plugin .jar</a>]
					<br>
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>			
			<tr>
				<td width="20%" valign="top">
					<b>Expression Correlation Network Plugin</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: January 19, 2005</font>
				</td>
				<td width="40%" valign="top">
					<p>
					This plugin enables Cytoscape users to correlate genes or conditions in an expression matrix file loaded into Cytoscape. The resulting correlations are visualized as a network in Cytoscape.  A condition correlation network is an alternate way of representing expression condition clustering results which can sometimes make it easier to notice clusters compared to the normal 'heat-map' view.
					<p>
						Released by: Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.0, 2.1.
				</td>					
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/plugins/ExpressionCorrelationReadme.txt">Release Notes</a>]
					[<a href="http://www.cbio.mskcc.org/cytoscape/plugins/ExpressionCorrelation.zip">Download</a>]
				</td>
			</tr>	
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>			
			<tr>
				<td width="20%" valign="top">
					<b>Graph Merge Plugin</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: February 25, 2005</font>
				</td>
				<td width="40%" valign="top">
					<p>
					This plugin enables Cytoscape to merge two or more networks into one.
					<p>
						Released by: <a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">The Ideker Lab</a>, Department of Bioengineering, UCSD
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.1.
				</td>					
				<td width="20%" valign="top">
					[<a href="/plugins/GraphMerge/README.txt">Release Notes</a>]
					[<a href="/plugins/GraphMerge/GraphMerge.jar">Download</a>]
				</td>
			</tr>	
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>			
			<tr>
				<td width="20%" valign="top">
					<b>HTTP Data and
						<br>
						Interaction Fetcher
						<br>
						Plugins</b>
					<br>
					<font size="-1">Version: .1 BETA
						<br>
						Release Date: Sept. 3, 2004</font>
				</td>
				<td width="40%" valign="top">
					Dynamically retrieve remote biological information for selected nodes in the current network.
					<br>
					<br>
					This plugin fetches and adds biological information from a remote server/database and adds them as additional edges or attributes to the current network. Currently implemented data include: protein/gene synonyms, orthologs, sequences (gene/protein/upstream) and interactions/associations. This information is available to varying degrees for several species including yeast, human, mouse, fruit fly, and worm. Some of this information is integrated, for example, retrieved synonym information may be used to expand the possible interactions that are retrieved, and retrieved ortholog information may be used to fetch interlogs for the species listed above. Available interaction data sets include <a href="http://www.blueprint.org/bind/bind.php">BIND</a>, <a href="http://dip.doe-mbi.ucla.edu">DIP</a>, <a href="http://hprd.org">HPRD</a>, <a href="http://www.blueprint.org/products/prebind/prebind.html">PreBIND</a>, and several others.
					<br>
					<br>
					Additional notes:
					<ul>
						<li>
							Some data sources are currently not allowed for "public use" (such as HPRD which is free for non-profit use only) and will require a password to allow access. Please email me for a password and do not share it with anyone.
						</li>
						<li>
							A (somewhat outdated) tutorial for many of the features of this plugin are available <a href="http://db.systemsbiology.net/cytoscape/projects/static/dreiss/getInteractions"> here</a>. Note that the web start (and screenshots) on this tutorial page still use Cytoscape v1.1.1.
						</li>
						<li>
							The structure of the plugins were written to allow easy expansion of the type of information that may be fetched, and may be extended in the future to various types of annotations and public microarray datasets, for example.
						</li>
						<li>
							Also included in the plugin jar are all necessary classes required to run a local version of the information server (not including, of course, the database). The server may be run locally as a stand-alone XML-RPC server, or behind an external web server such as Apache Tomcat.
						</li>
					</ul>
					Released by: David J. Reiss, Galitski and Schwikowski Groups, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
				</td>
				<td width="20%" valign="top">
				 Verified to work in 2.0, 2.1.
				</td>					
				<td width="20%" valign="top">
					[<a href="http://db.systemsbiology.net/cytoscape/projects/static/dreiss/httpdata_all.jar">Download Plugin .jar</a>]
					<br>
				</td>
			</tr>			
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>			
	<tr>
		<td width="20%" valign="top">
			<b>jActiveModules</b>
			<br>
			<font size="-1">Version: 1.0 BETA
				<br>
				Release Date: Oct. 9, 2004</font>
		</td>
		<td width="40%" valign="top">
			This plugin enables Cytoscape to search for significant networks as described in <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=12169552&amp;dopt=Abstract">Bioinformatics. 2002 Jul;18 Suppl 1:S233-40.</a>
			<br>
			<br>
			Released by: <a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">The Ideker Lab</a>, Department of Bioengineering, UCSD
		</td>
 		<td width="20%" valign="top">
				 Verified to work in 2.0, 2.1.
				</td>	
		<td width="20%" valign="top">
			[<a href="/plugins/jActiveModules/README.txt">Release Notes</a>]
			<br>
			[<a href="/plugins/jActiveModules/jActiveModules.jar">Download Jar</a>]
			<br>
			[<a href="/plugins/jActiveModules/jActiveModules.tgz">Download Source</a>]
			<br>
		</td>
	</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>	
			<tr>
				<td width="20%" valign="top">
					<b>MCODE Plugin</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: July 15, 2004</font>
				</td>
				<td width="40%" valign="top">
					The MCODE Cytoscape Plugin finds clusters (highly interconnected regions) in any network loaded into Cytoscape. Depending on the type of network, clusters may mean different things. For instance, clusters in a protein-protein interaction network have been shown to be protein complexes and parts of pathways. Clusters in a protein similarity network represent protein families.
					<p>
						Released by: Gary Bader, Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.0, 2.1.
				</td>					
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/~bader/software/mcode/index.html">MCODE Plugin Web Site</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/~bader/software/mcode/mcode_v1.zip">Download .zip</a>]
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>
	<tr>
		<td width="20%" valign="top">
			<b>Motif Finder Plugin</b>
			<br>
			<font size="-1">Version: .1 BETA
				<br>
				Release Date: Sept. 3, 2004</font>
		</td>
		<td width="40%" valign="top">
			Run a Gibbs sampling motif detector on sequences corresponding to the selected nodes in the current network. This currently implements the most basic of the motif detection algorithms available from the <a href="http://sf.net/projects/netmotsa">Gibbs sampling motif detection library</a> described in <a href="http://bioinformatics.oupjournals.org/cgi/content/abstract/20/suppl_1/i274">Bioinformatics</a>.
			<br>
			This plugin requires that the "sequence fetcher" (part of the HTTP Data plugin) be run first, to fetch the sequences, or they may be pre-loaded as node attributes. It may be used on protein or DNA sequences (detected automatically). Various types of information such as motif logos, alignment tables, and motif positions are displayed at the end of the detection run (<a href="http://db.systemsbiology.net/cytoscape/projects/static/dreiss/motifFinder/software.jpg">screenshot</a>).
			<br>
			<br>
			At some point, I hope to implement the fully network-informed version of the motif finder as described in the article.
			<br>
			<br>
			Released by: David J. Reiss, Schwikowski Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
		</td>
		<td width="20%" valign="top">
				 Verified to work in 2.0, 2.1.
				</td>			
		<td width="20%" valign="top">
			[<a href="http://db.systemsbiology.net/cytoscape/projects/static/dreiss/motifFinder/motifFinder.jar">Download Plugin .jar</a>]
			<br>
		</td>
	</tr>		
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>	
			<tr>
				<td width="20%" valign="top">
					<b>Oracle Spatial Network Data Model Plugin</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: November 30, 2004</font>
				</td>
				<td width="40%" valign="top">
					<p>
					This plugin enables Cytoscape users to visualize and analyze network data stored in
					Oracle Spatial Network Data Model. In order to use the plugin, users need to install
					<a href="http://www.oracle.com/technology/products/database/oracle10g/index.html">Oracle Database 10g</a>
					(with the <a href="http://www.oracle.com/technology/products/spatial/index.html">Spatial option</a>) and Cytoscape.
					<p>
						Released by: <a href="http://www.oracle.com/technology/industries/life_sciences/index.html">Life Sciences Group, Oracle Corporation</a>
					</p>
				</td>
				<td width="20%" valign="top">
				 Verified to work in 2.0, 2.1.</A>
				</td>					
				<td width="20%" valign="top">
					[<a href="http://www.oracle.com/technology/industries/life_sciences/ls_sample_code.html">Download</a>]
				</td>
			</tr>	
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>				
			<tr>
				<td width="20%" valign="top">
					<b>PSI-MI Import/Export Plugin</b><font size="-1">
						<br>
						Version: 2
						<br>
						Release Date: November 8, 2004</font>
				</td>
				<td width="40%" valign="top">
					<p>
					This plugin enables Cytoscape to import/export to the
					<A HREF="http://psidev.sourceforge.net/">Proteomics
					Standards Initiative Molecular Interaction (PSI-MI)</A> XML Format.
					PSI-MI is a XML format used to represent and exchange protein-protein
					interaction data.

					<P>For an earlier version of this Plugin, which
					works in Cytoscape 1.1, refer to the Data Services Plugin
					on the <A HREF="plugins1.php">Cytoscape 1.1 Plugins</A> page.
					<p>
						Released by: Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.0, 2.1.
				</td>				
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/psi/version2/README.txt">Release Notes</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/psi/version2/psi-2.tar.gz">Download .tar.gz</a>]
<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/psi/version2/psi-2.zip">Download .zip</a>]

				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>
			<tr>
				<td width="20%" valign="top">
					<b>Significant Attributes Plugin</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: February 25, 2005</font>
				</td>
				<td width="40%" valign="top">
					<p>
					This plugin enables Cytoscape to search for aggregation of attribute values in subnetworks.
					<p>
						Released by: <a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">The Ideker Lab</a>, Department of Bioengineering, UCSD
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.0, 2.1.
				</td>					
				<td width="20%" valign="top">
					[<a href="/plugins/SigAttributes/README.txt">Release Notes</a>]
					[<a href="/plugins/SigAttributes/SigAttributes.jar">Download</a>]
				</td>
			</tr>	
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>			
			<tr>
				<td width="20%" valign="top">
					<b>SOFT Import Plugin</b><font size="-1">
						<br>
						Version: 2
						<br>
						Release Date: November 8, 2004</font>
				</td>
				<td width="40%" valign="top">
					<p>
					This plugin enables Cytoscape to import data formatted in the
					<A HREF="http://www.ncbi.nlm.nih.gov/projects/geo/info/soft2.html">
					GEO Simple Omnibus Format in Text (SOFT)</A>.  SOFT is a text file
					format 	used to represent and exchange Gene Expression Data.
					<P>For an earlier version of this Plugin, which
					works in Cytoscape 1.1, refer to the Data Services Plugin
					on the <A HREF="plugins1.php">Cytoscape 1.1 Plugins</A> page.
					<p>
						Released by: Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.0, 2.1.
				</td>				
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/soft/version2/README.txt">Release Notes</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/soft/version2/soft-2.tar.gz">Download .tar.gz</a>]
<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/soft/version2/soft-2.zip">Download .zip</a>]

				</td>
			</tr>

		</tbody>
	</table>
	<div id="indent">
	<P>
			<A NAME="more_info">
			* If you have verified that the specified plugin works in 2.1, please send an email to <A HREF="http://groups-beta.google.com/group/cytoscape-discuss">cytoscape-discuss</A>, and we will update the web page.	
	</div>
</tbody>
</table>
<p>
<br>
<p></p>
</div>
<? include "footer.php"; ?>
<br>
</body>
</html>

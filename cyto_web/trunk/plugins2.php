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
						Version: 2.3
						<br>
						Release Date: June 27, 2006</font>
				</td>
				<td width="40%" valign="top">

					<p><em>Agilent Literature Search</em> is a meta-search tool for automatically querying multiple text-based search engines in order to aid biologists faced with the daunting task of manually searching and extracting associations among genes/proteins of interest.  Computationally extracted associations are grouped into a network that is viewed and manipulated in Cytoscape.
					<p>The meta-search engine peforms Information Retrieval (IR) and Knowledge Extraction (KE),
					using PubMed, OMIM, and USPTO search engines
					to identify symbols, extract interactions, and
					generates putative networks from literature.

<p><em>Agilent Literature Search</em> provides an easy-to-use interface to its powerful querying capabilities. When a query is entered, it is submitted to multiple user-selected search engines, and the retrieved results (documents) are fetched from their respective sources. Each document is then parsed into sentences and analyzed for associations between biological constructs, such as protein-protein associations.
<!-- <p><em>Agilent Literature Search</em> uses a set of lexicons for defining biomolecule names (and aliases) and association terms (verbs) of interest. -->
Associations extracted from these documents are then converted into interactions, which are further grouped into a network.
The sentences and source hyperlinks for each association are further stored as attributes of the corresponding nodes and links in the network.
The networks can be viewed and manipulated in Cytoscape.
<p>Enhancements in version 2.3 include
<ul>
<li> Improved file-based lexicon management, supporting arbitrary file names
<li> Cytoscape session load/save compatible
<li> Paged results view.
</ul>




					<p>	Released by:
						<a href="http://www.labs.agilent.com/research/mtl/projects/sysbio.html">Systems Biology project</a>,
						<a href="http://www.labs.agilent.com/">Agilent Laboratories</a>,.
						<a href="http://www.agilent.com/">Agilent Technologies</a>.
<!---                                        <p>     <a href="http://www.chem.agilent.com/scripts/PHome.asp">Agilent Technologies Life Science Products</a>  --->
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.3 (Agilent Literature Search 2.3).
				<br>
				Verified to work in 2.1, 2.2 (Agilent Literature Search 2.0).
				
				</td>
				<td width="20%" valign="top">
<!-- 				[<a href="http://www.labs.agilent.com/research/mtl/projects/sysbio/sysinformatics/litsearch.html">Agilent Literature Search Web Site</a>]
					<br>
				[<a href="http://www.agilent.com/labs/research/mtl/projects/sysbio/sysinformatics/downloadv2.html">Download Agilent Literature Search version 2.0</a>]
					<br>
					[<a href="http://www.agilent.com/labs/research/mtl/projects/sysbio/sysinformatics/downloadv1.html">Download Agilent Literature Search version 1.0</a>]
-->					<br>
					<br>
				[<a href="http://www.cytoscape.org/download_agilent_literature_search_v2.3.php?file=litsearch_v2.3">Download Agilent Literature Search version 2.3 for Cytoscape v2.3 </a>]
				<br>					<br>
				[<a href="http://www.cytoscape.org/download_agilent_literature_search_v2.php?file=litsearch_v2">Download Agilent Literature Search version 2 for Cytoscape v2.1, v2.2 </a>]
				<br>					<br>
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
				<td width="20%" valign="top">
					<b>BiNGO Plugin</b> <font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: May 2, 2005</font>
				</td>
				<td width="40%" valign="top">
					BiNGO is a Cytoscape 2.1 plugin to determine which Gene Ontology (GO) categories are statistically over-represented in a set of genes. BiNGO maps the predominant functional themes of a given gene set on the GO hierarchy, and outputs this mapping as a Cytoscape graph. A gene set can either be selected from a Cytoscape network or compiled from other sources (e.g. a list of genes that are significantly upregulated in a microarray experiment).<BR>
					<p>
						Released by: <a href="http://www.psb.ugent.be/cbd/">Computational Biology Division</a>, Dept. of Plant Systems Biology, Flanders Interuniversitary Institute for Biotechnology (VIB)
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.1, 2.2.
				</td>
				<td width="20%" valign="top">
					[<a href="http://www.psb.ugent.be/cbd/papers/BiNGO/">Download</a>]
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>
			<tr>
				<td width="20%" valign="top">
					<b>BioPAX Import Plugin</b> <font size="-1">
						<br>
						Version: 0.3 Beta
						<br>
						Release Date: June 16, 2006</font>
				</td>
				<td width="40%" valign="top">
					The BioPAX Plugin enables Cytoscape users to import and visualize BioPAX formatted documents.  BioPAX is a collaborative effort to create a data exchange format for biological pathway data.   Information is available at: <a href="http://biopax.org">http://biopax.org</a>
					<p>
						This product includes software developed by the Apache Software Foundation (<a href="http://www.apache.org/">http://www.apache.org</a>).
					</p>
					<p>
						Released by: Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
				</td>
				<td width="20%" valign="top">
					Verified to work in 2.3.
				</td>
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/plugins/biopax">Release Notes</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/plugins/biopax/zip_release/biopax_0_3.tar.gz">Download .tar.gz</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/plugins/biopax/zip_release/biopax_0_3.zip">Download .zip</a>]
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
						Version: 3 Beta
						<br>
						Release Date: June 9, 2006</font>
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
				Verified to work in 2.0, 2.1, 2.2, 2.3.
				</td>
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/beta3/README.txt">Release Notes</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/beta3/cpath-beta3.tar.gz">Download .tar.gz</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/beta3/cpath-beta3.zip">Download .zip</a>]
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
			Used by <a href="http://labs.systemsbiology.net/galitski/hepc/">the Hepatitis C Virus infection project.</a>
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
				 Verified to work in 2.0.
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
					<b>DomainNetworkBuilder Plugin</b><font size="-1">
						<br>
						Version: 1.0
						<br>
						Release Date: September 21, 2005</font>
				</td>
				<td width="40%" valign="top">
					<P>This plugin decomposes protein networks into domain-domain interactions.
						Basically, it transforms each protein node into a chain of
						consecutive domain nodes and constructs a putative network of
						interacting domain nodes. Parameter settings support the
						selection of various visualization modes.
					</P>
					<P>	Another plugin named DomainWebLinks
						provides context-dependent web links to Pfam, InterDom, and 3did
						for DomainNetworkBuilder.
					</P>
					<P>	Reference:<BR>Mario Albrecht, Carola
						Huthmacher, Silvio C.E. Tosatto, Thomas Lengauer<BR>Decomposing
						protein networks into domain-domain interactions. ECCB 2005
						Proceedings. <a href="http://bioinformatics.oxfordjournals.org/cgi/content/abstract/21/suppl_2/ii220">Bioinformatics, 21, Suppl. 2,
						2005</a>
					</P>
					<P>
						<A HREF="http://med.bioinf.mpi-sb.mpg.de/domainnet/index.html">Project web site</A>.
					</P>
					<P>Released by: Mario Albrecht, Carola
						Huthmacher, Lengauer Group, <A HREF="http://www.mpi-sb.mpg.de/">Max Planck Institute for Informatics</A>.
					</P>
				</td>
				<TD WIDTH=20%>
					<P>Verified to work in 2.1, 2.2.
					<BR>Not tested in Cytoscape 2.0
					</P>
				</TD>
				<TD WIDTH=20%>
					<P>[<A HREF="http://med.bioinf.mpi-sb.mpg.de/domainnet/index.html">
						Download from Project Web Site</A>]
					</P>
				</TD>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>




			<tr>
				<td width="20%" valign="top">
					<b>Dynamic Expression Plugin</b><font size="-1">
						<br>
						Version: 1.0
						<br>
						Release Date: August 9, 2005</font>
				</td>
				<td width="40%" valign="top">
					This plug-in loads an expression data file (consult the Cytoscape manual to learn
					about the format of this type of file) and then allows the user to color the nodes
					in a network according to their expression values. The GUI works like a VCR, with
					play, pause, and stop buttons. If the user presses the play button, the plug-in will
					iterate over all the conditions in the expression file and color the nodes according
					to their corresponding expression values.

					<p>
						Released by: Iliana Avila-Campillo, Galitski Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
					</p>
				</td>
				<td width="20%" valign="top">
				 Verified to work in 2.1. <br>Not tested in Cytoscape 2.0.
				</td>
				<td width="20%" valign="top">
					[<a href="plugins/DynamicXpr/dynxpr.jar">Download Plugin .jar</a>]
					<br>
					[<a href="plugins/DynamicXpr/README.txt">Download README</a>]
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
				Verified to work in 2.0, 2.1, 2.2.
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
				<td valign="top" width="20%">
					<b>GenePro Plugin</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: April, 2006</font>
				</td>
				<td valign="top" width="40%">
					<p>
GenePro is a Cytoscape 2.2 plugin for the visualization and analysis of  
protein-protein interaction networks and functional modules.
</p><ul>
<li>Networks can be visualized at two levels of resolution.  At the level  
      of functional modules, each cluster appears as a node.  Double clicking  
      a cluster loads a new network showing the local interactions within  
      that cluster, and the nearest neighbors.</li>
<li>The distribution of a cluster's members within a second network can  
      be visualized using "Pie Nodes", where each cluster (node) appears  
      as a pie chart illustrating how that cluster's members are  
      distributed within a second network.  Visual cues indicate cluster and  
      interaction attributes. </li>
<li>Gene expression data can be superimposed on clusters, to show how the  
      cluster responds to various conditions, or how the cluster members  
      respond to a single condition.</li>
</ul>
<br>
Released by:
<br>
<br>
Wodak Laboratory  
<br>
Centre for Computational Biology  
<br>
The Hospital for Sick Children,  
<br>
Toronto, Canada
<br>
<br>
in association with:
<br>

<br>
Dept. of Biochemistry and the Dept. of Medical Genetics and  
<br>
Microbiology,  
<br>
University of Toronto  
<br>
Toronto, Canada

					<p></p>
				</td>
				<td valign="top" width="20%">
				Verified to work in 2.2.
				</td>
				<td valign="top" width="20%">
					[<a href="http://genepro.ccb.sickkids.ca/">Download</a>]
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
						Version: 2
						<br>
						Release Date: November 31, 2005</font>
				</td>
				<td width="40%" valign="top">
					<p>
					This plugin enables Cytoscape to perform set-like operations (union, intersection, difference) on two or more graphs.
					<p>
						Released by: <a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">The Ideker Lab</a>, Department of Bioengineering, UCSD
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.2.
				</td>
				<td width="20%" valign="top">
					[<a href="http://be-web.ucsd.edu/faculty/area/ideker_lab/plugins/GraphMerge/release/README.txt">Release Notes</a>]
					[<a href="http://be-web.ucsd.edu/faculty/area/ideker_lab/plugins/GraphMerge/release/GraphMerge.jar">Download</a>]
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

			<tr>
				<td width="20%" valign="top">
					<b>GSnet</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: June 22, 2006</font>
				</td>
				<td width="40%" valign="top">
					This plugin visualizes a network graph of a given enriched geneset with
					their protein-protein interaction and Gene Ontology(GO) term
					enrichment. The protein-protein interaction between genes is estimated
					by Agilent literature search engine using PubMed, and the Biomoleuclar
					Interaction Network Database (BIND). GO term enrichment within given
					genes is assessed by statistical model using hypergeometric method and
					false discovery rate calculation.
					<br/>
					<br/>

					It provides the view control function using set operation. Users can
					select specific or common genes on network by using set operation.
					<br/>
					<br/>

					If you have any problems or suggestions about this program we would
					love to hear about them. 
				</td>
				<td width="20%" valign="top">
				 Verified to work in 2.2
				</td>
				<td width="20%" valign="top">
					[<a href="http://www.kobic.re.kr/gsnet">Download GSnet Plugin</a>]
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
                    Used by <a href="http://labs.systemsbiology.net/galitski/hepc/">the Hepatitis C Virus infection project.</a>
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
			<font size="-1">Version: 1.0
				<br>
				Release Date: Jan. 16, 2006</font>
		</td>
		<td width="40%" valign="top">
			This plugin enables Cytoscape to search for significant networks as described in <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=12169552&amp;dopt=Abstract">Bioinformatics. 2002 Jul;18 Suppl 1:S233-40.</a>
			<br>
			<br>
			Released by: <a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">The Ideker Lab</a>, Department of Bioengineering, UCSD
		</td>
 		<td width="20%" valign="top">
				 Verified to work in 2.2.
				</td>
		<td width="20%" valign="top">
			[<a href="http://be-web.ucsd.edu/faculty/area/ideker_lab/plugins/jActiveModules2/release/README.txt">Release Notes</a>]
			<br>
			[<a href="http://be-web.ucsd.edu/faculty/area/ideker_lab/plugins/jActiveModules2/release/jActiveModules.jar">Download Jar</a>]
			<br>
			[<a href="http://be-web.ucsd.edu/faculty/area/ideker_lab/plugins/jActiveModules2/release/jActiveModules.tgz">Download Source</a>]
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
						Version: 1.1
						<br>
						Release Date: February 15, 2005</font>
				</td>
				<td width="40%" valign="top">
					The MCODE Cytoscape Plugin finds clusters (highly interconnected regions) in any network loaded into Cytoscape. Depending on the type of network, clusters may mean different things. For instance, clusters in a protein-protein interaction network have been shown to be protein complexes and parts of pathways. Clusters in a protein similarity network represent protein families.
					<p>
						Released by: Gary Bader, Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.0, 2.1, 2.2.
				</td>
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/~bader/software/mcode/index.html">MCODE Plugin Web Site</a>]
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

	<tr>
		<td width="20%" valign="top">
			<b>Metabolica Plugin</b>
			<br>
			<font size="-1">Version: 1.0
				<br>
				Release Date: Dec. 8, 2005</font>
		</td>
		<td width="40%" valign="top">
			Metabolica plugin find network motifs of arbitrary length in the current network.
			<p>
			Released by: Michele Petterlini, Giovanni Scardoni.
		</td>
		<td width="20%" valign="top">
				 Verified to work in 2.2.
				</td>
		<td width="20%" valign="top">
			[<a href="http://www.petterlini.it/metabolica/">Download Plugin</a>]
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
			<b>NetworkAnalyzer Plugin</b>
			<br>
			<font size="-1">Version: 1.0
				<br>
				Release Date: Jan. 23, 2006</font>
		</td>
		<td width="40%" valign="top">
			NetworkAnalyzer is a Java plugin for Cytoscape, a software platform
			for the analysis and visualization of molecular interaction networks.
			The plugin computes specific parameters describing the network topology.
			<br><br>Feature List:<br>
		<ul>
			<li>Analysis of undirected networks:
				<ul>
					<li>Average number of neighbors</li>
					<li>Diameter</li>
					<li>Number of connected pairs of nodes</li>

					<li>Node degree distribution</li>
					<li>Average clustering coefficient distribution</li>
					<li>Topological coefficient distribution</li>
					<li>Shortest path length distribution</li>
				</ul>
			</li>

			<li>Analysis of directed networks:
				<ul>
					<li>Average number of neighbors</li>
					<li>In-degree distribution</li>
					<li>Out-degree distribution</li>
				</ul>
			</li>

			<li>Saving and loading analysis results</li>

			<li>Saving chart as an image file (PNG or JPEG)</li>

			<li>Adjusting visual properties of the charts: title, axis labels, and more</li>
		</ul>
			<P>Released by: Mario Albrecht, <a href="http://www.mpi-inf.mpg.de/">Max Planck Institute for Informatics</a>.
		</td>
		<td width="20%" valign="top">
				 Verified to work in 2.1, 2.2.
				</td>
		<td width="20%" valign="top">
			[<a href="http://med.bioinf.mpi-inf.mpg.de/netanalyzer/index.php">NetworkAnalyzer Project Page</a>]
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
			<b>Network Filter Plugin</b>
			<br>
			<font size="-1">Version: 1.0
				<br>
				Release Date: March 24, 2005</font>
		</td>
		<td width="40%" valign="top">
			This plugin supports features found in Cytoscape 2.0 for editing
Networks using Filters.  Available on the "Filters" Menu, "Network
�" will allow you to create new Networks, and modify existing
Networks by using Filters that you have created.
			<P>Released by: Rowan Christmas, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
		</td>
		<td width="20%" valign="top">
				 Verified to work in 2.1, 2.2.
				</td>
		<td width="20%" valign="top">
			[<a href="plugins/NetworkFilter/rowan.jar">Download Plugin .jar</a>]
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
				 Verified to work in 2.0, 2.1.<A HREF="#more_info"><BR>Not tested in Cytoscape 2.2*</A>
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
					<b>PeSca (Path Extraction by Smallest Cost Algorithm) Project</b><font size="-1">
						<br>
						Version: 1.0
						<br>
						Release Date: March, 2005</font>
				</td>
				<td width="40%" valign="top">
					<p>
					PeSca is a Cytoscape's plugin able to find the shortest interaction
					path between two proteins in a Cytoscape's network. It is tested under
					Cytoscape 2.1.
					<p>
						Released by: Michele Petterlini, Giovanni
Scardoni.
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.1, 2.2.
				</td>
				<td width="20%" valign="top">
					[<a href="http://www.petterlini.it/pesca/">PeSca Web Site and Download</a>]
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

     <tr>
				<td width="20%" valign="top">
					<b>Phenotype Genetics Plug-In</b><font size="-1">
						<br>
						Version: 2.0
						<br>
						Release Date: July, 2005</font>
				</td>
				<td width="40%" valign="top">
					<p>Constructs genetic-interaction networks from large sets of phenotype measurements from cells with single and pairwise genetic perturbations. Network derivation is generalized and fully computable. It also implements network-analysis methods to find local and global interaction patterns reflecting the effects of gene perturbations on biological processes and pathways.
					<p>
						Released by: <a href="http://labs.systemsbiology.net/galitski/">Galitski Lab</a> at the <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.1.<A HREF="#more_info"><BR>Not tested in Cytoscape 2.PSI2*</A>
				</td>
				<td width="20%" valign="top">
                                                                                        [<a href="http://labs.systemsbiology.net/galitski/projs/system_genetics/protected/tutorial/index_pg.html">Phenotype Genetics Web-Site and Download (includes a tutorial)</a>]
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

			<tr>
				<td width="20%" valign="top">
					<b>PSI-MI, Level 1,2 Import Plugin</b><font size="-1">
						<br>
						Version: 1.0
						<br>
						Release Date: June 16, 2006</font>
				</td>
				<td width="40%" valign="top">
					<p>
					This plugin enables Cytoscape to import to the Proteomics Standards Initiative Molecular 
					Interaction (PSI-MI) 1.0 and 2.5 XML Format. PSI-MI is a XML format used to represent 
					and exchange protein-protein interaction data. This plugin is extended from the 
					existing PSI-MI import/export plugin released by Sander group. This also supports 
					import of compressed formats like zip and gzip files containing PSI-MI 1.0 and 2.5 document. 
					The export functionality will be added to the plugin soon.
					<p>
						Released by: Proteomics Services Team, European Bioinformatics Institute
						<br>Hinxton, Cambridgeshire, UK
						<br><a href="http://www.ebi.ac.uk/proteomics">http://www.ebi.ac.uk/proteomics</a>
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.0, 2.1, 2.2, 2.3.
				</td>
				<td width="20%" valign="top">
					[<a href="http://www.ebi.ac.uk/~nvinod/psi_mi_release/psi-2.tar.gz">Download .tar.gz</A>]
					<br>
					[<a href="http://www.ebi.ac.uk/~nvinod/psi_mi_release/psi-2.zip">Download .zip</a>]
					<br>
					[<a href="http://www.ebi.ac.uk/~nvinod/psi_mi_release/Cytoscape_psi_mi_plugin2_5_src.zip">Download Source .zip</a>]
				</td>
			</tr>

			<tr>
				<td width="20%" valign="top">
					<b>PSI-MI, Level 1 Import/Export Plugin</b><font size="-1">
						<br>
						Version: 3.01
						<br>
						Release Date: June 22, 2006</font>
				</td>
				<td width="40%" valign="top">
					<p>
					This plugin enables Cytoscape to import/export to the
					<A HREF="http://psidev.sourceforge.net/">Proteomics
					Standards Initiative Molecular Interaction (PSI-MI)</A> XML Format.
					PSI-MI is a XML format used to represent and exchange protein-protein
					interaction data.  This plugin supports PSI-MI Level 1 only.

					<P>For an earlier version of this Plugin, which
					works in Cytoscape 1.1, refer to the Data Services Plugin
					on the <A HREF="plugins1.php">Cytoscape 1.1 Plugins</A> page.
					<p>
						Released by: Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.0, 2.1, 2.2, 2.3.
				</td>
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/psi/version3/README.txt">Release Notes</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/psi/version3/psi-3.tar.gz">Download .tar.gz</a>]
<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/psi/version3/psi-3.zip">Download .zip</a>]

				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

			<tr>
				<td width="20%" valign="top">
					<b>SBMLReader plugin</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: February 28, 2006</font>
				</td>
				<td width="40%" valign="top">
					<p>
This Cytoscape 2.2 plug-in reads all Species and Reactions and puts
them into one cytoscape network regardless of the compartments. It the
links the Species to the Reactions using the Reactant and Product
lists. It then also applies a specific VisualStyle for the SBMLReader
plugin.
<P>
Please see:
http://www.wligtenberg.nl
for downloading and installation information.
					<p>
						Released by: W.P.A. Ligtenberg M.Sc. Eindhoven University of Technology and Maastricht University
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.2.
				</td>
				<td width="20%" valign="top">
                    [<a href="http://www.wligtenberg.nl">Web Site and Download</a>]                           
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

			<tr>
				<td width="20%" valign="top">
					<b>ShortestPath Plugin</b><font size="-1">
						<br>
						Version: 0.3
						<br>
						Release Date: July 13, 2005</font>
				</td>
				<td width="40%" valign="top">
					<p>
					ShortestPath is a plugin for Cytoscape 2.1 to show the shortest path
between 2 selected nodes in the current network.

It supports both directed and undirected networks and it gives the
user the possibility to choose which node (of the selected ones)
should be used as source and target (useful for directed networks).

The plugin API makes possible to use its functionality from another plugin.
					<p>
						Released by: Marcio Rosa da Silva
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.1, 2.2.
				</td>
				<td width="20%" valign="top">
					[<a href="http://csresources.sourceforge.net/ShortestPath/">Web Site and Download</a>]
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
						Version: 3
						<br>
						Release Date: June 9, 2006</font>
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
				Verified to work in 2.0, 2.1, 2.2, 2.3.
				</td>
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/soft/version3/README.txt">Release Notes</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/soft/version3/soft-3.tar.gz">Download .tar.gz</a>]
<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/ds/soft/version3/soft-3.zip">Download .zip</a>]

				</td>
			</tr>

		</tbody>
	</table>
	<div id="indent">
	<P>
			<A NAME="more_info">
			* If you have verified that the specified plugin works in 2.2, please send an email to <A HREF="http://groups-beta.google.com/group/cytoscape-discuss">cytoscape-discuss</A>, and we will update the web page.
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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
	<meta name="robots" content="noindex" />
	<title>Cytoscape 2.x Plugins</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css">
	<link rel="shortcut icon" href="images/cyto.ico">
</head>
<body bgcolor="#ffffff">
<div id="topbar">
	<div class="title">Cytoscape 2.x Old Plugins</div>
</div>

<div id="warning">
  <p>&nbsp;</p>
  <p><font color="#FF0000" size="+3">Warning: This is old plugin page of Cytoscape. The new page is at <a href="http://cytoscape.org/plugins.php">http://cytoscape.org/plugins.php</a><a href="http://cytoscape.org/plugins"></a></font></p>
  <p>&nbsp;</p>
</div>

<br>
<div id="indent">
	<big><b>About Cytoscape Plugins:</b></big>
	<p>
		Cytoscape includes a flexible Plugin architecture that enables developers to add extra functionality beyond that provided in the core. Plugins also provide a convenient place for testing out new Cytoscape features. As more Plugins become available, they will be listed on this page, and posted to our <A HREF="http://groups-beta.google.com/group/cytoscape-announce">cytoscape-announce</A> mailing list.

    </p>
	<p><big><b>Current Plugins</b></big>
	<p>
	Refer to the current plugin page for a list of plugins registered with the plugin manager <a href="http://cytoscape.org/plugins/index.php">Current Cytoscape 2.x Plugins</a>. 
	</p>
	<p><big><b>Old Plugins</b></big>
<ul>
<li><b><a href="#ANALYSIS_PLUGINS">Analysis Plugins</a></b> - Used for analyzing existing networks.</li>
<li><b><a href="#IO_PLUGINS">Network and Attribute I/O Plugins</a></b> - Used for importing networks and attributes in different file formats.</li>
<li><b><a href="#NET_INFERENCE_PLUGINS">Network Inference Plugins</a></b> - Used for inferring new networks.</li>
<li><b><a href="#FUNC_ENRICHMENT_PLUGINS">Functional Enrichment Plugins</a></b> - Used for functional enrichment of networks.</li>
<li><b><a href="#SCRIPTING_PLUGINS">Communication/Scripting Plugins</a></b> - Used for communicating with or scripting Cytoscape.</li>
</ul>

    

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
	</p>
	<br/>
	<hr/>
	<br/>

	<p>
		<big><b><a name="ANALYSIS_PLUGINS">Current Cytoscape 2.x Analysis Plugins</a></b></big>
	</p>
</div>

<div id="indent">
	<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="95%" bgcolor="#ebebff">

		<tbody>		


			<tr>
				<td width="25%" valign="top">
					<b>CABIN Plugin</b><font size="-1">
						<br>
						Version: 1.0
						<br>
						Release Date: April 30, 2007</font>
			  </td>
				<td width="45%" valign="top">
					<P>
					CABIN is an exploratory data analysis tool that enables integration and
					analysis of interactions evidence obtained from multiple sources,
					thereby increasing the confidence of computational predictions as well
					as validating experimental observations. CABIN has been written in
					JavaTM.
					</P>
					<P>
						<A HREF="http://www.sysbio.org/capabilities/compbio/cabin.stm">Project web site</A>.
					</P>
					<P>
						Released by: Mudita Singhal, Pacific Northwest National Laboratory
					</P>
			  </td>
				<TD WIDTH=15%>
					<P>
						Verified to work in 2.4
					</P>
			  </TD>
				<TD WIDTH=15%>
					<P>
						[<A HREF="http://www.sysbio.org/dataresources/cabin.stm">Download from Project Web Site</A>]
					</P>
			  </TD>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>		



			<tr>
				<td width="25%" valign="top">
					<b>Cerebral Plugin</b><font size="-1">
						<br>
						Version: 1.0
						<br>
						Release Date: February 20, 2007</font>
			  </td>
				<td width="45%" valign="top">
					<P>
						Cerebral (Cell Region-Based Rendering And Layout) is an open-source
						Java plugin for the Cytoscape biomolecular interaction viewer. Given
						an interaction network and subcellular localization annotation,
						Cerebral automatically generates a view of the network in the style of
						traditional pathway diagrams, providing an intuitive interface for the
						exploration of a biological pathway or system. The molecules are
						separated into layers according to their subcellular localization.
						Potential products or outcomes of the pathway can be shown at the
						bottom of the view, clustered according to any molecular attribute
						data - protein function - for example. Cerebral scales well to
						networks containing thousands of nodes.
					</P>
					<P>
						Reference:<BR>
						Aaron Barsky, Jennifer L. Gardy, Robert E.W. Hancock and Tamara Munzner<br>
						<i>Cerebral: a Cytoscape plugin for layout of and interaction with biological 
						networks using subcellular localization annotation</i> 
						<a href="http://bioinformatics.oxfordjournals.org/cgi/content/abstract/btm057">
							Bioinformatics, Feb. 19, 2007</a>
					</P>
					<P>
						<A HREF="http://www.pathogenomics.ca/cerebral">Project web site</A>.
					</P>
					<P>
						Released by: Jennifer Gardy & Aaron Barsky
						University of British Columbia.
					</P>
			  </td>
				<TD WIDTH=15%>
					<P>
						Verified to work in 2.4
					</P>
			  </TD>
				<TD WIDTH=15%>
					<P>
						[<A HREF="http://www.pathogenomics.ca/cerebral">Download from Project Web Site</A>]
					</P>
			  </TD>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>		




		
			<tr>
				<td width="26%" valign="top">
					<b>DataMatrix Plugin</b><font size="-1">
						<br>
						Version: .8 BETA
						<br>
						Release Date: August 15, 2004</font>
			  </td>
				<td width="32%" valign="top">
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
					Full documentation is <a href="http://db.systemsbiology.net/cytoscape/versions/2.x/2.0/tutorials/DataMatrixPlugin"> here</a>.
					<p>
						Released by: Paul Shannon, Baliga Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
					</p>
			  </td>
				<td width="24%" valign="top">
				 Verified to work in 2.0.				</td>
				<td width="18%" valign="top">
					[<a href="http://db.systemsbiology.net/cytoscape/versions/2.x/2.0/plugins/DataMatrixPlugin.jar">Download jar</a>]
					<br>
			  </td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>


			<tr>
				<td width="26%" valign="top">
					<b>DomainNetworkBuilder Plugin</b><font size="-1">
						<br>
						Version: 1.0
						<br>
						Release Date: September 21, 2005</font>
			  </td>
				<td width="32%" valign="top">
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
				<TD WIDTH=24%>
					<P>Verified to work in 2.1, 2.2.
					<BR>Not tested in Cytoscape 2.0
					</P>
			  </TD>
				<TD WIDTH=18%>
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
				<td width="26%" valign="top">
					<b>Dynamic Expression Plugin</b><font size="-1">
						<br>
						Version: 1.0
						<br>
						Release Date: August 9, 2005</font>
			  </td>
				<td width="32%" valign="top">
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
				<td width="24%" valign="top">
				 Verified to work in 2.1. <br>
				 Not tested in Cytoscape 2.0.
			  </td>
				<td width="18%" valign="top">
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
				<td width="26%" valign="top">
					<b>Expression Correlation Network Plugin</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: January 19, 2005</font>
			  </td>
				<td width="32%" valign="top">
					<p>
					This plugin enables Cytoscape users to correlate genes or conditions in an expression matrix file loaded into Cytoscape. The resulting correlations are visualized as a network in Cytoscape.  A condition correlation network is an alternate way of representing expression condition clustering results which can sometimes make it easier to notice clusters compared to the normal 'heat-map' view.
					<p>
						Released by: Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
			  </td>
				<td width="24%" valign="top">
				Verified to work in 2.0, 2.1, 2.2.				</td>
				<td width="18%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/plugins/ExpressionCorrelationReadme.txt">Release Notes</a>]
					[<a href="http://www.cbio.mskcc.org/cytoscape/plugins/ExpressionCorrelation.zip">Download</a>]				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>



			<tr>
				<td valign="top" width="26%">
					<b>GenePro Plugin</b><font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: April, 2006</font>
			  </td>
				<td valign="top" width="32%">
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
				<td valign="top" width="24%">
				Verified to work in 2.2.				</td>
				<td valign="top" width="18%">
					[<a href="http://genepro.ccb.sickkids.ca/">Download</a>]				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

            <tr>
                
		<td width="26%" valign="top">
			<b>GOlorize</b>
			<br>
		  <font size="-1">Version: 1.1<br>
			  Release Date: Feb. 12, 2007</font>
		</td>
		<td width="32%" valign="top">
			<p>
				GOlorize provides an automated network visualization in Cytoscape, where Gene Ontology (GO) categories are 				used to direct the network graph layout process and to emphasize the biological function of the nodes. 						
				GOlorize is used in conjunction with BiNGO plug-in, an efficient tool to find the GO categories that are 	
				overrepresented in a selected part of a given network. GOlorize first highlights the nodes that belong to 
				the same category using color-coding and then constructs an enhanced visualization of the network using a 
				class-directed layout algorithm.
			</p>
			<p>
				Released by: <a href="http://www.pasteur.fr/recherche/unites/Biolsys/index.htm">Schwikowski Group</a>, 		
				Institut Pasteur
			</p>
			<p>
				<a href="http://www.pasteur.fr/recherche/unites/Biolsys/GOlorize/index.htm">Project Website</a>
			</p>
                Licensed under GNU Public License.
		</td>
 		<td width="24%" valign="top">
				 Verified to work in 2.2 and 2.4			  </td>
		<td width="18%" valign="top">
		[<a href="http://www.pasteur.fr/recherche/unites/Biolsys/GOlorize/GOlorize2-4UserGuide.pdf">User Guide</a>]
		<br>
		[<a href="http://www.pasteur.fr/recherche/unites/Biolsys/GOlorize/GOlorize2-4.jar">Download Jar</a>]
		<br>[<a href="http://www.pasteur.fr/recherche/unites/Biolsys/GOlorize/src.tar.gz">Source Code</a>]
		<br>[<a href="http://www.pasteur.fr/recherche/unites/Biolsys/GOlorize/javadoc.tar.gz">JavaDoc</a>]
		</td>
	</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

            <tr>
                
		<td width="26%" valign="top">
			<b>Hyperbolic Focus</b>
			<br>
			<font size="-1">Version: 1.0
				<br>
				Release Date: </font>
		</td>
		<td width="32%" valign="top">
		Hyperbolic Focus Layout plugin.
			<br>
			<br>
			This plug-in was created by Robert Ikeda, who was supported (partially) by the <a href="http://prime.ucsd.edu">PRIME</a> Program funded by NSF (OISE 0407508) and <a href="http://calit2.net">Calit2</a> 
			<br>
			<br>
			Released by: <a href="http://chianti.ucsd.edu/idekerlab">Ideker Lab</a>, UCSD 
                         <br>
                         Licensed under GNU Public License.
		</td>
 		<td width="24%" valign="top">
				 Verified to work in 2.4.			  </td>
		<td width="18%" valign="top">
			[<a href="plugins/HypFocus/HypFocus.jar">HypFocus</a>]
			<br>
			<br>
		</td>
	</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>


			<tr>

		<td width="26%" valign="top">
			<b>jActiveModules</b>
			<br>
			<font size="-1">Version: 1.0
				<br>
				Release Date: Jan. 19, 2007</font>
		</td>
		<td width="32%" valign="top">
			This plugin enables Cytoscape to search for significant networks as described in <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=12169552&amp;dopt=Abstract">Bioinformatics. 2002 Jul;18 Suppl 1:S233-40.</a>
			<br>
			<br>
			Released by: <a href="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">The Ideker Lab</a>, Department of Bioengineering, UCSD
		</td>
 		<td width="24%" valign="top">
				 Verified to work in 2.2 and 2.4			  </td>
		<td width="18%" valign="top">
			<p>
				[<a href="plugins/jActiveModules_2.4/license.txt">License Agreement</a>]
			</p>
			<b>2.2</b> compatible version:
			<br>
			[<a href="plugins/jActiveModules_2.2/jActiveModules.jar">Download Jar</a>]
			<br>
			[<a href="plugins/jActiveModules_2.2/jActiveModules.tgz">Download Source</a>]

			<br>
			<hr>
			<br>
			<b>2.4</b> compatible version:	
			<br>
			[<a href="plugins/jActiveModules_2.4/jActiveModules.jar">Download Jar</a>]
			<br>
			[<a href="plugins/jActiveModules_2.4/jActiveModules_src.tgz">Download Source</a>]

		</td>
	</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>
			<tr>
				<td width="26%" valign="top">
					<b>MCODE Plugin</b><font size="-1">
						<br>
						Version: 1.2
						<br>
						Release Date: February 7, 2007</font>
			  </td>
				<td width="32%" valign="top">
					The MCODE Cytoscape Plugin finds clusters (highly interconnected regions) in any network loaded into Cytoscape. Depending on the type of network, clusters may mean different things. For instance, clusters in a protein-protein interaction network have been shown to be protein complexes and parts of pathways. Clusters in a protein similarity network represent protein families.
					<p>
						Released by: Gary Bader, Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
			  </td>
				<td width="24%" valign="top">
				Verified to work in 2.0, 2.1, 2.2., 2.4				</td>
				<td width="18%" valign="top">
					[<a href="http://baderlab.org/Software/MCODE">MCODE Plugin Web Site</a>]				</td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

	<tr>
		<td width="26%" valign="top">
			<b>Metabolica Plugin</b>
			<br>
			<font size="-1">Version: 1.2
				<br>
				Release Date: Feb. 22, 2007</font>
		</td>
		<td width="32%" valign="top">
			The Metabolica plugin finds network motifs of arbitrary length in any
Cytoscape network, which may not be easily seen in complex networks. For
example, it can automatically find every Feed Forward Loop structure in
a network, which helps determine relationships between two proteins by
an activation protein chain. Results are available for further study in
Cytoscape.
			<p>
			Released by: Michele Petterlini, Giovanni Scardoni.
		</td>
		<td width="24%" valign="top">
				 Verified to work in 2.4.</td>
		<td width="18%" valign="top">
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
		<td width="26%" valign="top">
			<b>Motif Finder Plugin</b>
			<br>
			<font size="-1">Version: .1 BETA
				<br>
				Release Date: Sept. 3, 2004</font>
		</td>
		<td width="32%" valign="top">
			Run a Gibbs sampling motif detector on sequences corresponding to the selected nodes in the current network. This currently implements the most basic of the motif detection algorithms available from the <a href="http://sf.net/projects/netmotsa">Gibbs sampling motif detection library</a> described in <a href="http://bioinformatics.oupjournals.org/cgi/content/abstract/20/suppl_1/i274">Bioinformatics</a>.
			<br>
			This plugin requires that the "sequence fetcher" (part of the HTTP Data plugin) be run first, to fetch the sequences, or they may be pre-loaded as node attributes. It may be used on protein or DNA sequences (detected automatically). Various types of information such as motif logos, alignment tables, and motif positions are displayed at the end of the detection run (<a href="http://db.systemsbiology.net/cytoscape/old/projects/static/dreiss/motifFinder/software.jpg">screenshot</a>).
			<br>
			<br>
			At some point, I hope to implement the fully network-informed version of the motif finder as described in the article.
			<br>
			<br>
			Released by: David J. Reiss, Schwikowski Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
		</td>
		<td width="24%" valign="top">
				 Verified to work in 2.0, 2.1.		</td>
		<td width="18%" valign="top">
			[<a href="http://db.systemsbiology.net/cytoscape/old/projects/static/dreiss/motifFinder/motifFinder.jar">Download Plugin .jar</a>]
			<br>
		</td>
	</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>

	<tr>
		<td width="26%" valign="top">
			<b>NetMatch Plugin</b>
			<br>
			<font size="-1">Version: 1.0.1
				<br>
				Release Date: Dec. 22, 2006</font>
		</td>
		<td width="32%" valign="top">
			NetMatch finds user defined network motifs in any Cytoscape network. Node and edge
			attributes of any type and paths of unknown length can be specified in the search.
			<P>Released by: <a href="http://alpha.dmi.unict.it/~ctnyu/">Ferro, Giugno, Pulvirenti group, University of Catania</a>, <a href="http://baderlab.org/">Bader group,
			University of Toronto</a> and <a href="http://cs.nyu.edu/cs/faculty/shasha/">Shasha group, New York University</a>.
		</td>
		<td width="24%" valign="top">
				 Verified to work in 2.3, 2.4		</td>
		<td width="18%" valign="top">
			Download from: [<a href=" http://alpha.dmi.unict.it/~ctnyu/netmatch.html">NetMatch Official Site</a>] or
			[<a href=" http://baderlab.org/Software/NetMatch">NetMatch Mirror</a>]
			<br>
		</td>
	</tr>

	<tr>
				<td colspan="4">
					<hr>
				</td>
		  </tr>


	<tr>
		<td width="26%" valign="top">
			<b>NetworkAnalyzer Plugin</b>
			<br>
			<font size="-1">Version: 1.0
				<br>
				Release Date: Jan. 23, 2006</font>
		</td>
		<td width="32%" valign="top">
			NetworkAnalyzer is a Java plugin for Cytoscape, a software platform
			for the analysis and visualization of molecular interaction networks.
			The plugin computes specific parameters describing the network topology.
			<br>
			<br>Feature List:<br>
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
		<td width="24%" valign="top">
				 Verified to work in 2.1, 2.2.		</td>
		<td width="18%" valign="top">
			[<a href="http://med.bioinf.mpi-inf.mpg.de/netanalyzer/index.php">NetworkAnalyzer Project Page</a>]
			<br>
		</td>
	</tr>

		 		<tr> <td colspan="4"> <hr> </td> </tr>

			<tr>
				<td width="26%" valign="top">
					<b>Network Motif Finder</b><font size="-1">
						<br> 
						Version: 1.0
						<br>
						Release Date: October 19, 2006
						</font>
			  </td>
				<td width="32%" valign="top">
				<p>
				This pluging identifies network motifs, small repeatedly occurring mult-element components of a network, where the repetition suggests functional significance.  Statistical significance is applied to each motif type, and all identified motif types can be aggregated and extracted into smaller motif sub-networks.  This plugin has full functionality for multi-mode genetic networks created by the Cytoscape plugin ‘Phenotype Genetics’, and partial functionality for all other network types.  Specifically, motifs be identified and extracted for any network type, however statistical significance algorithms have been written for multi-mode netic networks only.  Network Motif Finder is open source and statistical significance algorithms for any network can be coded and easily incorporated.  Plugin, tutorial and sample data are packaged in the zip for download.
				<p>
					Released by: James Taylor, Galitski Lab, <a href="http://www.systemsbiology.org">Institute for Systems Biology</a>
			  </td>
				<td width="24%" valign="top">
					Verified to work in 2.3.2				</td>
				<td width="18%" valign="top">
					[<a href="http://db.systemsbiology.net/cytoscape/versions/2.x/2.3.2/plugins/NetworkMotifFinder/NMFPlugin.zip">Download zip</a>]				</td>
			</tr>
	<tr>
				<td colspan="4">
					<hr>
				</td>
		  </tr>


			<tr>
				<td width="26%" valign="top">
					<b>PeSca (Path Extraction by Smallest Cost Algorithm) Project</b><font size="-1">
						<br>
						Version: 2.0
						<br>
						Release Date: April, 2007</font>
			  </td>
				<td width="32%" valign="top">
					<p>
					The Pesca 2.0 plug-in is able to find ALL shortest paths between two
					nodes in a network.  In complex, highly connected, biological networks, such as
					intracellular signaling networks, it is possible that two proteins are
					connected by multiple paths. Likely, not all these paths are the
					shortest but it also possible that multiple shortest paths are present
					between two proteins. Identification of all these shortest paths may
					help to detect and score highly communicating nodes.
					<p>
					Released by: Giovanni Scardoni and Michele Petterlini
					</p>
			  </td>
				<td width="24%" valign="top">
				Verified to work in 2.1, 2.2, 2.4		</td>
				<td width="18%" valign="top">
					Download and web site:
					<br/>
					[<a href="http://profs.sci.univr.it/~scardoni/">PeSca 2.0</a>]				
					<br/>
					[<a href="http://www.petterlini.it/pesca/">PeSca 1.0</a>]				
			  </td>
			</tr>
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>



			<tr>
				<td width="26%" valign="top">
					<b>ShortestPath Plugin</b><font size="-1">
						<br>
						Version: 0.3
						<br>
						Release Date: July 13, 2005</font>
			  </td>
				<td width="32%" valign="top">
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
				<td width="24%" valign="top">
				Verified to work in 2.1, 2.2.				</td>
				<td width="18%" valign="top">
					[<a href="http://csresources.sourceforge.net/ShortestPath/">Web Site and Download</a>]				</td>
			</tr>

  		<tr> <td colspan="4"> <hr> </td> </tr>

			<tr>
				<td width="26%" valign="top">
					<b>StructureViz Plugin</b><font size="-1">
						<br> 
						Version: 0.9
						<br>
						Release Date: January 25, 2007
						</font>
			  </td>
				<td width="32%" valign="top">
				<p>		
This plugin links the visualization of biological (and biological
relationships expressed as networks) provided by Cytoscape with the
visualization and analysis of macromolecular structures and sequences
provided by the molecular visualization package: 
<a href="http://www.cgl.ucsf.edu/chimera/">UCSF Chimera</a>. 
<i>structureViz</i> provides commands to open
structures in UCSF Chimera, align open structures using Chimera's
Sequence/Structure tools, close structures that are currently open, and
exit Chimera. In order to load a structure associated with a node, the
Protein Databank (PDB) identifier (or identifiers if there are more than
one) must be present as an attribute of that node. Currently, <i>structureViz</i>
will look for an attribute named Structure, pdb, or pdbFileName. When a
structure is opened, structureViz provides an alternative interface to
Chimera: the <b>Cytoscape Molecular Structure Navigator</b>. This interface
uses a tree-based paradigm to allow users to select and effect the
display of models, chains, and residues, mostly through the use of
context menus. Additional commands allow for selection by chemistry
(Ligand, Ions, Solvent, Secondary Structure, and in the model context
menu, Functional Residues). Users can also take advantage of Chimera's
structural alignment capabilities by using the "Align" command.
				<p>
					Released by: John "Scooter" Morris, <a href="http://www.rbvi.ucsf.edu">RBVI</a>, UCSF
			  </td>
				<td width="24%" valign="top">
					Verified to work in 2.4				</td>
				<td width="18%" valign="top">
					[<a href="http://www.rbvi.ucsf.edu/Research/cytoscape/structureViz/">Website and Download</a>]				</td>
			</tr>
  

		</tbody>
  </table>


<div id="indent">
<p><b><big><a name="IO_PLUGINS">Current Cytoscape 2.x Network and Attribute I/O Plugins</a></big></b></p>
</div>
	<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="95%" bgcolor="#ebebff">

		<tbody>
			<tr>
				<td width="25%" valign="top">
					<b>BioNetBuilder Plugin</b> <font size="-1">
						<br>
						Version: 1.0 
						<br> 
						Release Date: September 25, 2006</font>
				</td>
				<td width="45%" valign="top">
				<p>
				BioNetBuilder is an open-source client-server Cytoscape plugin that
				offers a user-friendly interface to create biological networks
				integrated from several databases. Users can create networks for ~1500
				organisms, including common model organisms and human. Currently
				supported databases include: DIP, BIND, Prolinks, KEGG, HPRD, The
				BioGrid, and GO, among others. The BioNetBuilder plugin client is
				available as a Java Webstart, providing a platform-independent network
				interface to these public databases.
				</p>
				<p>
				Released by: Iliana Avila-Campillo, Kevin Drew, John Lin, David J. Reiss
				and Richard Bonneau, <a href="http://homepages.nyu.edu/%7erb133/">NYU Bonneau Lab</a>.
				</p>
				</td>
				<td width="15%" valign="top">
					Verified to work in 2.3, 2.3.1	
				</td>
				<td width="15%" valign="top">
					[<a href="http://err.bio.nyu.edu/cytoscape/bionetbuilder/">Project Website</a>]
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
					<p><b>Note:  The BioPAX plugin is automatically bundled with the default installation 
					of Cytoscape (version 2.4 and beyond).  To import BioPAX files:  select File &raquo; Import &raquo; Network.</b>
					</p>
				</td>
				<td width="20%" valign="top">
					Verified to work in 2.3, 2.4.
				</td>
				<!--
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/plugins/biopax">Release Notes</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/plugins/biopax/zip_release/biopax_0_3.tar.gz">Download .tar.gz</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/plugins/biopax/zip_release/biopax_0_3.zip">Download .zip</a>]
				</td>
				-->
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
					<p><b>Note:  cPath plugin is automatically bundled with the default installation 
					of Cytoscape (version 2.4 and beyond).  To import a network from cPath:  select File &raquo; New 
					&raquo; Network &raquo; Construct network using cPath.</b>
					</p>					
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.4.
				</td>
				<!--
				<td width="20%" valign="top">
					[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/beta3/README.txt">Release Notes</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/beta3/cpath-beta3.tar.gz">Download .tar.gz</a>]
					<br>
					[<a href="http://www.cbio.mskcc.org/cytoscape/cpath/beta3/cpath-beta3.zip">Download .zip</a>]
				</td>
				-->
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
					<b>MiMI Plugin</b>
					<br>
					<font size="-1">Version: 1.0 
						<br>
						Release Date: March 1, 2007</font>
				</td>

				<td width="40%" valign="top">


This plugin retrieves and displays molecular interaction data from
the <a href="http://mimi.ncibi.org">Michigan Molecular Interactions (MiMI) database</a>(1).  MiMI gathers
and merges data from well-known protein interaction databases
including BIND, DIP, and HPRD.  A provenance model has been developed
that tracks the source of each data element and what processes have
been performed upon it.

					<br>
					<br>
Within Cytoscape, each set of interactions are displayed in a
separate network view. Use the MiMI dialog box from the plugins menu
to query MiMI, view help and about pages, and to download the latest
version of the plugin.

					<br>
					<br>
1) Jayapandian M, Chapman A, Tarcea VG, Yu C, Elkiss A, Ianni A, Liu
B, Nandi A, Santos C, Andrews P, Athey B, States D, Jagadish HV.
(2007) "Michigan Molecular Interactions (MiMI): putting the jigsaw
puzzle together." Nucleic Acids Res. 35(Database issue):D566-71. Epub
2006 Nov 27.

					<br>
					<br>

					Released by: Jing Gao, Alex Ade, <a href="http://www.ncibi.org/">National Center for Integrative Biomedical Informatics, University of Michigan</a>
				</td>

				<td width="20%" valign="top">
				 Verified to work in 2.4
				</td>

				<td width="20%" valign="top">
					[<a href="http://mimi.ncibi.org/cytoscape/plugins/MiMI.jar">Download</a>]
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
					<b>PSI-MI, Level 1,2 Import/Export Plugin</b><font size="-1">
						<br>
						Version: 1.0
						<br>
						Release Date: June 16, 2006</font>
				</td>
				<td width="40%" valign="top">
					<p>
					This plugin enables Cytoscape to import / export data in the Proteomics Standards Initiative Molecular 
					Interaction (PSI-MI) 1.0 and 2.5 XML Format. PSI-MI is a XML format used to represent 
					and exchange protein-protein interaction data. 
					<p>
						Released by: Proteomics Services Team, European Bioinformatics Institute
						<br>Hinxton, Cambridgeshire, UK
						<br><a href="http://www.ebi.ac.uk/proteomics">http://www.ebi.ac.uk/proteomics</a>
					</p>
					<p>
						Released by: Sander Group, <a href="http://www.cbio.mskcc.org/">Computational Biology Center</a>, Memorial Sloan-Kettering Cancer Center.
					</p>
					<p><b>Note:  The PSI-MI plugin is automatically bundled with the default installation 
					of Cytoscape (version 2.4 and beyond).  To import PSI-MI files:  select File &raquo; Import &raquo; Network.</b>
					</p>
				</td>
				<!--
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
				-->
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
			<tr>
				<td colspan="4">
					<hr>
				</td>
			</tr>
			<tr>
				<td width="20%" valign="top">
					<b>tYNA Plugin</b><font size="-1">
						<br>
						Version: 1.0
						<br>
						Release Date: August 8, 2006</font>
				</td>
				<td width="40%" valign="top">
					<p>
					<b>tYNA</b> is a Web-based network analyzer. It provides five main functions:
					<ol>
					<li>Network management: storing, retrieving and categorizing networks, as well as performing format conversion. A comprehensive set of widely used network datasets is preloaded, put into standard form, and categorized with a set of tags.</li>
					<li>Network visualization: displaying networks in a graphical interface, with interactive controls for zooming, panning, coloring and exporting networks.</li>
					<li>Network comparison and manipulation: filtering based on network statistics and node names, and multiple network operations such as taking the union and intersection of networks.</li>
					<li>Network analysis: computing various statistics for the whole network and subsets, and finding motifs and defective cliques.</li>
					<li>Network Mining: predicting one network based on the information in another.</li>
					</ol>
					The plugin allows one to upload networks to and download networks from the tYNA database. The combination of the network sharing capability and other unique features provided by tYNA and the advanced visualization and analysis facilities of Cytoscape can prove particularly powerful.
					<p>
					Released by: <a href="http://www.gersteinlab.org/">The Gerstein Lab</a>, Department of Molecular Biophysics and Biochemistry, Yale University
					</p>
				</td>
				<td width="20%" valign="top">
				Verified to work in 2.2, 2.3.
				</td>
				<td width="20%" valign="top">
				        [<a href="http://networks.gersteinlab.org/tyna/cytoscape/">Download</a>]
				</td>
			</tr>
		</tbody>
	</table>

	<div id="indent">
	<p>
		<big><b><a name="NET_INFERENCE_PLUGINS">Current Cytoscape 2.x Network Inference Plugins</a></b></big>
	</p>
	</div>
	<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="95%" bgcolor="#ebebff">

		<tbody>
			<tr>
				<td width="25%" valign="top">
					<b>Agilent Literature Search</b><font size="-1">
						<br>
						Version: 2.4
						<br>
						Release Date: March 9, 2007</font>
				</td>
				<td width="45%" valign="top">

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
<p>Enhancements in version 2.4 include
<ul>
<li> Extend a Cytoscape network with associations extracted from the literature.
<li> Highlight search terms in the Cytoscape network.
</ul>




					<p>	Released by:
						<a href="http://www.labs.agilent.com/research/mtl/projects/sysbio.html">Systems Biology project</a>,
						<a href="http://www.labs.agilent.com/">Agilent Laboratories</a>,.
						<a href="http://www.agilent.com/">Agilent Technologies</a>.
					</p>
				</td>
				<td width="15%" valign="top">
				Verified to work in 2.4 (Agilent Literature Search 2.4).
				Verified to work in 2.3 (Agilent Literature Search 2.3).
				<br>
				Verified to work in 2.1, 2.2 (Agilent Literature Search 2.0).
				 
				</td>
				<td width="15%" valign="top">
					<br>
				[<a href="http://www.cytoscape.org/download_agilent_literature_search_v2.4.php?file=litsearch_v2.4">Download Agilent Literature Search version 2.4 for Cytoscape v2.4 </a>]
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
                        <b>MONET Plugin</b>
                        <br>
                        <font size="-1">Version: 1.0
                                <br>
                                Release Date: September 6, 2006</font>
                </td>
                <td width="40%" valign="top">
                MONET is a genetic interaction network inference algorithm based on Bayesian networks, which 
enables reliable network inference with large-scale data(ex. microarray) and genome-scale network 
inference from expression data. Network inference can be finished in reasonable time with parallel 
processing technique with supercomputing center resources.
                        <p>

                        <a href="http://delsol.kaist.ac.kr/~monet/home/"> MONET homepage</a>
                        <p>
			This product includes software developed by the 
			<a href="http://www.apache.org">Apache Software Foundation</a>.
                        <p>
			Released by: <a href="http://biosoft.kaist.ac.kr/">BISL Lab</a>, Department of
			BioSystems, Korea Advanced Institute of Science and Technology.

                </td>
                <td width="20%" valign="top">
                                 Verified to work in 2.1, 2.2, 2.3.
          </td>
                <td width="20%" valign="top">
                        [<a href="http://delsol.kaist.ac.kr/~monet/home/downloads.html">Download Plugin</a>]
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
		</tbody>
	</table>
	<div id="indent">
	<p>
		<big><b><a name="FUNC_ENRICHMENT_PLUGINS">Current Cytoscape 2.x Functional Enrichment Plugins</a></b></big>
	</p>
	</div>
	<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="95%" bgcolor="#ebebff">

		<tbody>
			<tr>
				<td width="25%" valign="top">
					<b>BiNGO Plugin</b> <font size="-1">
						<br>
						Version: 1
						<br>
						Release Date: May 2, 2005</font>
				</td>
				<td width="45%" valign="top">
					BiNGO is a Cytoscape 2.1 plugin to determine which Gene Ontology (GO) categories are statistically over-represented in a set of genes. BiNGO maps the predominant functional themes of a given gene set on the GO hierarchy, and outputs this mapping as a Cytoscape graph. A gene set can either be selected from a Cytoscape network or compiled from other sources (e.g. a list of genes that are significantly upregulated in a microarray experiment).<BR>
					<p>
						Released by: <a href="http://www.psb.ugent.be/cbd/">Computational Biology Division</a>, Dept. of Plant Systems Biology, Flanders Interuniversitary Institute for Biotechnology (VIB)
					</p>
				</td>
				<td width="15%" valign="top">
				Verified to work in 2.1, 2.2.
				</td>
				<td width="15%" valign="top">
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

		</tbody>
	</table>

	<div id="indent">
	<p>
		<big><b><a name="SCRIPTING_PLUGINS">Current Cytoscape 2.x Communication/Scripting Plugins</a></b></big>
	</p>
	</div>
	<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="95%" bgcolor="#ebebff">

		<tbody>
			<tr>
				<td width="25%" valign="top">
					<b>CyGoose Plugin</b>
					<br><font size="-1">
					Version: 2.4.3
					<br>
					Release Date: February 13, 2007</font>
				</td>
				<td width="45%" valign="top">
				<p>
				The CyGoose Cytoscape Plugin gives any network in Cytoscape full access
				to <a href="http://gaggle.systemsbiology.org/">the Gaggle</a>.  The Gaggle is a tool
				to create a data exploration and analysis environment. Other geese
				(independent threads/tools which Cytoscape can now interact with through
				the Gaggle Boss) can be found at
				<a href="http://gaggle.systemsbiology.org/docs/geese/">http://gaggle.systemsbiology.org/docs/geese/</a>
				</p>
				Added features for 2.4:</b><br>
				<ul>
					<li> Each network within Cytoscape is recognized as a separate goose in the Gaggle Boss
					<li> Automatic creation of a Visual Style when handling movies to give users a starting point to visualize network change 
				</ul>
				<b>Known issue:</b> This goose does not work with a local install of Cytoscape on a Windows machine.  It will work with Mac or Linux and as a webstart on Mac/Linux/Windows.  We are 
working on this and will release a fix as soon as we can.  				
<p>
				Released by: Sarah Killcoyne, <a href="http://systemsbiology.org">Institute for Systems Biology</a>, John Lin, Kevin Drew and Richard Bonneau, <a href="http://homepages.nyu.edu/%7erb133/">NYU Bonneau Lab</a>.
				</p>
		</td>
		<td width="15%" valign="top">
				 Verified to work in 2.4
			  </td>
		<td width="15%" valign="top">
			[<a href="http://db.systemsbiology.net/cytoscape/versions/2.x/2.4/plugins/CyGoose_2.4.3.jar">Download Jar</a>]
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
							<a href="http://galitski.systemsbiology.net//hepc/downloads/examples/CytoTalkHandler.html"> JavaDoc documentation of the client functions are available.</a></li>
							<li>
								Some simple <a href="http://galitski.systemsbiology.net//hepc/downloads/examples/"> example Perl, Python, and R scripts</a> have been written. </li>
			</ul>
			Released by: David J. Reiss, Schwikowski Group, <a href="http://www.systemsbiology.org/">Institute for Systems Biology</a>.
			Used by <a href="http://labs.systemsbiology.net/galitski/hepc/">the Hepatitis C Virus infection project.</a>
		</td>
		<td width="20%" valign="top">
				 Verified to work in 2.0, 2.1.
			  </td>
		<td width="20%" valign="top">
			[<a href="http://galitski.systemsbiology.net//hepc/downloads/cytoTalk-v2.jar">Download Plugin .jar</a>]
			<br>
		</td>
	</tr>
		</tbody>
	</table>

	<div id="indent">
	<P>
			<A NAME="more_info">
			* If you have verified that the specified plugin works in 2.3, please send an email to <A HREF="http://groups-beta.google.com/group/cytoscape-discuss">cytoscape-discuss</A>, and we will update the web page.
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

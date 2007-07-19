<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<? include "config.php"; ?>
<! Updated by kono for 2.3 release on 06_21_2006 >
<! Updated by apico with "latest release" variables for future updates (see config.php). 12_21_2006 >
<html>
	<head>
		<title>
			Cytoscape: Analyzing and Visualizing Network Data
		</title>
		<link href="css/cytoscape.css" media="screen" type="text/css" rel="stylesheet">
		<link href="images/cyto.ico" rel="shortcut icon">
		<meta content="Cytoscape, visualization, interaction network,
		software, genetic, gene, expression, protein interaction, graph,
		bioinformatics, computational biology,
		Whitehead Institute, Institute for Systems Biology, microarray analysis,
		clustering, pathways, integration, algorithm, simulated annealing,
		gene regulation" name="keywords">
	</head>
	<body>
		<table summary="" cellspacing="0" cellpadding="0" border="0" id="feature">
			<tbody>
				<tr>
					<td width="10">
						&nbsp;
					</td>
					<td width="200" valign="top">
						<div id="about">
							<h1>
								Cytoscape
							</h1>
							Cytoscape is an open source bioinformatics software platform for
							<b>
								<i>
									visualizing
								</i>
							</b>
							molecular interaction networks and
							<b>
								<i>
									integrating
								</i>
							</b>
							these interactions with gene expression profiles and other state data.
							<br>
							<a href="features.php">
								Read more &raquo;
							</a>
						</div>
					</td>

					<td width="440" valign="bottom" align="right">
						<div id="article">
							<h2>
								<a href='<?= $latest_download_link ?>'>
									Download Cytoscape <?= $latest_version ?>!
								</a>
							</h2>
							<p>
								New!&nbsp; Cytoscape <?= $latest_version ?> is now available.
								<br>
								<br>
									<a href='<?= $latest_release_notes_link ?>'>
									<?= $latest_version ?> Release Notes &raquo;
									</a>
								<br>
									<a href='<?= $latest_download_link ?>'>
									Download &raquo;
								</a>
								<br>
							</p>
							<p>
								<br>
								<h2><a href="retreat2007/index.php">Announcing Cytoscape Retreat 2007</a></h2>
								<p><a href="retreat2007/index.php">Now first time in Europe!!! <b>Amsterdam - Netherlands &raquo;</b></a></p>
								<p><b>November 6<sup>th</sup>-9<sup>th</sup> 2007</b></p>
								<br>
							</p>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
<? include "nav.php"; ?>
<? include "detailed_nav.php"; ?>
<table width="100%" border="0" cellpadding="5" cellspacing="5">
		<tr valign="top">
			<td width="65%">
				<div id="contents">
					<div id="content">
						<div id="indent">
							<div class="item">
								<br>
								<p>
								<big><b>
									<i>NEW!</i> &nbsp;&nbsp;<a href="retreat2007/index.php">Cytoscape Retreat 2007!</a>
								</b></big>

								<table width="100%">
									<tr valign="top">
										<td width="47%" valign="top">
											<br>
										 	Now in Europe! November 6<sup>th</sup> - 9<sup>th</sup> 
											<br>
											Including a public symposium on November 8<sup>th</sup>: <b> </b>
											with a formidable list of confirmed speakers: among them
											<ul>
											<li>Leroy Hood
											<li>Peter Sorger
											<li>Ewan Birney
											<li>Chris Sander
											</ul>	
											<br>
											Hosted by the <a href="http://www.humangenetics-amc.nl" target="_blank">Human Genetics Department of the Academic Medical Center</a> 
											in the vibrant historic city of <a href="/retreat2007/venue.php">Amsterdam</a>
										</td>
										<td valign="top">
											<a href="retreat2007/venue.php">
												<img src="retreat2007/images/magere-brug-small.jpg" border="0" align="right" alt="Amsterdam by night" />
											</a>
										</td>

									</tr>

								</table>
							</div>
							<p>
							<div class="item">
								<br>
								<p>
								<big><b>
									<i>NEW!</i> &nbsp;&nbsp;Cytoscape 2.4.1
								</b></big>
								<table width="100%">
									<tr valign="top">
										<td width="47%" valign="top">
											<br>
											(Updated 4/27/2006)
											<br>
										No new features, but several bugs have been fixed.	
											<br>
										</td>
									</tr>
								</table>
							</div>
								<p>
							<div class="item">
								<br>
								<p>
								<big><b>
									Cytoscape 2.4.0
								</b></big>
								<table width="100%">
									<tr valign="top">
										<td width="47%" valign="top">
											<br>
											(Updated 1/16/2007)
											<br>
										New features include:	
											<br>
											<ul>

		<li> Publication quality image generation. 
			<ul>
		    <li> Node label position adjustment.</li>
		    <li> Automatic Visual Legend generator.</li>
		    <li> Node position fine-tuning by arrow keys.</li>
		    <li> The ability to override selected VizMap settings. </li>
			</ul></li>


		<li>Quick Find plugin.</li>
		<li>New icons for a cleaner user interface.</li>
		<li>Consolidated network import capabilities.</li>
			<ul>
		    <li> Import network from remote data sources (through http or ftp).</li>
		    <li> Default support for the following file formats: SBML, BioPAX, PSI-MI, Delimited text, Excel.</li>
			</ul></li>

		<li>New Ontology Server. 

			<ul>
		    <li> Native support for OBO format ontology files. </li>
		    <li> Ability to visualize the ontology tree as a network (DAG).</li>
		    <li> Full support for Gene Association files. </li>
			</ul></li>

		<li>Support for Java SE 5</li>
		<li> Many, many bug fixes! </li>
											</ul>
											See the <a HREF="cyto_2_4_features.php">Release Notes</a> for more detail.
										</td>
										<td valign="top">
											<A HREF="screenshots/2_4_ss1.png">
												<img src="screenshots/2_4_ss1_thumb.png" border="0" align="left" alt="Cytoscape 2.4.0 Screenshot" />
											</A>
										</td>
									</tr>
								</table>
							</div>
								<p>
							<div class="item">
								<br>
								<p>
								<big><b>
									Cytoscape 2.3.2
								</b></big>
								<table width="100%">
									<tr valign="top">
										<td width="47%" valign="top">
											<br>
											(Updated 9/1/2006)
											<br>
										This release fixes a bug that made it impossible to save session files on Windows systems.  No new features.	
											<br>
										</td>
									</tr>
								</table>
							</div>
								<p>
							<div class="item">
								<br>
								<p>
								<big><b>
									&nbsp;&nbsp;Cytoscape 2.3.1
								</b></big>
								<table width="100%">
									<tr valign="top">
										<td width="47%" valign="top">
											<br>
											(Updated 7/21/2006)
											<br>
											No major new features, just bug fixes and some behind-the-scenses refactoring.	
											<br>
										</td>
									</tr>
								</table>
							</div>
							<p>
							<div class="item">
								<br>
								<p>
								<big><b>
									&nbsp;&nbsp;Cytoscape 2.3
								</b></big>
								<table width="100%">
									<tr valign="top">
										<td width="47%" valign="top">
											<br>
											(Updated 06/21/2006)
											<br>
											New Features include:
											<br>
											<ul>
												<li	>High-performance rendering engine.  Support for large networks (100,000+ nodes & edges)</li>
												<li>Ability to save a session</li>
												<li>Support for network attributes</li>
												<li>An improved command line interface.</li>
												<li>The GraphMerge plugin included by default</li>
												<li>Enhanced context or pop-up menus for nodes</li>
												<li>A rewritten <i>bird's eye view</i> of the network that is enabled by default</li>
												<li>Enhanced Undo/Redo support</li>
												<li>Enhanced Ontology Server Wizard</li>
												<li>More user-friendly UI for Attribute Browser</li>
												<li>Greater flexibility in expression data loading</li>
												<li>Ability to rename networks</li>
												<li>Re-organized menu system</li>
												<li>Many performance improvements and bug fixes</li>
											</ul>
											<a HREF="cyto_2_3_features.php">Cytoscape 2.3.2 Release Notes</a>.1
										</td>
										<td valign="top">
											<A HREF="screenshots/cytoscapeMainWindowv2_3.png">
												<img src="screenshots/cyto_2_3_thumb.png" border="0" align="left" alt="Cytoscape 2.3.2 Screenshot" />
											</A>
										</td>
									</tr>
								</table>
							</div>
							<p>
							<div class="item">
								<br>
								<p>
								<big><b>
									Announcing Cytoscape 2.2
								</b></big>
								<table width="100%">
									<tr valign="top">
										<td width="47%" valign="top">
											<br>
											(Updated 12/13/2005)
											<br>
											New Features include:
											<br>

											<ul>
												<li	>Improved node/edge attribute browsing.</li>
												<li>Cytoscape Graph Editor v1.0</li>
												<li>Support for
													<A HREF="http://www.geneontology.org/GO.downloads.shtml#ont"
														target="_blank">
														Gene Ontology OBO
													</A>
													and
													<A HREF="http://www.geneontology.org/GO.current.annotations.shtml"
														target="_blank">gene annotation (association)
													</A> files
												</li>
												<li>Cytoscape panels (CytoPanels) to ease window management</li>
												<li>New GML visual style to manage visual attributes from GML files</li>
												<li>Independent internal network windows for easy comparison</li>
												<li>Simplified mechanism for saving Visual Styles in between sessions</li>
												<li>Improved Attribute API (CyAttributes)</li>
												<li>Improved performance</li>
												<li>Many bugs fixed </li>
											</ul>
											<A HREF="cyto_2_2_features.php">Cytoscape 2.2 Release Notes</A>
										</td>
										<td valign="top">
											<A HREF="screenshots/cytoscapeMainWindowv2_2.png">
												<img src="screenshots/cyto_2_2_thumb.png" border="0" align="left" alt="Cytoscape 2.2 Screenshot" />
											</A>
										</td>
									</tr>
								</table>
							</div>
							<P>
								<div class="item">
									<big>
										<b>
											Genome Research article on Cytoscape
										</b>
									</big>
									<br>
									<br>
									<p>
										Genome Research has published a journal article on Cytoscape
										<a href="http://www.genome.org/cgi/content/full/13/11/2498">
											[Abstract]
										</a>
										<a href="http://www.genome.org/cgi/reprint/13/11/2498">
											[PDF]
										</a>
										<a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=14597658&amp;dopt=Abstract">
											[PubMed entry]</a>.
									</p>
									<p>
										<br>
									</p>
								</div>
								<p>

								<div class="item">
									<big><b><i>Updated!&nbsp;</i>(Mar. 9 2006)</i>&nbsp;&nbsp;Research using Cytoscape</b></big>
									&nbsp;&nbsp;
									[<a href="pubs.php">View All Publications</a>]
									<P>
									<br>
									<p>
										<b>Nature Aug. 2005</b>
			<br>
			Kristin C. Gunsalus, Hui Ge, Aaron J. Schetter, Debra S. Goldberg, 
			Jing-Dong J. Han, Tong Hao, Gabriel F. Berriz, Nicolas Bertin, Jerry Huang, 
			Ling-Shiang Chuang, Ning Li, Ramamurthy Mani, Anthony A. Hyman, Birte Sönnichsen, 
			Christophe J. Echeverri, Frederick P. Roth, Marc Vidal and Fabio Piano
			<br>
			Predictive models of molecular machines involved in Caenorhabditis elegans early embryogenesis
			<br>
			[<A HREF="http://www.nature.com/nature/journal/v436/n7052/abs/nature03876.html"/>Abstract</A>]
			[<A HREF="http://www.nature.com/nature/journal/v436/n7052/full/nature03876.html">Full Text</A>]
			[<A HREF="http://www.nature.com/nature/journal/v436/n7052/pdf/nature03876.pdf">PDF</A>]
			<br>
		<p>
									
									<br>
									<p>
										<b>Nature Nov. 2005</b>
			<br>
			Suthram S, Sittler T, Ideker T.
			<br>
			Plasmodium protein network diverges from those of other eukaryotes.
			<br>
			[<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=16267557&query_hl=5"/>Abstract</A>]
			[<A HREF="">Full Text</A>]
			[<A HREF="">PDF</A>]
			<br>
			<br>
		<p>

		<p>
			<b>J Comput Biol. Jul-Aug 2005</b>
			<br>
			Sharan R, Ideker T, Kelley B, Shamir R, Karp RM.
			<br>
			Identification of protein complexes by comparative analysis of yeast and bacterial protein interaction data.
			<br>
			[<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=16108720&query_hl=5"/>Abstract</A>]
			[<A HREF="http://www.liebertonline.com/doi/pdf/10.1089/cmb.2005.12.835">Full Text</A>]
			[<A HREF="http://www.liebertonline.com/doi/pdf/10.1089/cmb.2005.12.835">PDF</A>]
			<br>
			<br>
		<p>
		<p>
			<b>Genome Biology. July 2005</b>
			<br>
			Yeang CH, Mak HC, McCuine S, Workman C, Jaakkola T, Ideker T.
			<br>
			Validation and refinement of gene-regulatory pathways on a network of physical interactions.
			<br>
			[<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=15998451&query_hl=5"/>Abstract</A>]
			[<A HREF="http://www.pubmedcentral.gov/articlerender.fcgi?tool=pubmed&pubmedid=15998451">Full Text</A>]
			[<A HREF="http://www.pubmedcentral.gov/picrender.fcgi?artid=1175993&blobtype=pdf">PDF</A>]
			<br>
			<br>
		<p>

		<p>
			<b>Nature Biotechnology. May 2005</b>
			<br>
			Kelley R, Ideker T.
			<br>
			Systematic interpretation of genetic interactions using protein networks.
			<br>
			[<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=15877074&query_hl=5"/>Abstract</A>]
			[<A HREF="http://www.nature.com/nbt/journal/v23/n5/full/nbt1096.html">Full Text</A>]
			[<A HREF="http://www.nature.com/nbt/journal/v23/n5/pdf/nbt1096.pdf">PDF</A>]
			<br>
			<br>
		<p>
			<b>BMC Bioinformatics, July 2005</b>
			<br>
			David J Reiss , Iliana Avila-Campillo , Vesteinn Thorsson , Benno Schwikowski  and Timothy Galitski
			<br>
			Tools enabling the elucidation of molecular pathways active in human disease: Application to Hepatitis C virus infection
			<br>
			BMC Bioinformatics 2005, 6:154
			<br>
			[<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=15967031">Abstract</A>]
			[<A HREF="http://www.biomedcentral.com/1471-2105/6/154">Full Text</A>]
			[<A HREF="http://www.biomedcentral.com/content/pdf/1471-2105-6-154.pdf">PDF</A>]
			<br>
		<P>
								<br>
										<b>
											Note:
										</b>
										If you have a publication which makes use of Cytoscape, please let us know by sending an email to the
										<a href="http://groups-beta.google.com/group/cytoscape-discuss">
											cytoscape-discuss
										</a>
										mailing list.
								</div>
							</div>
						</div>
					</div>
				</td>
				<td>
<?
include "help.php";
echo "<P>";
if ($news_option == "atom") {
	include "feed.php";
	include "community_box.php";
} else {
	include "news.php";
	include "community_box.php";
}
echo "<P>";
include "collab.php";
?>
				</td>
			</tr>
		</table>
<? include "footer.php"; ?>
	</body>
</html>






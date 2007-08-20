<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<? include "config.php"; ?>
<! Updated by kono for 2.5 7_23_2007 >
<! Updated by apico with "latest release" variables for future updates (see config.php). 12_21_2006 >

<html>

	<head>
		<title>Cytoscape: Analyzing and Visualizing Network Data</title>
		<link href="css/cytoscape.css" type="text/css" rel="stylesheet" media="screen">
		<link href="images/cyto.ico" rel="shortcut icon">
		<meta name="keywords" content="Cytoscape, visualization, interaction network, software, genetic, gene, expression, protein interaction, graph, bioinformatics, computational biology, Whitehead Institute, Institute for Systems Biology, microarray analysis, clustering, pathways, integration, algorithm, simulated annealing, gene regulation">
	</head>

	<body>
		<!>
		<! ========== Title and menu bar ============ >
		<!>
		<div id="feature">
			<div class="title">
				Cytoscape
			</div>
			<img src="images/logo.png">
			<div class="article">
				<h2><a href="<?= $latest_download_link ?>">Download Cytoscape
            <?= $latest_version ?>
            ! </a></h2>
				<a href="<?= $latest_release_notes_link ?>"><?= $latest_version ?> Release Notes &raquo; </a>
				<h2><a href="retreat2007/index.php">Announcing Cytoscape Retreat 2007</a></h2>
				<a href="registration.php"><b>Registration now open!!!</b></a><br>
				<a href="retreat2007/index.php">Now first time in Europe!!! <b>Amsterdam - Netherlands &raquo;</b></a><br>
				<b>November 6<sup>th</sup>-9<sup>th</sup> 2007</b><br>
				</p></div>
		</div>
		<? include "nav.php"; ?><? include "detailed_nav.php"; ?>
		<!>
		<! ========== Main Contents ============ >
		<!>
		<div id="content">
			<div class="item">
				<h2><i>NEW!</i> &nbsp;&nbsp;Cytoscape 2.5.0</h2>
				<a href="screenshots/2_5_ss1.png"><img src="screenshots/2_5_ss1_thumb.png" alt="Cytoscape 2.5 Screenshot" align="left" border="0"> </a>
				<div class="paragraph">
					(Updated 7/23/2007) <br>
					New features include: <br>
					<ul>
						<li> New VizMapper User Interface
							<ul>
								<li> More intuitive.
								<li> Continuous mapping editors.
								<li> Visual editor for default view.
								<li> Visual mapping browser. 
								<li> Improved visual legend generator. 
								<li> Utilities to generate discrete values. 
							</ul>
						<li>New Features for Visual Style
							<ul>
								<li> Transparency (opactiy) support.
								<li> Continuous edge width.
								<li> Color visual property is separated from Arrow and Edge.
							</ul>
						<li>New Filter User Interface
							<ul>
								<li> Intuitive widgets for basic filters.
								
								<li> Suggested search values with indexing.
								
								<li> Options to save in session or globally.
							
							</ul>
						<li>Plugin Manager and New Plugin Website
							<ul>
								<li> Install/Update/Delete plugins from within Cytoscape.
								
								<li> Search for version compatible plugins from any host site.
								
								<li> Display list of installed plugins.
							
							</ul>
						<li>Layout customization.
						
						<li>Undo and Redo.
						
						<li>Group API for plugin developers.
						
						<li>Node stacking.
						
						<li>Tested on both Java SE 5 and 6
						
						<li> Many, many bug fixes! 
					
					</ul>
				</div>
			</div>
			<div class="item">
				<h2><i>NEW!</i> &nbsp;&nbsp;<a href="retreat2007/index.php">Cytoscape Retreat 2007!</a></h2>
				<a href="retreat2007/venue.php"><img src="retreat2007/images/magere-brug-small.jpg" alt="Amsterdam by night" border="0"> </a>
				<div class="paragraph">
						Now in Europe! November 6<sup>th</sup> - 9<sup>th</sup><br>
					 Including a public symposium on November 8<sup>th</sup>, with a formidable list of confirmed speakers among them
					<ul>
						<li>Leroy Hood 
						
						<li>Peter Sorger 
						
						<li>Ewan Birney 
					
					</ul>
					 Hosted by the <a href="http://www.humangenetics-amc.nl" target="_blank">Human Genetics Department of the Academic Medical Center</a> in the vibrant historic city of <a href="/retreat2007/venue.php">Amsterdam</a>. </div>
			</div>
			<div class="item">
				<h2>Cytoscape 2.4.1</h2>
				<div class="paragraph">
					No new features, but several bugs have been fixed. </div>
			</div>
			<div class="item">
				<h2>Cytoscape 2.4.0 </h2>
				<a href="screenshots/2_4_ss1.png"><img src="screenshots/2_4_ss1_thumb.png" alt="Cytoscape 2.4.0 Screenshot" align="left" border="0"> </a>
				<div class="paragraph">
					(Updated 1/16/2007) <br>
					New features include: <br>
					<ul>
						<li> Publication quality image generation.
							<ul>
								<li> Node label position adjustment.
								
								<li> Automatic Visual Legend generator.
								
								<li> Node position fine-tuning by arrow keys.
								
								<li> The ability to override selected VizMap settings. 
							
							</ul>
						<li>Quick Find plugin.
						
						<li>New icons for a cleaner user interface.
						
						<li>Consolidated network import capabilities.
							<ul>
								<li> Import network from remote data sources (through http or ftp).
							
								<li> Default support for the following file formats: SBML, BioPAX, PSI-MI, Delimited text, Excel.
						
							</ul>
						<li>New Ontology Server.
							<ul>
								<li> Native support for OBO format ontology files. 
								
								<li> Ability to visualize the ontology tree as a network (DAG).
								
								<li> Full support for Gene Association files. 
							
							</ul>
						<li>Support for Java SE 5
						
						<li> Many, many bug fixes! 
					
					</ul>
					</ul>
      See the <a href="cyto_2_4_features.php">Release Notes</a> for more detail. </div>
			</div>
			<div class="item">
				<h2>Publications about Cytoscape</h2>
				<div class="paragraph">
					Shannon P, Markiel A, Ozier O, Baliga NS, Wang JT, Ramage D, Amin N, Schwikowski B, Ideker T.<br>
					<strong>Cytoscape: a software environment for integrated models of biomolecular interaction networks.</strong><br>
					Genome Research 2003 Nov; 13(11):2498-504<br><br>
					<a href="http://www.genome.org/cgi/content/full/13/11/2498"> [Abstract] </a>
					<a href="http://www.genome.org/cgi/reprint/13/11/2498"> [PDF] </a>
					<a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=14597658&amp;dopt=Abstract"> [PubMed entry]</a>.
				</div>
			</div>
			<div class="item">
				<h2><i>Updated!&nbsp;</i>(July 24 2007)</i>&nbsp;&nbsp;Research using Cytoscape</h2>
				
				<div class="paragraph">
				
					<strong>As of July 2007, 281 publications are citing 
					<a href="http://www.genome.org/cgi/content/full/13/11/2498">Shannon et al. (2003)</a>.</strong><br>
					<ul>
						<li><a href="http://scholar.google.com/scholar?hl=en&lr=&cites=3669641697993554798">View Full Listing at Google Scholar</a></li>
						<li><a href="http://www.pubmedcentral.nih.gov/tocrender.fcgi?action=cited&artid=403769">Link to Listing at PubMed Central</a></li>
						<li><a href="http://highwire.stanford.edu/cgi/searchresults?fulltext=cytoscape&andorexactfulltext=and&author1=&pubdate_year=&volume=&firstpage=&src=hw&searchsubmit=redo&resourcetype=1&search=Search&fmonth=Jan&fyear=1844&tmonth=Jul&tyear=2007&fdatedef=1+January+1844&tdatedef=24+Jul+2007">
								Link to HighWire Press</a>
					</ul>
									
					<p>[<a href="pubs.php">Link to Publications Page</a>]</p>
					
					<p><br>
						<b> Note: </b> If you have a publication which makes use of Cytoscape, please let us know by sending an email to the <a href="http://groups-beta.google.com/group/cytoscape-discuss"> cytoscape-discuss </a> mailing list. 
    </p>
				</div>
			</div>
			<div class="item">
				<div class="paragraph">
					 Past news articles are available <a href="past_news.php">here.</a></div>
			</div>
		</div>
		<div id="rightbox">
			<?
include "help.php";

if ($news_option == "atom") {
	include "feed.php";
} else {
	include "news.php";
	
}
?><?
include "community_box.php";
include "collab.php";
?></div>
		<p><? include "footer.php"; ?></p>
	</body>

</html>

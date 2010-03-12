<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<? include "config.php"; ?>

<html>

	<head>
		<script type="text/javascript" src="http://www.google.com/jsapi"></script>
		<script type="text/javascript" src="feedReader.js"></script>
		<title>Cytoscape: Analyzing and Visualizing Network Data</title>
		<link href="css/cytoscape.css" type="text/css" rel="stylesheet" media="screen">
		<link href="images/cyto.ico" rel="shortcut icon">
		<meta name="keywords" content="Cytoscape, visualization, interaction network, 
		software, genetic, gene, expression, protein interaction, graph, bioinformatics, 
		computational biology, Whitehead Institute, Institute for Systems Biology, 
		microarray analysis, clustering, pathways, integration, algorithm, 
		simulated annealing, gene regulation, complex network">
	</head>

	<body>
<div id="container">
<div id="feature">
<div class="title">Cytoscape</div>
<img src="images/logo.png">

<div class="article">
<h3><a href="<?= $latest_download_link ?>">Download Cytoscape <?= $latest_version ?>!</a></h3>

<a href="<?= $latest_release_notes_link ?>"><?= $latest_version ?>   Release Notes &raquo; </a></div>
</div>

<? include "nav.php"; ?> <? include "detailed_nav.php"; ?>


<div id="main">


<div class="item">
<h2><b><i>NEW!</i></b> Cytoscape 2.7.0</h2>
<div id="paragraph">
<b>A new Cytoscape release with cool new functionality! </b>
New features include:
<ul>
<li>Nested Networks</li>
<li>New Edge Types</li>
<li>Newlines and list editing in attribute browser</li>
<li>Automatic label wrap</li>
<li>Arrow color optionally locked to edge color</li>
<li>CyCommandHandlers</li>
<li>BioPAX Level 3 support</li>
</ul>
For further details on each item check the <a href="cyto_2_7_features.php">release notes"</a>!
</div>
</div>


<div class="item">
<h2>Cytoscape, Agilent, and UCSD in ...</h2>
<div id="paragraph">
<i><b><a href="images/CytoscapeDDN.pdf">Drug Discovery News</a></b></i>
</div>
</div>


<div class="item">
<h2>Cytoscape 2.6.3</h2>
<div id="paragraph">
<b>This is an emergency release of Cytoscape specifically for Apple OS X.  This release
fixes bugs introduced with Apple's latest 
<a href="http://support.apple.com/kb/HT1222">security update</a> for Java.  </b>
<p>
The problems that we have fixed are:
<ul>
<li>Misshapen windows and misshapen panels within windows.</li>
<li>Disappearing menu options.</li>
</ul>
It is important to understand that we have only fixed the core Cytoscape application and
that some plugins may still exhibit these problems.  We're working with plugin developers
to solve any remaining problems.


</div>
</div>


<div class="item">

<h2>Cytoscape 2.6.2</h2>
<div id="paragraph">
This is a bug-fix release that should work with all 2.6.x plugins. There are no API changes.
<p>
Some issues that have been addressed in this release include: 
<ul>
<li>Fixed the webstart on Mac OS X problem.</li>
<li>Fixed Linkout for Linux when no default browser is set.</li>
<li>Fixed bugs: 1865, 1939, 1940, 1927, 1858, 1957, 1917, among others.</li>
<li>Improved handling of metanodes.</li>
<li>Improvements in the attribute browser that so that new line characters can now be specified.</li>
<li>Improved all Cytoscape layouts to support graph partitioning, selected only, and so that
configuration parameters are saved.</li>
<li>Fixed various documentation issues.</li>
<li>Fixed URL loading issues related to file type.</li>
<li>Many other minor improvements.</li>
</ul>

</div>
</div>

<div class="item">

<h2></i>Cytoscape 2.6.1</h2>
<div id="paragraph">
This is a minor bug-fix release that should work with all 2.6.0 plugins.
<p>
There are no API changes, however we have added a new centralized logging facility. 
See <b>Help-&gt;Error Console</b> to see Cytoscape's startup messages and any warnings or errors 
that occur during operation.  
<p>
Some Bugs that have been fixed include:
<ul>
	<li>Topology filter performance</li>
	<li>XGMML loading consistency</li>
	<li>Mac usability issues</li>
</ul>

</div>
</div>


<div class="item">
<h2>Cytoscape 2.6.0</h2>
<a href="screenshots/2_6_ss1.png"> <img
	src="screenshots/2_6_ss1_thumb.png" alt="Cytoscape 2.6 Screenshot" />
</a>

<div id="paragraph">(Updated 4/11/2008) New features include: <br>
<ul id="paragraph">
	<li><strong>Web Service Client Manager</strong></li>
	<ul>
		<li>Seamless access to Pathway Commons, IntAct, and NCBI Entrez
		Gene.</li>
		<li>Synonym import from BioMart.</li>
	</ul>
	<li><strong>Cytoscape Themes</strong></li>
	<li><strong>Dynamic Filters</strong></li>
	<li><strong>Network Manager supports multiple network
	selection</strong></li>
	<li><strong>Label Positioning has been improved</strong></li>
	<li><strong>Session saving occurs in memory</strong></li>
	<li><strong>XGMML loading/saving optimized</strong></li>
	<li><strong>Linkout integrated with attribute browser</strong></li>
	<li><strong>Extra sample Visual Styles using new visual
	properties</strong></li>
	<li><strong>Many, many bug fixes!</strong></li>
</ul>
<a href="cyto_2_6_features.php">Cytoscape 2.6.0 release notes</a></div>

</div>


<div class="item">
<h2>Cytoscape 2.5.2</h2>
<div id="paragraph">This is a bug-fix release that addresses
issues related to XGMML loading and parsing old vizmap.props files.
Enjoy!</div>
</div>

<div class="item">
<h2>Cytoscape 2.5.1</h2>
<div id="paragraph">A point release to address a variety of bugs
in the 2.5.0 release.</div>
</div>


<div class="item">
<h2>Cytoscape 2.5.0</h2>
<a href="screenshots/2_5_ss1.png"> <img
	src="screenshots/2_5_ss1_thumb.png" alt="Cytoscape 2.5 Screenshot" />
</a>
<div id="paragraph">(Updated 7/23/2007) New features include: <br>
<ul id="paragraph">
	<li><strong>New VizMapper User Interface</strong></li>
	<ul>
		<li>More intuitive</li>
		<li>Continuous mapping editors</li>
		<li>Visual editor for default view</li>
		<li>Visual mapping browser</li>
		<li>Improved visual legend generator</li>
		<li>Utilities to generate discrete values</li>
	</ul>

	<li><strong>New Features for Visual Style</strong></li>
	<ul>
		<li>Transparency (opactiy) support</li>
		<li>Continuous edge width</li>
		<li>Color visual property is separated from Arrow and Edge</li>
	</ul>

	<li><strong>New Filter User Interface</strong></li>
	<ul>
		<li>Intuitive widgets for basic filters</li>
		<li>Suggested search values with indexing</li>
		<li>Options to save in session or globally.
	</ul>

	<li><strong>Plugin Manager and New Plugin Website</strong></li>
	<ul>
		<li>Install/Update/Delete plugins from within Cytoscape</li>
		<li>Search for version compatible plugins from any host site</li>
		<li>Display list of installed plugins</li>
	</ul>

	<li><strong>Layout customization</strong></li>
	<li><strong>Undo and Redo</strong></li>
	<li><strong>Group API for plugin developers</strong></li>
	<li><strong>Node stacking</strong></li>
	<li><strong>Tested on both Java SE 5 and 6</strong></li>
	<li><strong>Many, many bug fixes!</strong></li>
</ul>
</div>
</div>

<div class="item">
<h2>Publications about Cytoscape</h2>

<div id="paragraph">
<div class="pub">Melissa S Cline, Michael Smoot, Ethan Cerami,
Allan Kuchinsky, Nerius Landys, Chris Workman, Rowan Christmas, Iliana
Avila-Campilo, Michael Creech, Benjamin Gross, Kristina Hanspers, Ruth
Isserlin, Ryan Kelley, Sarah Killcoyne, Samad Lotia, Steven Maere, John
Morris, Keiichiro Ono, Vuk Pavlovic, Alexander R Pico, Aditya Vailaya,
Peng-Liang Wang, Annette Adler, Bruce R Conklin, Leroy Hood, Martin
Kuiper, Chris Sander, Ilya Schmulevich, Benno Schwikowski, Guy J Warner,
Trey Ideker & Gary D Bader
<h3>Integration of biological networks and gene expression data
using Cytoscape</h3>
Nature Protocols 2, 2366 - 2382 (2007) Published online: 27 September
2007 | doi:10.1038/nprot.2007.324<br>
<br>
<a href="http://www.ncbi.nlm.nih.gov/pubmed/17947979"> [PubMed
entry]</a>.</div>
</div>

<div id="paragraph">
<div class="pub">Shannon P, Markiel A, Ozier O, Baliga NS, Wang
JT, Ramage D, Amin N, Schwikowski B, Ideker T.<br>
<h3>Cytoscape: a software environment for integrated models of
biomolecular interaction networks.</h3>
Genome Research 2003 Nov; 13(11):2498-504<br>
<br>
<a href="http://www.genome.org/cgi/content/full/13/11/2498">
[Abstract] </a> <a href="http://www.genome.org/cgi/reprint/13/11/2498">
[PDF] </a> <a
	href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=14597658&amp;dopt=Abstract">
[PubMed entry]</a>.</div>
</div>
</div>
<div class="item">
<h2><i>Updated!&nbsp;</i>(July 24 2007)</i> Research using Cytoscape</h2>

<div id="paragraph"><strong>As of July 2007, 281
publications are citing <a
	href="http://www.genome.org/cgi/content/full/13/11/2498">Shannon et
al. (2003)</a>.</strong><br>
<ul id="paragraph">
	<li><a
		href="http://scholar.google.com/scholar?hl=en&lr=&cites=3669641697993554798">View
	Full Listing at Google Scholar</a></li>
	<li><a
		href="http://www.pubmedcentral.nih.gov/tocrender.fcgi?action=cited&artid=403769">Link
	to Listing at PubMed Central</a></li>
	<li><a
		href="http://highwire.stanford.edu/cgi/searchresults?fulltext=cytoscape&andorexactfulltext=and&author1=&pubdate_year=&volume=&firstpage=&src=hw&searchsubmit=redo&resourcetype=1&search=Search&fmonth=Jan&fyear=1844&tmonth=Jul&tyear=2007&fdatedef=1+January+1844&tdatedef=24+Jul+2007">
	Link to HighWire Press</a>
</ul>

<p>[<a href="pubs.php">Link to Publications Page</a>]</p>

<p><br>
<b> Note: </b> If you have a publication which makes use of Cytoscape,
please let us know by sending an email to the <a
	href="http://groups-beta.google.com/group/cytoscape-discuss">
cytoscape-discuss </a> mailing list.</p>
</div>
</div>
<div class="item">
<div id="paragraph">Past news articles are available <a
	href="past_news.php">here.</a></div>
</div>
</div>


<div id="rightbox"><? include "help.php"; ?> 
<?
			if ($news_option == "atom") {
				include "feed.php";
			} else {
				include "news.php";
			}
?> 
<? include "community_box.php"; include "collab.php"; ?></div>
<p><? include "footer.php"; ?></p>
</div>
</body>

</html>

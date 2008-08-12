<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<title>Past News</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/cytoscape.css" />
<link rel="shortcut icon" href="images/cyto.ico" />
</head>

<body bgcolor="#FFFFFF">
<div id="container">
<div id="topbar">
<div class="title">Cytoscpae Past News</div>
</div>
<? include "nav.php"; ?>

<!-- Main Contents -->
<br>
<br>
<div class="item">
<h2><a href="retreat2007/index.php">Cytoscape Retreat 2007!</a></h2>
<a href="retreat2007/venue.php"> <img
	src="retreat2007/images/magere-brug-small.jpg" alt="Amsterdam by night" />
</a>
<div id="paragraph">Now in Europe! November 6<sup>th</sup> - 9<sup>th</sup><br>
Including a public symposium on November 8<sup>th</sup>, with a
formidable list of confirmed speakers among them
<ul id="paragraph">
	<li>Leroy Hood
	<li>Peter Sorger
	<li>Ewan Birney
</ul>
Hosted by the <a href="http://www.humangenetics-amc.nl" target="_blank">Human
Genetics Department of the Academic Medical Center</a> in the vibrant
historic city of <a href="/retreat2007/venue.php">Amsterdam</a>.</div>
</div>

<div class="item">
<h2>Cytoscape 2.4.1</h2>
<div id="paragraph">No new features, but several bugs have been
fixed.</div>
</div>

<div class="item">
<h2>Cytoscape 2.4.0</h2>
<a href="screenshots/2_4_ss1.png"><img
	src="screenshots/2_4_ss1_thumb.png" alt="Cytoscape 2.4.0 Screenshot"
	align="left" border="0"> </a>
<div id="paragraph">(Updated 1/16/2007) <br>
New features include: <br>
<ul id="paragraph">
	<li>Publication quality image generation.
	<ul>
		<li>Node label position adjustment.
		<li>Automatic Visual Legend generator.
		<li>Node position fine-tuning by arrow keys.
		<li>The ability to override selected VizMap settings.
	</ul>
	<li>Quick Find plugin.
	<li>New icons for a cleaner user interface.
	<li>Consolidated network import capabilities.
	<ul>
		<li>Import network from remote data sources (through http or
		ftp).
		<li>Default support for the following file formats: SBML, BioPAX,
		PSI-MI, Delimited text, Excel.
	</ul>
	<li>New Ontology Server.
	<ul>
		<li>Native support for OBO format ontology files.
		<li>Ability to visualize the ontology tree as a network (DAG).
		<li>Full support for Gene Association files.
	</ul>
	<li>Support for Java SE 5
	<li>Many, many bug fixes!
</ul>

See the <a href="cyto_2_4_features.php">Release Notes</a> for more
detail.</div>
</div>


<div class="item">
<h2>Cytoscape 2.3.2</h2>
<div class="paragraph">(Updated 9/1/2006)<br>
This release fixes a bug that made it impossible to save session files
on Windows systems. No new features.</div>
</div>

<div class="item">
<h2>Cytoscape 2.3.1</h2>
<div class="paragraph">(Updated 7/21/2006)<br>
No major new features, just bug fixes and some behind-the-scenses
refactoring.</div>
</div>
<div class="item">
<h2>Cytoscape 2.3</h2>
<div class="paragraph">(Updated 06/21/2006)<br>
New Features include:<br>
<ul>
	<li>High-performance rendering engine. Support for large networks
	(100,000+ nodes & edges)</li>
	<li>Ability to save a session</li>
	<li>Support for network attributes</li>
	<li>An improved command line interface.</li>
	<li>The GraphMerge plugin included by default</li>
	<li>Enhanced context or pop-up menus for nodes</li>
	<li>A rewritten <i>bird's eye view</i> of the network that is
	enabled by default</li>
	<li>Enhanced Undo/Redo support</li>
	<li>Enhanced Ontology Server Wizard</li>
	<li>More user-friendly UI for Attribute Browser</li>
	<li>Greater flexibility in expression data loading</li>
	<li>Ability to rename networks</li>
	<li>Re-organized menu system</li>
	<li>Many performance improvements and bug fixes</li>
</ul>
<a HREF="cyto_2_3_features.php">Cytoscape 2.3.2 Release Notes</a> <A
	HREF="screenshots/cytoscapeMainWindowv2_3.png"> <img
	src="screenshots/cyto_2_3_thumb.png" border="0" align="left"
	alt="Cytoscape 2.3.2 Screenshot" /></A></div>
</div>
<div class="item">
<h2>Announcing Cytoscape 2.2</h2>
<div class="paragraph">(Updated 12/13/2005)<br>
New Features include:<br>
<ul>
	<li>Improved node/edge attribute browsing.</li>
	<li>Cytoscape Graph Editor v1.0</li>
	<li>Support for <A
		HREF="http://www.geneontology.org/GO.downloads.shtml#ont"
		target="_blank"> Gene Ontology OBO </A> and <A
		HREF="http://www.geneontology.org/GO.current.annotations.shtml"
		target="_blank">gene annotation (association) </A> files</li>
	<li>Cytoscape panels (CytoPanels) to ease window management</li>
	<li>New GML visual style to manage visual attributes from GML
	files</li>
	<li>Independent internal network windows for easy comparison</li>
	<li>Simplified mechanism for saving Visual Styles in between
	sessions</li>
	<li>Improved Attribute API (CyAttributes)</li>
	<li>Improved performance</li>
	<li>Many bugs fixed</li>
</ul>
<A HREF="cyto_2_2_features.php">Cytoscape 2.2 Release Notes</A> <A
	HREF="screenshots/cytoscapeMainWindowv2_2.png"> <img
	src="screenshots/cyto_2_2_thumb.png" border="0" align="left"
	alt="Cytoscape 2.2 Screenshot" /> </A></div>
</div>
</div>

<? include "footer.php"; ?>

</body>
</html>

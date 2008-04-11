<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>

	<head>
		<title>Cytoscape 2.6 Release Notes</title>
		<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css">
		<link rel="shortcut icon" href="images/cyto.ico">
	</head>

	<body>
	<div id="container">
	
	<div id="topbar">
		<div class="title">Cytoscape 2.6 Release Notes</div>
	</div>

	<? include "nav.php"; ?>
	
	<br>
	<ul>
		<h3>Web Service Client Manager</h3>
			<ul>
				<li>New framework to integrate web service clients into Cytoscape</li>
				<li>Web Service client plugins for downloading networks from 
					PathwayCommons, IntAct, and NCBI Entrez Gene.</li>
				<li>Annotation import web service plugin for BioMart. This is mainly for 
					ID translation/synonym mapping</li>
			</ul>
						
		<h3>Cytoscape Themes</h3>
		Themes are bundles of plugins that are all
		related or work towards a common goal.  Themes that we have created include a
		<strong>WebService theme</strong> that bundles all available web services into a single installable
		unit and a <strong>Nature Protocols theme</strong> that bundles all plugins needed
		to execute the protocol described in our recent
		<a href="http://www.ncbi.nlm.nih.gov/pubmed/17947979">Nature Protocols publication</a>.
		<h3>Dynamic Filters</h3>
		As changes to filters are made, the filters get applied dynamically to the network view. 
		As you move sliders, you'll see the nodes and edges get selected as you move them.
		<h3>Network Manager supports multiple network selection</h3>
		For operations like layout and network deletion, you can now select more than one network in the
		network manager and have the action applied to all selected networks.
		<h3>Label Positioning has been improved</h3>
		<h3>Session saving occurs in memory</h3>
		<h3>XGMML loading/saving optimized</h3>
		<h3>Linkout integrated with attribute browser</h3>
		<h3>Extra sample Visual Styles using new visual properties</h3>
		<h3>Many, many bug fixes!</h3>
	</ul>


	<? include "footer.php"; ?>
	</div>
	</body>
</html>

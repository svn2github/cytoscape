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
			<ul>
				<li>Pack multiple plugins in a Theme and install all of them at once</li>
			</ul>
		<h3>Dynamic Filters</h3>
		<h3>Network Manager supports multiple network selection</h3>
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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<! Updated for 2.5 by kono >
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<title>Cytoscape Screen Shots</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css" />
<link rel="shortcut icon" href="images/cyto.ico" />
</head>
<body bgcolor="#FFFFFF">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
  <tr>
    <td width="10">&nbsp;</td>
    <td valign="center"><h1>Past News</h1></td>
  </tr>
</table>
<? include "nav.php"; ?>

<! =========== Main Contents ============= >
<div id="content">
	<div class="item">
		<h2>Cytoscape 2.3.2</h2>
		<div class="paragraph">
			(Updated 9/1/2006)<br>
			This release fixes a bug that made it impossible to save session files on Windows systems.  No new features.	
		</div>
	</div>
	
	<div class="item">
	<h2>Cytoscape 2.3.1</h2>
		<div class="paragraph">
		(Updated 7/21/2006)<br>
		No major new features, just bug fixes and some behind-the-scenses refactoring.	
		</div>
	</div>
	<div class="item">
		<h2>Cytoscape 2.3</h2>
		<div class="paragraph">
			(Updated 06/21/2006)<br>
			New Features include:<br>
			<ul>
				<li>High-performance rendering engine.  Support for large networks (100,000+ nodes & edges)</li>
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
			<a HREF="cyto_2_3_features.php">Cytoscape 2.3.2 Release Notes</a>
			<A HREF="screenshots/cytoscapeMainWindowv2_3.png">
			<img src="screenshots/cyto_2_3_thumb.png" border="0" align="left" alt="Cytoscape 2.3.2 Screenshot" /></A>
		</div>
	</div>
	<div class="item">
		<h2>Announcing Cytoscape 2.2</h2>
		<div class="paragraph">
			(Updated 12/13/2005)<br>
			New Features include:<br>
			<ul>
				<li>Improved node/edge attribute browsing.</li>
				<li>Cytoscape Graph Editor v1.0</li>
				<li>Support for
					<A HREF="http://www.geneontology.org/GO.downloads.shtml#ont"
					target="_blank">
					Gene Ontology OBO
					</A>
					and <A HREF="http://www.geneontology.org/GO.current.annotations.shtml"
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
			<A HREF="screenshots/cytoscapeMainWindowv2_2.png">
			<img src="screenshots/cyto_2_2_thumb.png" border="0" align="left" alt="Cytoscape 2.2 Screenshot" />
			</A>
		</div>
	</div>
</div>

<? include "footer.php"; ?>
</body>
</html>

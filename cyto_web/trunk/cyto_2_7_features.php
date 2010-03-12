<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>

	<head>
		<title>Cytoscape 2.7 Release Notes</title>
		<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css">
		<link rel="shortcut icon" href="images/cyto.ico">
	</head>

	<body>
	<div id="container">
	
	<div id="topbar">
		<div class="title">Cytoscape 2.7 Release Notes</div>
	</div>

	<? include "nav.php"; ?>
	
	<br>
	<ul>
		<h3> Nested Networks </h3>
A node may now have a reference to another Network, which allows us to capture the relationships between networks in networks themselves. This feature includes a new file format (NNF) for nested network I/O and direct nested network editing via the Editor and Right-click menu. A Visual Property has been added to provide control of the visualization of the nested network.
					

		<h3> New Edge Types </h3> 
						
Several new edge types between solid and dashed have been added.
								

		<h3> Newlines and list editing in attribute browser </h3> 
									
The attribute browser has been updated to allow newline characters to be added by pressing the "Enter" key. List editing is now also enabled.
											
		<h3> Automatic label wrap </h3> 
A new visual property has been added that sets the width of a label. Any label extending beyond this width will be automatically wrapped.
														
		<h3> Arrow color optionally locked to edge color </h3> 
															
Arrow color may now be bound to the edge color by checking a box in the Dependencies pane of the Default Appearance Browser in the VizMapper, which avoids the necessity of creating separate-yet-identical mappings for edge, source, and target arrows.
																	
		<h3> CyCommandHandlers </h3> 
Addition of a mechanism to the core to provide inter-plugin communication
																							

		<h3> BioPAX Level 3 support </h3> 


	</ul>


	<? include "footer.php"; ?>
	</div>
	</body>
</html>

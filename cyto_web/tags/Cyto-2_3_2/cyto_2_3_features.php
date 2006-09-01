<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<! Created by kono@ucsd.edu for Cytoscape 2.3 release. >

<html>
<head>
	<title>Cytoscape 2.3 Release Notes</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css">
	<link rel="shortcut icon" href="images/cyto.ico">
</head>
<body bgcolor="#FFFFFF">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tr>
		<td width="10"></td>
		<td valign="center">
			<h1>Cytoscape 2.3 Release Notes</h1>
		</td>
	</tr>
</table>
<? include "nav.php"; ?>
<div id="indent">
<p>
	Cytoscape 2.3 is the latest release of the open source bioinformatics software 
	platform for visualizing molecular interaction networks and integrating these
	interactions with gene expression profiles and other state data.
</p>
<P>
	The 2.3 release of Cytoscape includes:<br>
	<ul>
		<li	>High-performance rendering engine.  Support for large networks (100,000+ nodes & edges)</li>
		<li>Ability to save a session</li>
		<li>Support for network attributes</li>
		<li>An improved command line interface</li>
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
</p>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>High-performance rendering engine</b></big>
		<p>
			New rendering engine enables users to draw, navigate, and manipulate huge networks in real time.   
			On modern workstations Cytoscape can render networks with 100,000+ nodes and edges.
		</p>
	</td>
</tr>
</table>
<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Ability to save a session</b></big>
		<p>
			From this version, Cytoscape's basic data file is <strong><i>.cys (CYtoscape Session)</i></strong> file.  This is a zip archive 
			which includes the following data files:
			<ul>
				<li>cysession.xml - File for saving desktop states</li>
				<li>Property Files</li>
				<ul>
					<li>session_vizmap.props - Visual Styles</li>
					<li>session_cytoscape.props - Preference values</li>
				</ul>
				<li>Network files in XGMML format - Networks and attributes</li>
			</ul>

			With session files users can save everything in the workspace, including networks, attributes, visual styles, 
			properties, and relationships between networks (shown in the Network Panel) into a single .cys file.
		</p>
	</td>
</tr>
</table>
<P>

<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Support for network attributes</b></big>
		<p>
			In addition to node and edge attributes, Cytoscape supports attributes for networks.  This includes network
			metadata in RDF (Resource Description Framework). 
		</p>
	</td>
</tr>
</table>

<P>

<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>An improved command line interface</b></big>
		<p>
			The new command line simplifies the option specification.  	
		</p>
	</td>
</tr>
</table>

<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>The GraphMerge plugin included by default</b></big>
		<p>
			<a href="plugins2.php">GraphMerge plugin</a> is now a core plugin.
		</p>
	</td>
</tr>
</table>

<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Enhanced context or pop-up menus for nodes</b></big>
		<p>
			The <i>LinkOut</i> plugin provides enhanced mechanism to link nodes to external web resources within Cytoscape.
		</p>
	</td>
</tr>
</table>
<P>

<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>A rewritten <i>bird's eye view</i> of the network</b></big>
		<p>
			Rewritten <i>bird's eye view</i> window enables users to browse huge network more efficiently.
		</p>
	</td>
</tr>
</table>

<P>

<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Enhanced Undo/Redo support</b></big>
		<p>
			New Undo manager provides better undo/redo function.
		</p>
	</td>
</tr>
</table>

<P>

<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Enhanced Ontology Server Wizard</b></big>
		<p>
			Enhanced Gene Ontology wizard has more intuitive user interface for importing 
			<a href="http://www.geneontology.org/GO.downloads.shtml#ont" target="_blank">Gene Ontology</a> data files.
		</p>
	</td>
</tr>
</table>

<P>

<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>More user-friendly UI for Attribute Browser</b></big>
		<p>
			Network attribute browser panel is added.  Attribute import related functions are attached to new icons on browser.
		</p>
	</td>
</tr>
</table>

<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Greater flexibility in expression data loading</b></big>
		<p>
			Users can specify key value for mapping expression matrix data.
		</p>
	</td>
</tr>
</table>

<P>

<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Ability to rename networks</b></big>
		<p>
			Network names can be modified from the Network Panel.
		</p>
	</td>
</tr>
</table>

<P>

<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Re-organized menu system</b></big>
		<p>
			Menu items have been re-organized to better match common applications like Firefox and Word. 
		</p>
	</td>
</tr>
</table>

<P>

<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
	<tr>
		<td>
			<big><b>Core Plugins:</b></big>
			<p>
				Seven "core" plugins are bundled and distributed with Cytoscape 2.3. These
				plugins offer fundamental operations of value to many users,
				and are included in the basic distribution.
			</p>
			<center>
			<table style="margin-left: 30;margin-right:30;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
    		<tr>
    			<td>
    				<p><b>Plugin Name</b></p>
    			</td>
    			<td width="172">
    				<p><b>JAR file</b></p>
    			</td>
    			<td style="width: 281px;">
    				<p><b>Description</b></p>
    			</td>
  			</tr>
  			<tr valign="top">
      			<td width="132">
      				<p>Filter</p>
      			</td>
      			<td width="172">
      				<p>filter.jar</p>
      			</td>
      			<td width="281">
      				<p>
      					Provides filtering functionality; adds filter icon, filters 
      					menu and filtering dialog box.
      				</p>
      			</td>
    		</tr>
    		<tr valign="top">
      			<td width="132">
      				<p>Attribute Browser</p>
      			</td>
      			<td width="172">
      				<p>browser.jar</p>
      			</td>
      			<td width="281">
      				<p>
      					Provides attribute browser functionality; adds browser menu item under Data.
      				</p>
      			</td>
    		</tr>
    		<tr valign="top">
      			<td width="132">
      				<p>Cytoscape Editor</p>
      			</td>
      			<td width="172">
      				<p>CytoscapeEditor.jar</p>
      			</td>
      			<td width="281">
      				<p>
      					Provides network editor functionality; adds setEditor menu item under File.
      				</p>
      			</td>
    		</tr>
    		<tr valign="top">
					<td width="132">
      			<p>cPath</p>
      		</td>
      		<td width="172">
      			<p>cpath.jar</p>
      		</td>
      		<td width="281">
      			<p>
							Provides aility to directly query, retrieve and visualize interactions retrieved from 
							the <a href="http://cbio.mskcc.org/cpath/cytoscape.do" target="_blank">cPath</a> database.</p>
      		</td>
    		</tr>
    		<tr valign="top">
      			<td style="width: 132px;">
      				<p>yFiles Layouts</p>
      			</td>
      			<td width="172">
      				<p>yLayouts.jar</p>
      			</td>
      			<td style="width: 281px;">
      				<p>
      					Provides yFiles layouts functionality; adds
						yFiles submenu to layout menu.
					</p>
					Layouts provided:<br>
					&nbsp; - Circular<br>
					&nbsp; - Organic<br>
&nbsp; - Hierarchic<br>
&nbsp; - Random<br>
&nbsp; - MirrorX<br>
&nbsp; - MirrorY<br>
&nbsp; - Orthogonal<br>
      </td>
    </tr>
    <tr valign="top">
      <td width="132">
      <p>Linkout</p>
      </td>
      <td width="172">
      <p>linkout.jar</p>
      </td>
      <td width="281">
      <p>
					Provides a right-click menu of hyper links for nodes and edges.
				</p>
      </td>
    </tr>
		<tr valign="top">
      <td width="132">
      <p>Graph Merge</p>
      </td>
      <td width="172">
      <p>GraphMerge.jar</p>
      </td>
      <td width="281">
      <p>
					Provides ability to merge networks and perform other set operations on networks.
				</p>
      </td>
    </tr>
    <tr valign="top">
      <td width="132">
      <p>Manual Layout</p>
      </td>
      <td width="172">
      <p>ManualLayout.jar</p>
      </td>
      <td width="281">
      <p>Provides align functionality.</p>
      </td>
    </tr>
		<tr valign="top">
      <td width="132">
      <p>Automatic Layout</p>
      </td>
      <td width="172">
      <p>AutomaticLayout.jar</p>
      </td>
      <td width="281">
      <p>
					Provides layout algorithms shown under menu items <i>JGraph Layouts</i> and <i>Cytoscape Layouts</i>.  
					The code for these algorithms is all open source.
				</p>
      </td>
    </tr>
    <tr valign="top">
      <td width="132">
      <p>Yeast
context-sensitive menu</p>
      </td>
      <td width="172">
      <p>yeast-context.jar</p>
      </td>
      <td width="281">
      <p>Provides
Yeast-specific web search capabilities.</p>
      </td>
    </tr>
</table>
</center>
</td>
</tr>
</table>
</div>
<? include "footer.php"; ?>
</body>
</html>

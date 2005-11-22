<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<! Created by kono@ucsd.edu for Cytoscape 2.2 release. >

<html>
<head>
	<title>Cytoscape 2.2 Release Notes</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css">
	<link rel="shortcut icon" href="images/cyto.ico">
</head>
<body bgcolor="#FFFFFF">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tr>
		<td width="10"></td>
		<td valign="center">
			<h1>Cytoscape 2.2 Release Notes</h1>
		</td>
	</tr>
</table>
<? include "nav.php"; ?>
<div id="indent">
<p>
	Cytoscape 2.2 is the latest release of the open source bioinformatics software 
	platform for visualizing molecular interaction networks and integrating these
	interactions with gene expression profiles and other state data.
</p>
<P>
	The 2.2 release of Cytoscape includes: Improved node/edge attribute browsing, 
	Cytoscape Graph Editor v1.0, Support for Gene Ontology OBO and gene annotation files, 
	Cytoscape panels (CytoPanels) to ease window management, Independent internal network 
	windows for easy comparison, New GML visual style to manage visual attributes from 
	GML files, and many bugs fixes.
</p>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Cytoscape panels (CytoPanels) to ease window management</b></big>
		<p>
			CytoPanels are floatable / dockable panels to cut down on the number of pop-up 
			windows within Cytoscape, and create a more unified user experience.  Cytoscape 
			2.2 includes three CytoPanels:
		</p>
	<ul>
  		<li>
			CytoPanel 1 (appears on the left).  By default, it has Network Tree Browser panel.
		</li>
  		<li>
  			CytoPanel 2 (appears on the bottom).  Node/Edge attribute browser will be shown here.
		</li>
		<li>
			CytoPanel 3 (appears on the right).  Mainly for advanced features.
		</li>
	</ul>
	</td>
</tr>
</table>
<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Improved node/edge attribute browsing:</b></big>
		<p>Improved Attribute Browser is on CytoPanel 2.  Users can browse, edit, 
		and export attributes in the table.</p>
	</td>
</tr>
</table>
<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Cytoscape Graph Editor v1.0</b></big>
		<p>New network files can be created visually by Cytoscape Editor.</p>
	</td>
</tr>
</table>

<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Support for Gene Ontology OBO and gene annotation files</b></big>
		<p>Now Cytoscape supports standard ontology and annotation file formats:</p>
	<ul>
  		<li>
  			<a href="http://www.geneontology.org/GO.downloads.shtml#ont" target="_blank" >
				OBO Ontology File
			</a>
		</li>
  		<li>
  			<a href="http://www.geneontology.org/GO.current.annotations.shtml" target="_blank">
  				Gene Association (annotation) File
  			</a>
		</li>
	</ul>
	Through the new Gene Ontology Server Wizard, users can select those files from GUI. 
	</td>
</tr>
</table>

<P>

<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>Independent internal network windows for easy comparison</b></big>
		<p>Ecah network is displayed in a independent window.</p>
	</td>
</tr>
</table>

<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
	<td>
		<big><b>New GML visual style to manage visual attributes from GML files</b></big>
		<p>
			When a gml file is loaded into Cytoscape, its visual properties will be registered
			as a new visual style. 
		</p>
	</td>
</tr>
</table>

<P>


<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
	<tr>
		<td>
			<big><b>Core Plug-Ins:</b></big>
			<p>
				Five "core" plug-ins are bundled and distributed with Cytoscape 2.2. These
				plug-ins offer fundamental operations of value to many users,
				and are included in the basic distribution.
			</p>
			<center>
			<table style="margin-left: 30;margin-right:30;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
    		<tr>
    			<td>
    				<p><b>Plug-In Name</b></p>
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
      <p>Align</p>
      </td>
      <td width="172">
      <p>control.jar</p>
      </td>
      <td width="281">
      <p>Provides
alignment/distribution functionality; adds align menu item to layout
menu and align/distribute dialog box.</p>
      </td>
    </tr>
    <tr valign="top">
      <td width="132">
      <p>Hierarchical
Layout</p>
      </td>
      <td width="172">
      <p>HierarchicalLayout.jar</p>
      </td>
      <td width="281">
      <p>Provides
hierarchical layout functionality; adds hierarchical layout menu item
to layout menu.</p>
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
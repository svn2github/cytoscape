<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>Cytoscape 2.1 Release Notes</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/cytoscape.css">
	<link rel="shortcut icon" href="images/cyto.ico">
</head>
<body bgcolor="#FFFFFF">
<table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
	<tr>
		<td width="10"></td>
		<td valign="center">
			<h1>Cytoscape 2.1 Release Notes</h1>
		</td>
	</tr>
</table>
<? include "nav.php"; ?>
<div id="indent">
<p>
Cytoscape 2.1 is the latest release
of the open source bioinformatics software platform for visualizing
molecular interaction networks and integrating these
interactions with gene expression profiles and other state data.</i></p>
<P>The 2.1 release of Cytoscape
includes: major performance improvements for loading, manipulating
and managing large networks; new layout and visual attribute
operations; support for new external data formats; usability
improvements; and bug fixes.</p>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
<td>
<big><b>Performance Improvements:</b></big>
<p>Performance
Optimization of Graph Model:</p>
<ul>
  <li>Large graphs are now supported
(&gt;100K
nodes, edges, depending on system)</li>
  <li>The 2.1 release features
significant
performance improvements for graph loading, creation, and miscellaneous
graph operations (&gt; 2 - 10x improvements for some operations)</li>
</ul>
</td>
</tr>
</table>
<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
<td>
<big><b>On-line Help System:</b></big>
<ul style="font-family: helvetica,arial,sans-serif;">
  <li>On-line help and content for
Cytoscape</li>
  <li>On-line help framework for plug-in
developers</li>
</ul>
<p>An on-line help system supporting topic-based, indexed,
context-sensitive and full-text search is now available. The on-line
help system is based on JavaHelp technology, and supports
the standard on-line help conventions, including "F1" (function
key F1) and Help menu activated help system invocation. Current help
content is derived and indexed from the Cytoscape 2.1 User Manual.</p>
</td>
</tr>
</table>
<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
<td>
<big><b>Usability Improvements:</b></big>
<p>Headless mode operation is now supported.  Headless
mode allows users to now run their own Cytoscape processing/analysis
jobs without creating the GUI and user interaction components or
requiring user intervention.</p>
<p>A new Threading/Task Framework and associated GUI components have been
added to the Cytoscape core architecture which allows for visual
feedback and user control for longer-running tasks. A progress bar
API is available for communicating activity and status to users for
operations which formerly blocked the GUI, thereby improving
usability and perceived performance of the application. Many load,
save, and layout operations now utilize the Threading/Task Framework.
</td>
</tr>
</table>
<P>
<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
<td>
<big><b>External Data Support:</b></big>
<P>A direct cPath Interface is available via a new cPath plug-in. </p>
<p>cPath
is a freely available, open-source protein-protein interaction
database from Memorial Sloan-Kettering Cancer Center. Using cPath, a
researcher can search for specific protein-protein interaction
records, inspect matching records, and export them to a third-party
database or visualization application, such as Cytoscape. 
<A HREF="http://www.cbio.mskcc.org/cpath/cytoscape.do">View cPath PlugIn Details</A>.
</td>
</tr>
</table>
<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
<td>
<big><b>Updated Technical Documentation:</b></big>
<ul>
  <li>Updated User Documentation</li>
  <li>Updated Javadocs.</li>
</ul>
<p>The 2.1 release of Cytoscape includes updates to the user and developer
documentation. Updated APIs and more complete JavaDoc's
capturing new, changed and deprecated APIs introduced by the core
changes in the Cytoscape 2.1 release are included for developers and
plug-in writers.</p>
</td>
</tr>
</table>
<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
<td>
<big><b>Updated VizMapper:</b></big>
<P>
<UL><LI>
Node label color control has been added.
</UL>
</td>
</tr>
</table>
<P>
<table style="margin-left: 30;margin-right:30;border: dotted gray 1px;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
<tr>
<td>
<big><b>Core Plug-Ins:</b></big>
<p>Five "core" plug-ins are bundled and distributed with Cytoscape 2.1. These
plug-ins offer fundamental operations of value to many users,
and are included in the basic distribution.
<BR>
<center>
<table style="margin-left: 30;margin-right:30;padding-left: 10px;font-size:small" cellpadding="5" cellspacing="5" width="90%">
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
      <p>Provides
filtering functionality; adds filter icon, filters menu and filtering
dialog box.</p>
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
      <p>Provides yFiles layouts functionality; adds
yFiles submenu to layout menu.</p>
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
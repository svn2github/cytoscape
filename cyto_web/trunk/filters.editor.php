<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
   <meta http-equiv="content-type" content=
   "text/html; charset=ISO-8859-1">
   <title>
	Cytoscape Online Tutorial
    </title>
    <link rel="stylesheet" type="text/css" media="screen" href=
  "css/cytoscape.css">
   <link rel="shortcut icon" href="images/cyto.ico">
  <META HTTP-EQUIV="CONTENT-TYPE" CONTENT="text/html; charset=utf-8">
  <META NAME="AUTHOR" CONTENT="Melissa Cline">
  <STYLE>
  <!--
    @page { size: 8.27in 11.69in; margin-right: 1.25in; margin-top: 1in; margin-bottom: 1in }
    P { margin-bottom: 0.08in; direction: ltr; color: #000000; widows: 0; orphans: 0 }
    P.western { font-family: "Nimbus Roman No9 L", "Times New Roman", serif; font-size: 12pt; so-language: en-US }
    P.cjk { font-family: "Times New Roman", serif; font-size: 12pt }
    P.ctl { font-family: "Times New Roman", serif; font-size: 10pt }
  -->
  </STYLE>
</HEAD>
<BODY LANG="en-US" TEXT="#000000" DIR="LTR">
  <table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
   <tr>
     <td width="10"> </td>
     <td valign="center">
        <h1>
  	Cytoscape Online Tutorial
        </h1>
     </td>
   </tr>
  </table>
  <? include "nav.php"; ?>
  <? include "nav_tut.php"; ?>
  <div id="indent">
  <center>
     <h2>Filtering and editing</h2>
  </center>
  <p>
<P> 
</P>
<P>If you have completed the <A HREF="getting.started.php">Getting Started</A> 
tutorial, this tutorial will introduce you to
some advanced basics under Cytoscape.  In this tutorial, you will:</P>
<UL>
  <LI>Apply filters to filter out low-confidence edges.</P>
  <LI>Merge two or more related networks with the GraphMerge plugin.</P>
  <LI>Perform basic edits using the Cytoscape graph editor.
</UL>
This tutorial features the following plugins:
<UL>
  <LI>The <A HREF="http://www.cytoscape.org/plugins/GraphMerge/GraphMerge.jar">
      Graph Merge</A> plugin from 
     <A HREF="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">
     The Ideker Lab</A></LI>
  <LI>The <A HREF="http://www.cytoscape.org/plugins/NetworkFilter/rowan.jar">
     Network Filter</A>	plugin by Rowan Christmas at the 
     <A HREF="http://www.systemsbiology.org/">Institute for Systems Biology</A>
</UL>
and the following datasets:
<UL>
  <LI><A HREF="tut/fetching.data/RUAL.subset.sif">RUAL.subset.sif</A>, a 
    portion of a human interaction dataset published in 
    <A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=16189514&query_hl=1">
    Rual et al, Nature.2005 Oct 20;437(7062):1173-8</A> and available at 
    <A HREF="http://www.cytoscape.org/cgi-bin/moin.cgi/Data_Sets/">
    http://www.cytoscape.org/ cgi-bin/moin.cgi/Data_Sets/</A>.</LI>
  <LI><A HREF="tut/fetching.data/STELZL.subset.sif">STELZL.subset.sif</A>, a 
    portion of a human interaction dataset published in 
    <A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=16169070&query_hl=3">
    Stelzl et al, Cell. 2005 Sep 23;122(6):957-68</A> and available at 
    <A HREF="http://www.cytoscape.org/cgi-bin/moin.cgi/Data_Sets/">
    http://www.cytoscape.org/cgi-bin/moin.cgi/Data_Sets/</A>.</LI>
</UL>
Before starting, please download these files to your computer.

<P>Begin by clicking here:&nbsp; <font size=+1> <a
href="tut/webstart/cyto2.2.211205.jnlp">WEB START</a> </font>&nbsp;
(approximate download size: 22 MB) This starts Cytoscape on your own
computer, after downloading the program and annotations from our
website.

<H3>Getting started, building merged networks</H3>
<OL>
  <LI><P> Start by loading the interaction dataset <B>STELZL.subset.sif</B>
  by selecting <B>Load</B> and <B>Network</B> under the <B>File</B>
  menu.  This file contains interactions related to the transcription
  factor protein TP53.   This should generate
  a figure similar to the one shown below:</P>
  <IMG SRC="tut/filters.editor/Fig1.jpg" WIDTH="30%"></P>
  
  <LI><P>Earlier, we applied
  the <B>Spring Embedded Layout</B> algorithm.  Experiment with some of
  the other layouts under the <B>Layout</B> menu. </P>
  <LI> <PR> Continue by loading the interaction dataset <B>RUAL.subset.sif</B>
  and choosing some layout algorithm.</P>

  <LI><P>Under the <B>Plugins</B> menu, select <B>Merge All Networks</B>.
  This should bring up a popup window labeled <B>Merge Networks</B>.  In
  this window, under <B>Available Networks</B>, click on
  <B>RUAL.subset.sif</B>, and click on the right-pointing arrow to add
  this network to <B>Selected Networks</B>, as shown.</P>
  <<P><IMG SRC="tut/filters.editor/Fig2.png"  WIDTH="20%"></P>

  <LI><P>Repeat to select <B>STELZL.subset.sif</B>. Then, to 
  proceed with the merge, click on the up arrow, and then click on the 
  <B>OK</B> button.</P>
  <P>Under <B>CytoPanel 1</B> (at the left side of your Cytoscape
  Desktop window), there should now be an entry labeled <B>Merged
  Network</B>.  This entry is colored red, indicating 
  that no view was created by default (by default, Cytoscape creates no 
  view for large networks, and merged networks can be large).Right-click 
   on the <B>Merged Network</B> entry. This should bring up the menu
   shown below.
    Select <B>Create View</B>.</P>
    <IMG SRC="tut/filters.editor/Fig3.jpg" WIDTH="10%"><BR CLEAR=LEFT>
    <LI><P> How were these networks merged?  Anytime there was a node with
    the same ID in both networks, it was considered the same node.
   In both input networks, the nodes were labeled by Entrez Gene IDs.
    Note
    that your original networks contained 419 and 43 nodes respectively,
    while your merged network contains 453 nodes.  This suggests that
    there is little overlap between these experimental datasets, a common
    observation in proteomics.</P>
    <LI><P>Create a layout for your network using one of the layout algorithms under
    the <B>Layout</B> menu of your Cytoscape desktop.</P>
</OL>
<H3>Using Filters</H3>
<P>Your network contains
    several types of edges. From the Rual dataset, the edge types include
   <B>Y2H</B> yeast two-hybrid interactions, <B>coAP</B> GST pull-down
    interactions, and three types of literature-based interactions, listed
   in order of increasing confidence: <B>non-core, core, hypercore</B>.
    From the Stelzl dataset, you will see edges labeled <B>LacZ4</B> 
    (high-confidence yeast two-hybrid measurements), <B>SD4</B>
    (low-confidence yeast two-hybrid measurements), and <B>GST_PullDown</B>
    (mass spec measurements). We will now use Cytoscape's filters to remove 
    the lower-confidence the <B>non-core</B> and <B>SD4</B> edges</P>
<OL>
    <LI><P>Look at the edge
    types under the <B>Edge Attribute Browser</B> by selecting several
    edges.  Remember that to select edges, you must first go to the
    <B>Select</B> menu on the Cytoscape Desktop, and then to <B>Mouse
    Drag Selects...</B></P>
    <LI><P>Note that there should be 1139 edges in your Merged Network.</P>
    <LI><P>Bring up the <B>Use Filters</B> window by selecting <B>Filters</B> 
    and <B>Use Filters</B> under the Cytoscape desktop.</P>
    <LI><P>Click on the button labeled <B>Create New Filter</B>.</P>
    <LI><P>The <B>Filter Creation Dialog</B> will now appear.  
    Select <B>String Filter</B>  and click <B>OK</B>.</P>
    <IMG SRC="tut/filters.editor/Fig4.png"  WIDTH=310 HEIGHT=209></P>
    <LI><P>Under the <B>Use Filters</B> popup, you should now see an entry 
    labeled <B>Pattern:</B> in the available filters. Click on it. </P>
    <LI><P>On the right side of the window, under the section labeled
    <B>String Pattern Filter</B>, fill in the following:</P>
      <OL>
        <LI><P>For <B>Select graph objects of type</B>, select
        <B>Edge</B>.</P>
        <LI><P>For <B>with a value for text attribute</B>, select
        <B>interaction</B>.</P>        
        <LI><P>For <B>that matches the pattern</B>, enter <B>non_core</B>,
         and then click on the <B>Apply selected filter</B> button. </P>
        <IMG SRC="tut/filters.editor/Fig5.png"  WIDTH=459 HEIGHT=193></P>
        <LI><P>Verify that 660 edges have been selected</P>
        <LI><P>Under <B>Edit</B> in the Cytoscape Desktop, select
        <B>Delete Selected Nodes/Edges</B>.</P>
        <LI><P>Repeat to delete the edges of type SD4.  Verify that this
        selects 7 of the remaining 479 edges.  After deleting these edges,
        there should be 492 edges remaining</B>
    </OL>
    <LI><P>Apply the <B>Organic</B> layout under <B>Layout</B> and 
    <B>yFiles</B>  This should generate a network such as shown below:</P>
    <IMG SRC="tut/filters.editor/Fig6.jpg" WIDTH="30%">
    <LI>Notice that at the bottom of the canvas, there are several nodes
    with no edges.  Select these nodes with your mouse and delete them.
    <LI><P>Notice also some nodes connected by multiple
    edges.  This indicates that these nodes are connected by two different
    types of edges.  Since we have removed the lower-confidence interactions,
    this means that the nodes are connected according to two or more 
    higher-confidence methods.  Such interactions can be considered 
    especially reliable.</P>
<H3>Editing</H3>
<P>At times, it can be very useful to modify a network slightly: to
add or remove nodes or edges.  This section will describe how to do
so. </P>
<OL>

  <LI><P>Under the <B>File</B> menu in the Cytoscape Desktop, select
  <B>SetEditor</B>, and <B>DefaultCytoscapeEditor</B>.  <B>CytoPanel
  1</B> should now appear as shown below. </P>
  <IMG SRC="tut/filters.editor/Fig23.png" ALIGN=BOTTOM WIDTH=245 HEIGHT=317>

  <LI><P>Add a node to the canvas by clicking the left mouse button on
  the node labeled <B>Add a Node</B>, and holding down the left mouse
  button, dragging it onto the Cytoscape canvas.  You should see a
  new, blank node on your canvas.</P>
  <LI><P>Select the node with your mouse.  In the <B>Node Attribute
  Browser</B>, you should see a node with ID of <B>node0</B>, as shown
  below:</P>
  <IMG SRC="tut/filters.editor/Fig24.png" WIDTH=472 HEIGHT=197><BR CLEAR=LEFT>
  <LI><P>Give your new node a name by going to the <B>Node Attribute
  Browser</B>, clicking on the entry for the name, and entering a name
  under the column labeled <B>Official</B>.  Notice that you cannot
  enter a new internal ID for the node.</P>
  <LI><P>Enter an edge between your new node and some other node, as
  follows:</P>
  <OL>
    <LI><P>In the Cytoscape  Editor, click on <B>Directed Edge</B>.</P>
    <LI><P>Holding down the left mouse button, drag the mouse from
    <B>Directed Edge</B> to your new node on the Cytoscape canvas.
    When you release the left mouse button, you should see two things:
    when the mouse is on top of your node, it should have a thick
    black border; and as you move your mouse away from the node, a
    thick black line should follow the mouse.  Click on another node,
    and a new edge should appear between the two nodes.</P>
    <LI><P>To undo your new edge, under the Cytoscape desktop select
    <B>Edit</B> and <B>Undo</B>.</P>
    <LI><P>Create several new edges between your node and existing nodes.</P>
    <LI><P>Delete your new node (and any edges attached to it) by
    selecting the node with your mouse, and selecting <B>Delete
    Selected Nodes/Edges</B>. Note that if you delete a node, any
    edges running to it are also deleted.</P>
</OL>
</OL>
<P><B>Congratulations!</B>  You are now finished the advanced course
in Cytoscape menu operation.  That is worth at least a nice snack!
</BODY>
</HTML>

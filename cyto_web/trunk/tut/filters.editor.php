<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
   <meta http-equiv="content-type" content=
   "text/html; charset=ISO-8859-1">
   <title>
	Cytoscape Online Tutorial
    </title>
    <link rel="stylesheet" type="text/css" media="screen" href=
  "../css/cytoscape.css">
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
  <LI>Perform basic edits using the Cytoscape graph editor.
</UL>
This tutorial features the following plugins:
<UL>
  <LI>The <A HREF="http://www.cytoscape.org/plugins/NetworkFilter/rowan.jar">
     Network Filter</A>	plugin by Rowan Christmas at the 
     <A HREF="http://www.systemsbiology.org/">Institute for Systems Biology</A>
</UL>
and the following datasets:
<UL>
  <LI><A HREF="filters.editor/RUAL.subset.sif">RUAL.subset.sif</A>, a 
    portion of a human interaction dataset published in 
    <A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=16189514&query_hl=1">
    Rual et al, Nature.2005 Oct 20;437(7062):1173-8</A> and available at 
    <A HREF="http://www.cytoscape.org/cgi-bin/moin.cgi/Data_Sets/">
    http://www.cytoscape.org/ cgi-bin/moin.cgi/Data_Sets/</A>.</LI>
    <LI>Its attribute set <A HREF="fetching.data/RUAL.na">RUAL.na</A>, 
        available at the same site.
</UL>
Before starting, please download these datasets to your computer.

<P>Begin by clicking here:&nbsp; <font size=+1> <a
href="webstart/cyto2.2.060106.jnlp">WEB START</a> </font>&nbsp;
(approximate download size: 22 MB) This starts Cytoscape on your own
computer, after downloading the program and annotations from our
website.
<? include "first.time.php"; ?>

Following the steps outlined in the <A HREF="getting.started.php">Getting 
Started</A> tutorial,
<OL>
   <LI> Load the network <B>RUAL.subset.sif</B> by going to the <B>File</B> menu
        on the Cytoscape desktop, then <B>Load</B>, and then <B>Network</B>, and then
        specifying the location you have downloaded the file to.
   <LI> Load the node attribute file <B>RUAL.na</B> by going to the <B>File</B> menu,
        then <B>Load</B>, and then <B>Node Attributes</B>.
   <LI> Generate a spring-embedded layout for your network.
</OL>
   Your Cytoscape window should now appear as shown:
   <P><IMG SRC="filters.editor/Fig1.jpg" WIDTH=30%>
   <P>You can see the node attributes as follows:
    <OL>
    	<LI> The node attribute file you have just loaded defines the official node
    	name, called <B>Official</B>.  To see how this attribute is specified, open
    	the file <B>RUAL.na</B> with your favorite text editor.
    	<LI> In the Cytoscape desktop, under the <B>Node Attribute Browser</B>, 
    	click on the <B>Select Attributes</B> button.
    	<LI> Notice the attribute named <B>Official</B>.  Select it by clicking on
    	it with the left mouse button.
    	<LI> Exit the menu with the right mouse button.
    	<LI> You should now see two columns in the <B>Node Attribute Browser</B>, one
    	labeled <B>ID</B> and one labeled <B>Official</B>.  Select some nodes on the 
    	Cytoscape canvas.  You should see their IDs (Entrez gene IDs, in this case),
    	and their official gene names.
    </OL>
<H3>Using Filters</H3>
<P>Your network contains
    several types of edges: 
   <B>Y2H</B> yeast two-hybrid interactions, <B>coAP</B> GST pull-down
    interactions, and three types of literature-based interactions, listed
   in order of increasing confidence: <B>non-core, core, hypercore</B>.
   Verify this by examining the edge types under the <B>Edge Attribute
   Browser</B>.  Remember that to select edges, you must first go to the
    <B>Select</B> menu on the Cytoscape Desktop, and then to <B>Mouse
    Drag Selects...</B></P>
<P>
    We will now use Cytoscape's filters to remove 
    the lower-confidence the <B>non-core</B> edges</P>
<OL>
    <LI><P>Select <B>Filters</B> and <B>Use Filters</B> under the
          under the Cytoscape desktop.  This should bring up the
         <B>Use Filters</B> popup window.</P>
    <LI><P>In the <B>Use Filters</B> window, click on the button labeled
           <B>Create New Filter</B>.</P>
    <LI><P>The <B>Filter Creation Dialog</B> will now appear.  
    Select <B>String Filter</B>  and click <B>OK</B>.</P>
    <P><IMG SRC="filters.editor/Fig2.png"  WIDTH="30%"></P>
    <LI><P>Under the <A>Available Filters</B> menu in the <B>Use Filters</B> window, you should now see an entry 
    labeled <B>Pattern:</B>, as shown (below the highlighted line).
    <P><IMG SRC="filters.editor/Fig3.jpg"  WIDTH="60%"></P>

    <LI>Click on the line labeled <B>Pattern:</B>, which will 
        immediately change to say <B>Node: null ~</B>. </P>
    <LI><P>On the right side of the window, under the section labeled
    <B>String Pattern Filter</B>, fill in the following:</P>
      <OL>
        <LI><P>For <B>Select graph objects of type</B>, select
        <B>Edge</B>.</P>
        <LI><P>For <B>with a value for text attribute</B>, select
        <B>interaction</B>.</P>        
        <LI><P>For <B>that matches the pattern</B>, enter <B>non_core</B>,
         and then click on the <B>Apply selected filter</B> button. </P>
        <IMG SRC="filters.editor/Fig5.png"  WIDTH="60%"</P>
        <LI><P>Verify that 660 edges have been selected</P>  Your Cytoscape 
            window should appear as shown below. </P>
            <P><IMG SRC="filters.editor/Fig6.jpg"  WIDTH="40%"></P>
        <LI><P>Under <B>Edit</B> in the Cytoscape Desktop, select
        <B>Delete Selected Nodes/Edges</B>. 
        <LI>Your Cytoscape window should now appear as shown:</P>
            <P><IMG SRC="filters.editor/Fig7.jpg"  WIDTH="40%"></P>
    </OL>
    Compared to the network you started with, the network you have now has fewer
    edges, but all the edges are determined either through experimentation or by
    higher-confidence literature-based methods.  For some types of analysis, this is
    a more appropriate set of edges.
    <P>
    This will leave you with several nodes with no edges, which you may
    now want to filter out.  Here is one method for doing so.
    <OL>
       <LI> Create another <B>String Pattern</B> filter to select 
            objects of type <B>Edge</B> with a value for 
            text attribute <B>interaction</B> that matches the wildcard pattern 
            <B>*</B>, as shown below
            <P><IMG SRC="filters.editor/Fig8.jpg" WIDTH="60%"></P>
        <LI> This filter should select every edge on the canvas.  Click the
             <B>Apply Selected Filter</B> button, and verify that all
             edges are selected.
        <LI> Under the <B>Select</B> menu, select <B>To New Network</B>
             and <B>Selected nodes, Selected edges</B>.  This should create
             a "child" network of 257 nodes and 429 edges.
        <LI> Apply spring-embedded layout.  This should generate a network
             as shown.
            <P><IMG SRC="filters.editor/Fig9.jpg" WIDTH="40%"></P>
 
    </OL>
    Note that this will not filter out nodes that only have self-edges.
    But at this point, such nodes are easy to select with the mouse, and
    delete.
<H3>Editing</H3>
<P>At times, it can be very useful to modify a network slightly: to
add or remove nodes or edges.  For instance, if you have prior knowledge
on some biological process, you might want to add some nodes for proteins
that you know are involved in the process, but that don't appear in your dataset.
This section will describe how to do so. </P>
<OL>

  <LI><P>Under the <B>File</B> menu in the Cytoscape Desktop, select
  <B>SetEditor</B>, and <B>DefaultCytoscapeEditor</B>.  <B>CytoPanel
  1</B> should now appear as shown below. </P>
  <IMG SRC="filters.editor/Fig23.png" ALIGN=BOTTOM WIDTH=245 HEIGHT=317>

  <LI><P>Add a node to the canvas by clicking the left mouse button on
  the node labeled <B>Add a Node</B>, and holding down the left mouse
  button, dragging it onto the Cytoscape canvas.  You should see a
  new, blank node on your canvas.</P>
  <LI><P>Select the node with your mouse.  In the <B>Node Attribute
  Browser</B>, you should see a node with ID of <B>node0</B>, as shown
  below:</P>
  <IMG SRC="filters.editor/Fig24.png" WIDTH=472 HEIGHT=197><BR CLEAR=LEFT>
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
    <LI><P>Create several new edges between your node and existing nodes.</P>
    <LI><P>Delete your new node (and any edges attached to it) by
    selecting the node with your mouse, and selecting <B>Delete
    Selected Nodes/Edges</B>. Note that if you delete a node, any
    edges running to it are also deleted.</P>
</OL>
</OL>
<P><B>Congratulations!</B>  You are now finished the advanced course
in Cytoscape menu operation.  That is worth at least a nice snack!
<P>
<? include "tut.footer.php"; ?>
<? include "../footer.php"; ?>
</BODY>
</HTML>

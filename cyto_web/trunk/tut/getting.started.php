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
     <META NAME="AUTHOR" CONTENT="Melissa Cline">
     <META NAME="CREATED" CONTENT="20051104;21140000">
     <META NAME="CHANGED" CONTENT="20051220;16053000">
     <STYLE>
     <!--
		@page { size: 8.27in 11.69in; margin-right: 1.25in; margin-top: 1in; margin-bottom: 1in }
		P { margin-bottom: 0.08in; direction: ltr; color: #000000; text-align: left; widows: 0; orphans: 0 }
		P.western { font-family: "Nimbus Roman No9 L", "Times New Roman", serif; font-size: 12pt; so-language: en-US }
		P.cjk { font-family: "Times New Roman", serif; font-size: 12pt }
		P.ctl { font-family: "Times New Roman", serif; font-size: 10pt }
	-->
	</STYLE>
</HEAD>
<BODY LANG="en-US" TEXT="#000000" DIR="LTR">
     <table id="feature" border="0" cellpadding="0" cellspacing=
     "0" summary="">
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
	   <h2> Getting Started </h2>
	</center>
	<p>

This tutorial introduces basic network browsing and menu navigation.
In this tutorial, you will learn about the following:
<ul>
  <li> Loading networks and generating network layouts </li>
  <li> Selecting and querying specific nodes and edges </li>
  <li> Setting visual properties </li>
</ul>
This tutorial and accompanying lectures were delivered at 
<A HREF="http://www.csc.fi/suomi/info/index.phtml.en">CSC</A>, the Finnish IT center for science</A> 
The lecture slides of background material are
available <A HREF="getting.started.ppt">here</A> and an accompanying video
presentation is 
<A HREF="http://rm.tv.funet.fi:8080/ramgen/fi/csc/kurssit/2005/cytoscape/_cytos01.rm">available</A>
courtesy of the CSC.
<P>
This tutorial features this dataset
<A HREF="getting.started/RUAL.subset.sif">RUAL.subset.sif</A>, a 
    portion of a human interaction dataset published in 
    <A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=16189514&query_hl=1">
    Rual et al, Nature.2005 Oct 20;437(7062):1173-8</A> and available at 
    <A HREF="http://www.cytoscape.org/cgi-bin/moin.cgi/Data_Sets/">
    http://www.cytoscape.org/ cgi-bin/moin.cgi/Data_Sets/</A>
    Before starting, please download this dataset and its companion
    attribute file <A HREF="getting.started/RUAL.na">RUAL.na</A> to 
    your computer.
<p>
Begin by clicking here: 
&nbsp;
<font size=+1>
<a href="webstart/cyto2.2.060106.jnlp">WEB START</a>
</font>&nbsp; (approximate download size: 22 MB) This starts Cytoscape on your own computer, after downloading the
program and annotations from our website. (On subsequent runs, the program or
annotation will not be downloaded again unless we have new versions or new
annotation for you to use.) If Cytoscape does not start, please look at the
<a href="tutorial.php">instructions</a> for some places to get help.<p> If at any point you wish
to restart this tutorial, just click again on the <b>WEB START</b> link just above.
<? include "first.time.php"; ?>
<P>
This tutorial and accompanying lectures were delivered at 
<A HREF="http://www.csc.fi/math_topics/">Computer Science Center</A> in 
Helsinki, Finland.  The lecture slides of background material are
available <A HREF="getting.started.ppt">here</A> and an accompanying video
presentation will be available soon.
<P>
<H3> Initial steps</H3>
Launch Cytoscape. You should see a window that looks like this:  

<P  STYLE="margin-left: 0.2in; margin-bottom: 0in"><IMG SRC="getting.started/Fig1.png" WIDTH=196 HEIGHT=185><BR CLEAR=LEFT>

<P>Load the network <B>RUAL.subset.sif</B> into Cytoscape by selecting <B>Load</B> 
under the <B>File</B> menu, then selecting <B>Network</B>, and then specifying the location of the file you just downloaded.
This network consists of 1089 interactions observed
between 419 human proteins, and is a small subset of a large human interaction
dataset,  
published in <A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=16189514&query_hl=1">Rual et al, Nature.2005 Oct 20;437(7062):1173-8</A>.  The interaction data
itself is available at
<A HREF="http://www.cytoscape.org/cgi-bin/moin.cgi/Data_Sets/">
http://www.cytoscape.org/cgi-bin/moin.cgi/Data_Sets/</A>.
This subset of interactions consist of proteins that interact with the transcription factor protein TP53.
Now, your screen should look like this: 
<P><IMG SRC="getting.started/Fig2.png" WIDTH="25%"><BR CLEAR="LEFT">

<P>Click <B>Close</B> on the <B>Loading Network</B> popup window.  Under the
<B>Layout</B> menu, select <B>Apply Spring Embedded Layout</B>, and
<B>All Nodes</B>.  After a brief calculation, your screen should look
like this:</P>
<P><IMG SRC="getting.started/Fig3.png" WIDTH="25%"><BR CLEAR=LEFT>
<H3>Selection</H3>
<P>In the Cytoscape canvas (the blue window showing the network
graphic), you can select nodes by clicking on them with the mouse, or
dragging with the left mouse button.  Select a few nodes, and move
them around the screen.</P>
<P>You can select edges in the Cytoscape canvas (the blue portion of
the screen showing the network) by going to the <B>Select</B> menu,
selecting <B>Mouse Drag Selects</B>, and selecting either <B>Edges</B>
or <B>Nodes and Edges</B>.  Select a few edges with the mouse by
clicking on them directly, or using the mouse to define a select
region that intersects them.</P>
<H3>Cytopanel navigation</H3>
Notice that the very bottom of this network is hidden by CytoPanel 2, which is labeled
<B>Node Attribute Browser</B>.  Move this panel into a separate window, as follows:</P>
<OL>
	<LI><P>Locate the <B>Float Window</B> control in the upper left corner of
	CytoPanel 2, shown: <IMG SRC="getting.started/Fig4.png" WIDTH="3%" BORDER=1><BR CLEAR=LEFT></P>

	<LI><P>Click
	on this control.  Now, you should have two Cytoscape windows: the
	Cytoscape Desktop, and a new window labeled CytoPanel 2, similar to
	the one shown below.  This browser will display attributes of
	selected nodes. Select a few nodes.</P>
        <IMG SRC="getting.started/Fig5.png" WIDTH="25%"><BR CLEAR=LEFT></P>

	<LI><P>Notice
	that CytoPanel 2 now has a <B>Dock Window</B> control, shown below. 
	Click on this control to dock CytoPanel 2, and then click on the
	<B>Float</B> control to un-dock it. </P>
        <IMG SRC="getting.started/Fig6.png" HSPACE=12 WIDTH=31 HEIGHT=18 BORDER=1><BR CLEAR=LEFT BORDER=1></P>
	<LI><P>In your network
	window, locate the <B>maximize</B> control (as shown), and click on
	it to maximize your network window.</P>
	<IMG SRC="getting.started/Fig7.png" WIDTH=21 HEIGHT=22 BORDER=1><BR CLEAR=LEFT>
	</P>
	<LI><P>To center your network in this window, click on the icon in the top menu bar: </P>
	<IMG SRC="getting.started/Fig8.png" WIDTH=47 HEIGHT=44 BORDER=1>
</OL>
Your Cytoscape Desktop window should now appear as follows: 
<P><IMG SRC="getting.started/Fig9.png" WIDTH="25%"><BR CLEAR=LEFT>
<H3>More on Node Selection</H3>
<P>The nodes in this network are identified by numeric Entrez IDs
The node representing TP53 is numbered 7157.  Select this node as follows:</P>
<OL>
	<LI><P>Under the <B>Select</B>
	menu, select <B>Nodes</B>, and <B>By Name</B>.  
	</P>
	<LI><P>A popup windowshould appear labeled <B>Select Nodes by Name</B>.</P>
	<LI><P>Enter 7157, and click <B>Search</B>.</P>
	<IMG SRC="getting.started/Fig10.png" WIDTH=395 HEIGHT=93></P>
	<BR CLEAR=LEFT>
	<LI><P>The node in the center of the screen should turn yellow. Close the popup window.</P>
	<LI><P>Un-select this node by clicking on the background of the Cytoscape canvas.</P>
	<LI><P>Type <B>Ctrl-F</B>.
	 The <B>Select Nodes by Name</B> popup should reappear.  <B>Ctrl-F</B>
	is a keyboard shortcut for selecting a node by name.  Many Cytoscape
	menu options have keyboard shortcuts; they are listed at the right
	side of the pull-down menus.</P>
	<LI><P>Re-select node 7157 (TP53).</P>
	<LI><P> Select the nodes that interact directly with TP53, as follows:</P>
	<OL>
		<LI><P>Under the <B>Select</B> menu, select <B>Nodes</B>,and 
		<B>First neighbors of selected nodes</B>. You should see a network with several 
		yellow nodes in the center, as shown.  </P>
		<IMG SRC="getting.started/Fig11.png" WIDTH=346 HEIGHT=302><BR CLEAR=LEFT>

		<LI><P>In <B>CytoPanel 1</B> at the left side of the window, you should see
		the following.  This indicates that of 419 nodes in your network,
		64 are currently selected.  Your network also contains 1089 edges,
		none of which are currently selected.</P>
		<IMG SRC="getting.started/Fig12.png" WIDTH=246 HEIGHT=79><BR CLEAR=LEFT>

	</OL>
	<LI>Copy the selected nodes and their edges into a separate network by selecting <B>To New Network</B> under the <B>Select</B> menu.
	<LI><P>Clean up your canvas:</P>
	<OL>
		<LI><P>Maximize your subnetwork window.</P>
		<LI><P>Select <B>Layout</B>, <B>Apply Spring Embedded Layout</B>, and 
		<B>All Nodes. </B> </P>
		<LI><P>Use the Zoom control to zoom into this network.</P>
		<LI><P>Your display should now appear as shown.</P>
		<IMG SRC="getting.started/Fig13.png" HSPACE=12 WIDTH=346 HEIGHT=278><BR CLEAR=LEFT></P>
	</OL>
</OL>
<H3>Loading Node Attributes</H3>
<P>The nodes of this network are identified by numeric IDs. Looking
closely at these nodes, you will see their numeric labels.  A
companion attribute file maps these numeric IDs to standard gene
names. Load this attribute file as follows:</P>
<OL>
	<LI><P>Under the <B>File</B> menu, select <B>Load</B>, and then <B>Node Attributes</B>.	</P>
	<LI><P>Select the file <A HREF="RUAL.na">RUAL.na</A>, and click <B>Open</B>.  </P>
	<LI><P>A popup window labeled <B>Loading Node Attributes</B> will appear.  When this
	window reports <B>Status: Done</B>, click <B>Close</B>.</P>
	<LI><P>Look at the file	<A HREF="RUAL.na">RUAL.na</A> with your favorite text 
	editor to examine its format.</P>
</OL>
View these attributes in Cytoscape as follows:
<OL>
	<LI><P>Go to the <B>Node Attribute Browser</B> (CytoPanel 2), and click on
	<B>Select Attributes</B>. A pull-down menu should appear, similar to the one shown: </P>
	<IMG SRC="getting.started/Fig15.png" WIDTH=181 HEIGHT=143><BR CLEAR=LEFT> </P>
	<LI><P>In the pull-down	menu, left-click on <B>Official</B>.  Right-click to exit the menu.</P>
	<LI><P>Now, the Node Attribute Browser will show the IDs and official names of the nodes 
	selected in this network.  Click on Node 7157 at the center of the network window, and the 
	<B>Node Attribute Browser</B> should appear as shown. </P>
	<IMG SRC="getting.started/Fig16.png" HSPACE=12 WIDTH=507 HEIGHT=211><BR CLEAR=LEFT>
</OL>
<H3>Defining visual styles</H3>
<P>Now, we will	modify the visual styles to display the official node names as node labels.</P>
<OL>
	<LI><P>In the Cytoscape	Desktop, under the <B>Visualization</B> menu, select <B>Set Visual
	Properties</B>. The <B>Visualization Style</B>s popup window should appear, as shown.</P>
	<IMG SRC="getting.started/Fig17.png" WIDTH=320 HEIGHT=87><BR CLEAR=LEFT>
	<LI><P>Click on the <B>Duplicate</B> button to define a new style.  Name this style ???Class1???.</P>
	<LI><P>Click on the <B>Define</B> button.  This will bring up the <B>Set Visual	Properties</B> 
	popup window.</P>
	<LI><P>Click on the <B>Node Label</B> tab.</P>
	<LI><P>Under the <B>Mapping</B>	section, locate the <B>Map Attribute</B> pull-down menu, with
	<B>canonicalName</B> chosen by default.</P>
	<LI><P>Scroll down to <B>Official</B>, as shown, and click on it</P>
	<IMG SRC="getting.started/Fig14.png" WIDTH=227 HEIGHT=296>
	<LI><P>Click the button	labeled <B>Apply to Network</B>, followed by the <B>Close</B>
	button. Move the <B>Visual Styles</B> menu to the side.</P>
</OL>
The nodes should now be labeled with their official names.  Now, we will step through the zooming 
commands, to get a closer look. </P>
<OL>
	<LI><P>Zoom into the network using the <B>Zoom In</B> button (below)</P>
	<IMG SRC="getting.started/Fig18.png" WIDTH=51 HEIGHT=45><BR CLEAR=LEFT>
	<LI><P>You can also zoom out using the <B>Zoom Out</B> button, below:</P>
	<IMG SRC="getting.started/Fig19.png" WIDTH=50 HEIGHT=42><BR CLEAR=LEFT>
	<LI><P>Also, you can zoom to a selected region using the <B>Zoom Selected Region</B> button.</P>
	<IMG SRC="getting.started/Fig20.png" WIDTH=49 HEIGHT=39><BR CLEAR=LEFT>
</OL>
This dataset contains many types of edges: some representing
experimentally-determined interactions (Y2H and coAP, from yeast
two-hybrid and co-immunoprecipitation respectively), and some obtained
from the literature (non_core, core, and hyper_core, corresponding to
low, moderate, and high confidence literature search results).  We
will now represent each interaction by its type, as follows:</P>
<OL>
	<LI><P>Return to the <B>Visual Styles</B> window to further define your Class1 visual style.</P>
	<LI><P>Under the <B>Set	Visual Properties</B> popup window, click on the <B>Edge Attributes</B>
	button.</P>
	<LI><P>Select the Edge Color tab.</P>
	<LI><P>Under the <B>Mapping</B>	section, go to the pull-down labeled <B>None</B>, and scroll down
	to <B>BasicDiscrete</B>.</P>
	<LI><P>A menu should now appear listing <B>Map Attribute</B> as <B>interaction</B>,
	and listing the five types of interactions in this network (<B>Y2H,
	coAP, core, hyper_core, non_core</B>).  
	</P>
	<LI><P>Click the space next to <B>Y2H</B> to select a color for the Y2H edges.</P>
	<LI><P>Repeat for the other edge types.  Select your colors so that the Y2H and coAP
	colors are similar (e.g. green and blue), and the core, hyper_core,and non_core colors are 
	similar (e.g. orange, red, and pink).  This will allow you to see if each edge was determined 
	experimentally or through literature, and will further allow you to see the edge type.</P>
	Your <B>Set Visual Properties</B> window should now look similar to the one
	shown below.</P>
	<P><IMG SRC="getting.started/Fig21.png" WIDTH=265 HEIGHT=328><BR CLEAR=LEFT>		
	<LI><P>Click on <B>Apply to Network</B>, and click the <B>Close</B> button on the <B>Visual</B>
	<B>Styles</B> popup.</P>
	<LI><P>You should see a	network similar to the one below.  Which is the most common types
	of edge?  Least common?  
	<IMG SRC="getting.started/Fig22.png" WIDTH=276 HEIGHT=248><BR CLEAR=LEFT><BR>
	</P>
</OL>
<P> Notice that you can switch quickly between visual styles using the <B>Set Visual Properties</B> 
pull-down menu.  Switch between some of the pre-defined visual properties and watch how the canvas 
changes.</P>
<P>To see details on specific edges, perform the following:</P>
<OL>
	<LI><P>In <B>CytoPanel 2</B>, click on the <B>Edge Attribute 
         Browser</B> tab at the bottom
	 of the window.</P>
	<LI><P>In the <B>Cytoscape Desktop</B> window, under the <B>Select</B> 
        menu, choose <B>Mouse
	Drag Selects</B>, and <B>Edges</B>.</P>
	<LI><P>Select edges in the network window by clicking the left mouse 
        button in the network window, holding down the left mouse button 
        (this should produce a rectangle), and dragging the far corner of 
        the rectangle to intersect the selected edges.</P>
	<LI><P>Observe how the list of edges changes in the 
        <B>Edge Attribute Browser</B> as you
	select additional edges.</P>
</OL>
<P><B>Congratulations! </B>You have bravely survived the least-exciting 
portion of Cytoscape instruction: learning to navigate the menu system.  Go 
reward yourself with a cup of coffee!</P>
<P>
<? include "tut.footer.php"; ?>
<? include "../footer.php"; ?>
</BODY>
</HTML>
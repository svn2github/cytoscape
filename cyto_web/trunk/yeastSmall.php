<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content=
    "text/html; charset=ISO-8859-1">
    <title>
      Cytoscape Online Tutorial
    </title>
    <link rel="stylesheet" type="text/css" media="screen" href=
    "css/cytoscape.css">
    <link rel="shortcut icon" href="images/cyto.ico">
  </head>
  <body bgcolor="#FFFFFF">
    <table id="feature" border="0" cellpadding="0" cellspacing=
    "0" summary="">
      <tr>
        <td width="10">
           
        </td>
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
<h2> A Small Network from Saccharomyces cerevisiae </h2>
</center>

This tutorial provides a basic introduction to the Cytoscape platform. You will explore a small network of yeast proteins with some attached expression and annotation data;  explore the
<a href="http://www.geneontology.org">Gene Ontology</a> (GO) annotation of those
genes, and layout the graph and add interactions to the network based on the GO
annotation.<p>

Once you have completed this tutorial, the next tutorial will present some advanced tools for analyzing networks, the plugin system of Cytoscape, and how to load your own data.

<p>
Begin by clicking here: &nbsp;
<font size=+1>
<a href="tut/yeastSmall/yeastSmall.jnlp">WEB START</a>
</font>&nbsp; (download size: 7 MB) This starts Cytoscape on your own computer, after downloading the
program and annotation from our website. (On subsequent runs, the program or
annotation will not be downloaded again unless we have new versions or new
annotation for you to use.) If Cytoscape does not start, please look at the
<a href="tutorial.php">instructions</a> for some places to get help.<p> If at any point you wish
to restart this tutorial, just click again on the <b>WEB START</b> link just above.


<br><br> <center> <h3> Introduction </h3> </center> <br>

After clicking the web start link above, you should see a splash screen while the program is loading the network and data.<P>

The 29-gene network you now see before you, displayed in a Cytoscape
window,  is centered on the <b><i>GAL</i></b> genes from yeast, which
are involved in galactose metabolism.  This metabolic pathway, and its
regulation, is one of the better studied systems in yeast molecular
biology.<p>

The main portion of the window displays the network that was loaded. The status bar at the bottom of the window show the number of nodes and edges in the network, as well as the number of selected and hidden graph objects (more on this below). At the top of the window are a menu bar with a number of menus, and a toolbar containing icons for commonly used operations. Each icon has a tooltip describing its function.<P>

<b>BIOLOGICAL CONTEXT:</b>&nbsp;In the presence of
galactose, GAL4 activates a number of genes which in turn code enzymes
to convert galactose to glucose.  In the absence of
galactose, GAL80 represses GAL4; the downstream genes are consequently
repressed as well.  In the experiment displayed in this network,
galactose is present, but GAL4 has been knocked out.  &nbsp;  (This experiment
is only one condition of 20 in an experiment described
in  <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=11340206&dopt=Abstract">Integrated genomic and proteomic analyses of a
systematically perturbed metabolic network</a>, Ideker et al, 2001.)

<p>
In the displayed network,
<ul>
   <li> The knocked-out gene, <b>GAL4</b> is an octagon; all others are circles.
   <p><li> <font color='blue'> Blue </font> edges indicate protein-DNA interactions.
   <p><li><b>Black</b> edges indicate protein-protein interactions.
   <p><li> Node colors indicate ratios of mRNA expression.  Shades of <font color="red">
           red</font> indicate levels of repression, and
          <font color="green"> green</font> indicate levels of induction.

</ul>

<br>
<center>
<h3> Navigation </h3>
</center>

With the full network in view, it's difficult to see the details of any
particular node or edge (this is particularly true when viewing larger
networks). To zoom in, click this icon
<IMG src="tut/zoom_in36.gif" align="absbottom"> in the toolbar; to zoom out again,
click this icon
<IMG src="tut/zoom_out36.gif" align="absbottom">. When part of the network is off
the visible screen, scrollbars appear allowing you to scroll the display. You
can also pan and zoom using the mouse in the graph display window. Use middle-click and drag to pan the
view, right-click and drag right to zoom in, and right-click and drag left to
zoom out. To fit the entire graph to the screen, click this icon
<IMG src="tut/fit36.gif" align="absbottom"> in the toolbar.<P>

The labels for nodes only appear at a sufficiently high
zoom level. In this tutorial, Cytoscape is configured to display the common
gene name on each node. Try zooming in close enough to read the labels, and
find the GAL4 gene.

<center>
<h3> Selection </h3>
</center>
A very common operation within cytoscape is to <b><i>select</i></b> nodes and
then to do some operation on them.  When a node or edge is selected, its color changes
in a way which causes them to stand out to let the user know that the nodes are
selected.<p>
Some of the things you can do with selected nodes are: zoom in to show only those nodes; popup a Node Browser to examine data
associated with them; hide them; display them in a new network.
Many Cytoscape <a target="body" href="plugins2.php">plugins</a> do calculations
on networks and individual nodes, and on completion, leave certain nodes, edges,
or their attributes in a new state.

<p>

There are two main ways to interactively (that is, with your mouse)
select nodes in the graph:

 <ul>
    <li> Draw a box around the nodes by dragging the mouse with the left button held down
    <li> Simply click directly on a node.  Shift-click to select multiple nodes
    <li> The default behavior is to select only the nodes that are included in the
selection box; you can change this via the <strong>Select</strong> menu to
select only edges, or both nodes and edges. To zoom to the current selection,
click this icon <IMG src="tut/crop36.gif" align="absbottom"> in the toolbar.
 </ul>

Try this: &nbsp;
  <ol>
    <li> Use your mouse to select the single node <i>MIG1</i>.  &nbsp;It will turn a
         slightly different color that it was before.
   <p> <li> On your keyboard, type <b>F6</b>.  Ten more genes will now be selected:&nbsp; these are the "first neighbors" of <i>MIG1</i>.<p> <li> Finally, again on your keyboard, type <b>Ctrl-N</b>
    (Hold down the Ctrl button and press the N key).  This will create
         a new Cytoscape network containing only the eleven genes you selected above.</ol>
The combined operation you just performed - selecting some nodes, then viewing
them in a new window - may often be useful.<p>

Note that <b>F6</b> and <b>Control-N</b> introduced above are keyboard shortcuts for menu
operations:  &nbsp; <i> Select first neighbors </i> can also be accomplished from
the <i> Nodes </i> submenu of the main menubar's <i>Select</i> menu.  <i> Select
to new window </i> is also on the <i>Select</i> menu.
<br>
<center>
<h3> Data Attributes </h3>
</center>
<br>

In Cytoscape, you can associate any kind of data with any node or
edge.  We call these associated data <b><i> attributes</i></b>.  We often
use these <b>data</b> attributes to control the display of <b>visual</b> attributes of
the graph, something we discuss at length below.  All of the nodes in
this particular network have an <b><i>expression</i></b> attribute, a double-precision rational
number which we obtained from microarray experiments.  You can examine the expression
of any node or nodes by

<ul>
   <p><li> Selecting the node or nodes your are interested in.
   <p><li> Clicking with the right mouse button on one of the nodes and
   selecting the Attribute Browser option. (Or selecting the Data->Show Attribute Browser menu option)<p>
<li> You will then see a "Node Browser" dialog box pop up, which has many
           tabs.  You can see the expression values for the genes you selected
           by clicking on the <b> expression tab</b>.  (The <b> customize tab </b>
           in the node browser allows you to construct a view of just those data attributes you
           care about; this is especially useful when there are many different
           data attributes.)<p><li> One of the attributes we load for each gene is a link to the Saccharomyces
           Genome Database (SGD).  By clicking in the node browser cell labeled
           'SGD' your web browser will display the SGD page for that gene.  (Note: a known bug with some browsers, especially some versions of Mozilla, may prevent you from opening new web pages this way).</ul>

<p>

<br>
</p>
<center>
<h3> Annotation </h3>
</center>
<br>

Several large projects, including the <a href="http://www.geneontology.org">Gene
Ontology Consortium</a> (GO) and the <a href="http://www.genome.ac.jp">Kyoto
Encyclopedia of Genes and Genomes</a> (KEGG), provide annotation information
for genes and proteins for many different species using a controlled
vocabulary. Cytoscape is able to integrate this information with the
displayed biomolecular network. Here, we will learn how to explore and assign
GO annotation to the proteins in our network. Follow the steps below:

<ol>
   <p><li> At the far right of the tool bar, press the icon which looks like
              <img src="tut/ontology36.gif" align="absbottom" width="34" height="36">
   <p><li> After a moment, the Annotation dialog comes up, containing two panes.
           In the left pane, click on the icon
             <img src="tut/jtreeWhirlygig.jpg" align="absbottom" width="20" height="20">
           immediately to the left of the label
           <center><p>
           <i><b> GO, Molecular Function, Saccharomyces cerevisiae</b></i> <br>
&nbsp;<p></center>
           This will display the many levels at which GO has annotated these
           genes for molecular function.
   <p><li> You will then see a list of numbers, 1-11, which indicate the
           levels of progressively more specific molecular function which has been
           assigned to yeast genes.
   <p><li> Click on <b>3</b>, and then, at the bottom of this same left pane,
           click on the wide button labeled <b> Apply Annotation to All Nodes </b>
   <p><li> Now you will see, in the right pane, <b>Go Molecular Function (level 3)</b>
   <p><li> Click on
             <img src="tut/jtreeWhirlygig.jpg" align="absbottom" width="20" height="20">
            and examine the approximately 15
           varieties of molecular function present on our 29 genes.
   <p><li> Click on any one or more of these categories, and watch the corresponding
           genes become selected in the network graph.   (Use 'Shift-Click' to select
           more than one category.)
   <p><li> If you now examine the data attributes of any selected genes (by
           right-clicking with your mouse; see
           the 'Attribute' section, above), you will see that this GO annotation,
           at level 3, is now registered as data attributes of the genes.
   <p><li> You may be interested in several kinds of annotation at the
           same time: &nbsp;molecular function at levels 2,3, and 4, for instance,
            and biological
           process at level 6.  By repeating steps 2, 3, and 4 above, you can
           quickly apply all of this annotation to all genes in your network.
           Every annotation you add will appear in the right-hand pane, where
           it can be opened, and node-selection-by-category is easily accomplished.<p><li> All of these annotations can be viewed in the node browser of the
           cytoscape window as well.   Simply select the nodes you are interested in,
           right-click on the background, and then customize the browser to
           show the annotations exactly as you like. (Please note that many
           genes are annotated with multiple values, even at the same logical
           level.)
</ol>


<br>
<center>
<h3> Annotation continued: adding edges and controlling layout</h3>
</center>
<br>

Annotation can be useful when used to control the layout of a
network.  Here is an example.<ol>
   <p><li> Apply  GO Molecular Function (level 2)</b> annotation to the network.
   Then select that title where it appears in the right-hand pane. You will then
   see two buttons at the bottom become enabled (they were
           previously 'grayed-out').   Click on <b>Add Edges</b>.  This adds more
           than 200 new edges to the network, drawn between every gene which
           shares one of the level 2 molecular function categories.
   <li>Click on <b>Layout</b> to lay the network out based on GO functional
   category. Nodes labeled with each GO functional category have been created to
   label each section of the network.</li>
   <li>Press the <b>Delete created nodes/edges</b> button to remove the edges
   and label nodes you just added.</li>
</ol>

<HR>

<center><H3>Tutorial complete!</H3></center>

You now know the basic operations that Cytoscape provides to explore a network
and its associated data.  Other tutorials cover more advanced Cytoscape functions.

    </div>
    <? include "footer.php"; ?>
  </body>
</html>

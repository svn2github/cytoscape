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
   <body>
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
	   <h2> Expression Analysis Basics </h2>
	</center>
	<p>

This tutorial introduces some basic methods for analyzing gene expression
data with Cytoscape.  In this tutorial, you will learn about the following:
<ul>
  <li> Loading expression data into Cytoscape </li>
  <li> Coloring the nodes of a network according to expression data </li>
  <li> Using filters to select nodes according to expression levels </li>
  <li> Using the <a href="http://www.cytoscape.org/plugins2.php">dynamic 
expression plugin</a> to aid in the visual detection of complexes or 
network modules </li>
</ul>
<p>
Begin by clicking here: 
&nbsp;
<font size=+1>
<a href="tut/expression/webstart/expression.jnlp">WEB START</a>
</font>&nbsp; (approximate download size: 22 MB) This starts Cytoscape on your own computer, after downloading the
program and annotations from our website. (On subsequent runs, the program or
annotation will not be downloaded again unless we have new versions or new
annotation for you to use.) If Cytoscape does not start, please look at the
<a href="tutorial.php">instructions</a> for some places to get help.<p> If at any point you wish
to restart this tutorial, just click again on the <b>WEB START</b> link just above.
<p>
You should see a splash screen while your computer is loading Cytoscape and
its data.  Once Cytoscape starts, you should see a figure that looks like
this figure:
<p>
<IMG SRC="tut/expression/figures/initial.window.gif" WIDTH="50%">
<p>
This figure shows a network of 331 nodes.  To 
better visualize the network, do two things.  
<ol>
  <li> Enlarge the panel containing the network by clicking on the 
       maximize button (<IMG SRC="tut/expression/figures/maximize.button.jpg" WIDTH="2%" ALIGN="BOTTOM">) 
       or by grabbing the lower right corner of the panel with your mouse and 
       dragging.  
  <li> Go to the <b>Layout</b> menu, select 
       <b>Apply Spring Embedded Layout</b>, and then select 
       <b>All Nodes</b>.  
</ol>
This should
yield a network that resembles this image:
<p>
<IMG SRC="tut/expression/figures/expr.start.gif" WIDTH="50%">

<br><br> <center> <h3> Biological Context </h3> </center> <br>

The network shown depicts 362 protein-dna and protein-protein interactions 
associated
with Galactose metabolism in yeast.  The expression dataset utilized in this
tutorial describes the gene expression levels observed while perturbing a 
number of Galactose-related genes.  For further information, see 
<a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=11340206&dopt=Abstract">Science 2001 292:929-34</a>.

<br><br> <center> <h3> Loading Expression Data </h3> </center> <br>

Expression data can be loaded into Cytoscape by selecting the <b>File</b> menu
followed by <b>Load</b> and <b>Expression Matrix File</b>, or by typing 
<b>Ctrl-E</b>.  In this case, the expression data was pre-loaded by
Java Web Start, and this step is not necessary.
<p> 
Cytoscape accepts expression data in a number of different formats.  The 
simplest format is as follows: 
<p>
<table border="0" cellspacing="10%" align="center" summary="">
  <tr>
     <td><em>Gene_Name</em></td>
     <td><em>Alias</em></td>
     <td><em>Value_1</em></td>
     <td><em>Value_2</em></td>
     <td><em>...</em></td>
     <td><em>Value_N</em></td>
  </tr>
 </table>
where <em>Gene_Name</em> represents the name of a node as it appears in the 
network, <em>Alias</em> describes an alias or common gene name, and 
<em>Value_1</em> to <em>Value_N</em> describe the expression level of the gene
in N experiments.  All columns are whitespace-delimited, and the first
line contains header information.  Here is an example:
<table border="0" cellspacing="10%" align="center" summary="">
  <tr>
    <td><tt>GENE</tt></td>
    <td><tt>COMMON</tt></td>
    <td><tt>gal1RG</tt></td>
    <td><tt>gal4RG</tt></td>
    <td><tt>gal80RG</tt></td>
   </tr>
   <tr>
    <td><tt>YHR051W</tt></td> 
    <td><tt>COX6</tt></td> 
    <td><tt> -0.034</tt></td> 
    <td><tt> 0.111</tt></td> 
    <td><tt> -0.304</tt></td> 
   </tr>
   <tr>
    <td><tt>YHR124W </tt></td> 
    <td><tt>NDT80 </tt></td> 
    <td><tt>-0.090</tt></td> 
    <td><tt> 0.007 </tt></td> 
    <td><tt>-0.348</tt></td> 
   </tr>
   <tr>
    <td><tt>YKL181W </tt></td> 
    <td><tt>PRS1 </tt></td> 
    <td><tt>-0.167 </tt></td> 
    <td><tt>-0.233</tt></td>  
    <td><tt>0.112 </tt></td>  
   </tr>
  </table>
The <a href="http://www.cytoscape.org/manual/Cytoscape2_1Manual.pdf">Cytoscape
Manual</a> describes extensions to this format for describing additional 
attributes, such as significance levels.

<br><br> <center> <h3> Coloring your network according to expression data</h3> </center> <br>
Depicting a biological network with the nodes colored by expression levels is
a simple yet powerful visualization method, as it places the expression results
within a functional context.  To try this, apply the following steps:
<ol>
  <li> Under the <b>Visualization</b> menu, select <b>Set Visual Properties</b>
This will bring up a menu that looks like this: 
<IMG SRC="tut/expression/figures/viz.styles.menu.gif" width="40%"></li>
  <li> Click on the <b>Duplicate</b> button.</li>
  <li> Select a name for your new visual style, such as "RedGreen", and 
       click on <b>OK</b>.</li>
  <li> Click on the <b>Define</b> button. </li>
  <li> Under the <b>Node Color</b> tab, locate the pull-down menu under the
       <b>Mapping</b> section.  Replace the option <b>None</b> with 
        <b>RedGreen</b>.</li>
  <li> Click on the pull-down menu next to the label <b>Map Attribute:</b>.  
This will bring up a list of the experiments in your expression matrix file.
Scroll down to <b>gal4RG.sigsig</b>.</li>
  <li> Click <b>Apply to Network</b>.  This will color nodes on the network
  as follows:</li>
  <ul>
    <li> White nodes have expression levels of close to zero. If the 
expression data is expressed as a log-odds ratio, this represents little or no 
change relative to the reference experiment. </li>
    <li> <font color='red'> Bright red </font> nodes have expression levels 
of around -2.5, indicating substantial down-regulation compared to the
reference experiment.  </li>
    <li> <font color='pink'> Pink </font> nodes have expression levels ranging
  between -2.5 and 0, with darker colors indicating lower expression ratios 
  and greater degrees of down-regulation.</li>
     <li> <font color='green'> Bright green </font> nodes have expression levels of around 2.1, representing substantial up-regulation relative to the reference experiment. Light green nodes have expression </li>
  levels ranging from 0 to 2.1, with darker colors indicating higher expression
levels, and greater degrees of up-regulation.</li>
  </ul>
  <li> To identify any nodes with no entries in the expression matrix,
       change the default node color to <font color='grey'> grey </font>, 
       by going to the <b> Default </b> section of the menu, clicking on 
       <b>Change Default</b>, and clicking on a grey color swatch.  This is
       a useful trick for identifying any mislabeled nodes.
  </li>

</ol>
  At this point, the screen should look resemble the figure shown below.  
Click on <b>Apply to Network</b> and <b>Close</b>.
  <IMG SRC="tut/expression/figures/expr.nodes.colored.jpg" width="70%">
  <p>

  At this point, Cytoscape shows a network of nodes colored according to 
changes in gene expression observed when the GAL4 transcription factor gene
is perturbed.  In your current network, the node YPL248C corresponds to the 
GAL4 gene.  To study the effects of perturbing GAL4 on the regulatory network,
perform the following steps:
<ol>
  <li> Select the node YPL248C by name by typing the keyboard shortcut 
  <b>Control-F</b>, entering the name YPL248C in the dialog box, and clicking
the <b>Search</b> button, and then closing the window</li>
  <li> Select the neighbors of this node by going to the <b>Select</b>
  menu, continuing to the <b>Nodes</b> sub-menu, and selecting <b>First
neighbors of selected nodes</b>.</li>
  <li> Copy the selected nodes to a new network using the keyboard shortcut
  <b>Control-N</b>.  Re-apply the spring-embedded layout, and zoom as
necessary.
</ol>
  This will generate a picture similar to the one shown below.  The nodes 
  YOL051W and YML051W have protein-protein interactions with YPL248C; all
  other interactions with YPL248C are protein-DNA.  One node is colored grey
  (assuming the default node color was set to grey above), and is labeled
with a question mark to indicate that an interaction had been observed but
one protein in the interaction could not be identified.  Notice that the nodes
  YBR018C, YBR019C, and YBR020W are colored bright red, implying 
substantial down-regulation.  This suggests that GAL4 activates transcription
of these nodes.  Notice that the effect of the pertubation is more striking
on these proteins regulated by GAL4 than on GAL4 itself.  Such a result is not
unusual.
<p>
  <IMG SRC="tut/expression/figures/expr.selected.subnetwork.gif" WIDTH="50%">
<p>
<br><br> <center> <h3> Dynamic expression analysis  </h3> </center> <br>

<p>
The previous sections of this tutorial have described analyzing biological 
networks with expression data from one experiment.  
This section describes
analyzing networks with expression data from multiple experiments.  
Many biological phenomena can be indicated by patterns of expression 
data across multiple experiments.  For instance:
<ul>
<li> Transcriptional regulation patterns can be suggested by related 
changes in expression data; the expression level of a gene may be inversely
related to its transcriptional repressors, and directly related to its
transcriptional activators. </li>
<li> Genes involved related to the same complex or network module may show
related changes in expression.  In prokaryotes, genes involved in the same
biological process are often located on the same operon, and 
are co-regulated.</li>
</ul>
This section utilizes the <a href="http://www.cytoscape.org/plugins2.php">
Dynamic expression plugin</a> by Iliana Avila-Campillo to search for such
patterns visually.
<p>
Under the <b>Plugins</b> menu, click on <b>Dynamic Expression</b>.  You will 
see a dialog box like the one shown here:
<IMG src="tut/expression/figures/dyn.expr.dialog.gif" width="30%">
<p>
Click on the button labeled <b>Play</b>.  This will cycle Cytoscape through
each of the mRNA expression experiments, showing an image similar to the
one below:  
<p>
<IMG src="tut/expression/figures/dyn.expression.1.gif" width="40%">
<p>
For each experiment:
<ul>
<li> Nodes with negative expression levels are colored 
<font color="blue">blue</font>.  Bright blue nodes have expression
levels of -1 or less.</li>
<li> Nodes with expression levels close to zero are colored white.</li>
<li> Nodes with positive expression levels are color 
<font color="red">red</font>.  Nodes with expression levels of 1 or higher
are colored bright red.</li>
<li> If you reset the default node color earlier, then nodes with no entries
in the expression dataset are colored <font color="grey">grey</font>.  This
change makes it easier to separate nodes with unknown expression levels
from those with expression levels that are consistently positive but low.</li>
<li> Anytime you wish to change the color settings, you may do so by going to
the <b>Visualization</b> menu, clicking on <b>Set Visual Properties</b>, and 
then clicking the <b>Define</b> button.</li>
</ul>
The controls on the <b>Dynamic Expression</b> dialog box allow one cycle
through the experiments again, control the speed, and select any one experiment
for a static view.  Press the <b>Play</b> button to restart the cycle, and see
if you can spot any nodes with coordinated changes in expression.
<p>
Type the keyboard shortcut <b>Control-N</b> to select a node by name.  In the
dialog box, enter the name YDR395W.  This is the nuclear transport factor
SXM1.  It is connected to eight nodes in this network via protein-protein
interactions.
Select these eight nodes by going to the <b>Select</b> menu, selecting the
<b>Nodes</b> sub-menu, and clicking on <b>First neighbors of selected node</b>.
<p>
Copy these nodes to a new network by typing <b>Control-N</b>.  Generate a new
layout for this graph by selecting a layout option under the <b>Layout</b>
menu, and zooming in or out as necessary.  This should produce an image
similar to the one shown below:
<p>
<IMG src="tut/expression/figures/dyn.expression.subgraph.gif" width="40%">
<p>
Press <b>Play</b> on the <b>Dynamic Expression</b> dialog box.  Notice how
with the exception of YER056CA, for which there is no expression data, the
expression value of the other nodes changes in a consistent fashion.
This suggests that these genes are co-regulated.  The fact that these genes
are also related through protein interactions suggests that they participate
in a common function, as part of a complex or a network module.  In fact,
these nodes are components of the 60S ribosomal subunit.  Since these 
nodes are components of the same complex, it is not
surprising that their expression patterns are similar.
<br><br> <center> <h3> Tutorial complete!  </h3> </center> <br>
You now have a basic set of skills for analyzing expression data with 
Cytoscape.  This tutorial is focused on mRNA expression data, but the
methods outlined here apply just as well to any other sort of experimental
data, so long as the data can be described using Cytoscape's expression
matrix format.
</body>

</html>


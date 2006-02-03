<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org">
<meta http-equiv="content-type" content=
"text/html; charset=ISO-8859-1">
<title>Cytoscape Online Tutorial</title>
<link rel="stylesheet" type="text/css" media="screen" href=
"../css/cytoscape.css">
<style type="text/css">
  <!--
    @page { size: 8.5in 11in; margin-right: 1.25in; margin-top: 1in; margin-bottom: 1in }
    P { margin-bottom: 0.08in; direction: ltr; color: #000000 }
    P.western { font-family: "Times New Roman", serif; font-size: 12pt; so-language: en-US }
    P.cjk { font-family: "Times New Roman", serif; font-size: 12pt }
    P.ctl { font-family: "Times New Roman", serif; font-size: 12pt }
    A:link { color: #0000ff }
  -->
  
</style>
</head>
<body lang="en-US" text="#000000" dir="LTR">
<table id="feature" border="0" cellpadding="0" cellspacing="0"
summary="">
<tr>
<td width="10"></td>
<td valign="center">
<h1>Cytoscape Online Tutorial</h1>
</td>
</tr>
</table>

<? include "nav.php"; ?>
<? include "nav_tut.php"; ?>
<div id="indent">
<center>
<h2>Basic Expression Analysis</h2>
</center>

<p>If you have completed the <a href="getting.started.php">Getting
Started</a> and <a href="filters.editor.php">Filters and Editor</a>
tutorials, this tutorial will show you some expression analysis
basics available wth Cytoscape. This tutorial will introduce you
to:</p>

<ul>
<li>Input formats for expression data</li>

<li>Coloring nodes by expression data values</li>

<li>Assessing expression data in the context of a biological
network</li>
</ul>

This tutorial features the following data files: 

<ul>
<li><a href="basic.expression/galFiltered.sif">galFiltered.sif</a>,
also distributed in the Cytoscape testData directory. This network
contains protein-protein and protein-DNA interactions associated
with Galactose metabolism in yeast.</li>

<li><a href=
"basic.expression/galExpData.pvals">galExpData.pvals</a>, also
distributed in the Cytoscape testData directory. This file contains
gene expression measurements for three pertubation experiments. In
each experiment, the level of one key protein was perturbed
artificially.</li>

<li><a href="basic.expression/galExpData.mrna">galExpData.mrna</a>,
<b>not</b> distributed in the Cytoscape testData directory. This
file contains a subset of the data from
<b>galExpData.pvals</b>.</li>
</ul>

For further information on these datasets, see <a href=
"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=11340206&amp;dopt=Abstract">
Science 2001 292:929-34</a>. Please download these data files to
your local disk before starting. 

<p>Begin by clicking here: <a href=
"webstart/cyto2.2.060106.jnlp">WEB START</a> (approximate download
size: 22 MB) This starts Cytoscape on your own computer, after
downloading the program and annotations from our website. This
tutorial and accompanying lectures were delivered at <a href=
"http://www.csc.fi/math_topics/">Computer Science Center</a> in
Helsinki, Finland. The lecture slides of background material are
available <a href="basic.expression.ppt">here</a> and an
accompanying video presentation will be available soon.</p>

<p></p>

<h3>Loading expression data</h3>

<p>Here, we will explore the basic structures for applying
expression data to a Cytoscape network. In this section, you will
learn about the expression data formats expected by Cytoscape,
formatting the nodes in your network according to expression
values.</p>

<ol>
<li>
<p>Start Cytoscape and load the network <b>galFiltered.sif</b>.
After detaching CytoPanels 1 and 3, maximizing the canvas, and
applying the spring-embedded layout , you should see a nework
similar to the one below.</p>

<img src="basic.expression/Fig1.jpg" width="473" height="414"><br>
<br>
</li>

<li>
<p>Using your favorite text editor, open the file
<b>galExpData.mrna</b>. The first few lines of the file are as
follows:</p>

<img src="basic.expression/Fig2.jpg" width="45%"><br>
<br>
</li>

<li>
<p>Here is the structure of this file.</p>

<ol type="i">
<li>
<p>The first line consists of labels.</p>
</li>

<li>
<p>All columns are separated by a single whitespace character, such
as a space or a tab.</p>
</li>

<li>
<p>The first column contains node names, and must match the names
of the nodes in your network <b>exactly!</b>.</p>
</li>

<li>
<p>The second column contains common locus names. This column is
optional, and the data is not currently used by Cytoscape, but
including this column makes the format consistent with the output
of many microarray analysis packages, and makes the file easier
to read.</p>
</li>

<li>
<p>The remaining columns contain experimental data, one column per
experiment, and one line per node. In this case, there are three
expression results per node.</p>
</li>
</ol>
</li>

<li>
<p>Under the <b>File</b> menu, select <b>Load</b>, and
<b>Expression Matrix File</b>, and load this file. After a brief
load, a status window will appear, indicating how many experimental
conditions were found (three) and what type significance values
were included (none).</p>
</li>

<li>
<p>Now we will use the <b>Node Attribute Browser</b> to browse
through the expression data, as follows.</p>

<ol type="i">
<li>
<p>Select some node in the Cytoscape canvas</p>
</li>

<li>
<p>In the Node Attribute Browser, click the <b>Select
Attributes</b> button, and select the attributes <b>gal1RGexp</b>,
<b>gal4RGexp</b>, and <b>gal80Rexp</b>.</p>
</li>

<li>
<p>Under the <b>Node Attribute Browser</b>, you should see your
node listed with their expression values, as shown.</p>

<img src="basic.expression/Fig3.jpg" width="75%"><br>
<br>
</li>
</ol>
</li>
</ol>

<h3>Coloring nodes</h3>

<p>Probably the most common use of expression data in Cytoscape is
to set the visual attributes of the nodes in a network according to
expression data. This creates a powerful visualization, portraying
functional relation and experimental response at the same time.
Here, we will walk through the steps for doing this.</p>

<ol>
<li>
<p>Go to the <b>Set Visual Style</b> menu under
<b>Visualization</b>.</p>
</li>

<li>
<p>Create a new visual style named <b>Gal80</b> by clicking on the 
<B>Duplicate</B> button to duplicate the default style.  Click on the
<B>Define</B> button to define your style. 
</li>

<li>
<p>Define the node color of this visual style as follows:</p>

<ol type="i">
<li>
<p>Under <b>Mapping</b>, click on the pull-down menu labeled
<b>None</b> and select <b>RedGreen.</b></p>
</li>

<li>
<p>In the pull-down menu labeled <b>MapAttribute</b>, select the
attribute <b>gal80RGexp</b>. This specifies that each node will be
colored on a color continuum according to <b>Gal80</b> expression,
as follows:</p>

<ol>
<li>
<p>Large negative values (indicating high repression) are colored
red</p>
</li>

<li>
<p>Small negative values (indicating slight repression) are colored
pink</p>
</li>

<li>
<p>Values close to zero are colored white</p>
</li>

<li>
<p>Small positive values (indicating slight induction) are colored
light green</p>
</li>

<li>
<p>Large positive values (indicating high induction) are colored
bright green.</p>
</li>

<li>
<p>Extreme values (negative values less than -2.5 and positive
values greater than 2.1) are colored blue and black
respectively.</p>
</li>
</ol>
</li>

<li>
<p>Note that the default node color of pink falls within this
spectrum. A useful trick is to choose a color outside this
spectrum, to distinguish nodes with no expression value defined
from those with slight repression. Under <b>Default</b>, click on
<b>Change Default</b>, and select a default color of grey.</p>
</li>

<li>
<p>The <b>Set Visual Properties</b> menu should appear as
follows:</p>

<img src="basic.expression/Fig5.jpg" width="336" height="462"><br>
<br>
</li>

<li>
<p>Click on <b>Apply to Network</b>. You should see most nodes
colored pink, green, or white, with a few grey nodes and a few
black nodes.</p>
</li>
</ol>
</li>
</ol>

<h3>Using P Values</h3>

<p>Expression values, such as the ones abov, generally rpresent
fold changes, log ratios comparing the expression level in the
experiment to some baseline condition But some genes have greater
variation in their expression levels normally, so many microarray
data analysis packages also report significance measures, such as P
values,to indicate if the observed fold change exceeds the normal
variability for the gene. Here, we shall explore the P value
support in Cytoscape, and one way of using expression values and P
values together in setting visual properties.</p>

<ol>
<li>Using your favorite text editor, open the file
<b>galExpData.pvals</b>. You will see the following format: 

<p><img src="basic.expression/Fig4.jpg" width="50%"></p>
</li>

<li>Notice how thereare two columns labeled <b>gal1RG</b>, two
labeled <b>gal4RG</b>, and two labeled <b>gal80R</b>. In each case,
the first of the two columns contains the expression value for the
experiment, while the second contains the P value.</li>

<li>Load the expression matrix file <b>galExpData.pvals</b>.</li>

<li>In the <b>Node Attribute Browser</b>, look at the list of
available attributes. Notice how there are pairs of attribute
names, such as <b>gal80Rexp</b> and <b>gal80Rsig</b>. The first is
the expression value, and the second is the P value.</li>

<li>Select some nodes, and look at their expression values and P
values under the <b>Node Attribute Browser</b>. Notice how while
the expression data value ranges from about -3 to +3 in these
cases, the P value ranges from 0 to 1 - as it should</li>

<li>Now, we will explore setting node shapes according to P values.


<ol>
<li>Go to <b>Set Visual Style</b> under <b>Visualization</b>.</li>

<li>Go to the <b>Node Shape</b> tab.</li>

<li>In the pull-down menu under <b>Mapping</b>, select
<b>BasicContinuous</b></li>

<li>In the <b>Map Attribute</b> pull-down menu, select
<b>gal80Rsig</b>.</li>

<li>By default, you will see a button labeled <b>Below</b>, a
button labeled <b>Above</b>, and three <em>points</em>, three input
fields each with a <b>Del</b> button at the left and a <b>Equal</b>
button at the right.</li>

<li>Click on the bottommost two <b>Del</b> buttons to delete the two
bottom points.
<EM>Make sure to follow these instructions carefully!</EM></li>

<li>Click on the <b>Below</b> button. This should bring up the
<b>Select Appearance</b> menu, shown below: 

<p><img src="basic.expression/Fig8.jpg" width="20%"></p>
</li>

<li>Select a square.</li>

<li>In the line below <b>Below</b>, set the number in the input
field to 0.05, click on the <b>Equals</b> button, and select a
circle.</li>

<li>In the following line, labeled <B>Above</B>, select a circle.</li>

<li>The <b>Set Visual Style</b> window should appear as follows: 

<p><img src="basic.expression/Fig9.jpg" width="50%"></p>

<p>This will have the effect of depicting nodes with P values of
less than 0.05 as squares, and all other nodes as circles.</p>
</li>

<li>Click on <b>Apply to Network</b> On your Cytoscape canvas, your
node shapes should change, as shown below: 

<p><img src="basic.expression/Fig10.jpg" width="50%"></p>
</li>
</ol>
</li>
</ol>

<h3>A Biological Analysis Scenario</h3>

This section presents one scenario on how expression data can be
combined with network data to tell a biological story.<br>
<br>
 First, here is some background on your data. You are working with
yeast, and the genes <b>Gal1</b>, <b>Gal4</b>, and <b>Gal80</b> are
all yeast transcription factors. Your expression experiments all
involve some pertubation of these transcription factor genes.
<b>Gal1</b>, <b>Gal4</b>, and <b>Gal80</b> are also represented in
your interaction network, where they are labeled according to yeast
locus tags: <b>Gal1</b> corresponds to <b>YBR020W</b>, <b>Gal4</b>
to <b>YPL248C</b>, and <b>Gal80</b> to <b>YML051W</b>.<br>
<br>
 

<ol>
<li>
<p>Your network contains a combination of protein-protein (pp) and
protein-DNA (pd) interactions. Here, we shall filter out the
protein-protein interactions to focus on the protein-DNA
interactions.</p>

<ol type="i">
<li>
<p>Create a String Pattern filter to select edges with text
attributes of <b>interaction</b> that match the pattern <b>pp</b>.
For more information, see the <a href=
"filters.editor.php">tutorial</a> on filters and editing.</p>
</li>

<li>
<p>Click on <b>Apply selected filter</b>. This should select 251 of
the 362 edges.</p>
</li>

<li>
<p>Under the <b>Edit</b> menu, select <b>Delete Selected
Nodes/Edges</b></p>
</li>

<li>
<p>Apply a graph layout algorithm to see the edges that remain.
Using the <b>yFiles Organic</b> layout, your network should now
appear as follows:</p>

<p><img src="basic.expression/Fig6.jpg" width="60%"></p>
</li>
</ol>
</li>

<li>
<p>Notice that all three black (highly induced) nodes are in the
same region of the graph. Zoom into the graph to see more
details.</p>
</li>

<li>
<p>Notice that there are two nodes that interact with all three
black nodes: <b>YPL248C</b> and <b>YOL051W</b>. Select these two
nodes and their immediate neighbors, and copy them to a new
network. This makes it easier to focus on the interactions
involving these nodes. With some layout and zooming, this new
network should appear similar to the one shown:</p>

<p><img src="basic.expression/Fig7.jpg" width="60%" border="1"></p>
</li>

<li>
<p>With a little exploration in the node attribute browser, you
should see the following:</p>

<ol type="i">
<li>
<p>The two nodes that interact with all three black nodes are
<b>YOL051W</b> (<b>Gal11</b>, a general transcription cofactor with
many interactions) and <b>YPL248C</b> (<b>Gal4</b>).</p>
</li>

<li>
<p>Both nodes show fairly small changes in expression, and neither
change is statistically-significant: are rendered as light-colored
circles. the 0.01 threshold. These slight changes in expression
suggest that the critical change affecting the black nodes might be
somewhere else in the network. either of these nodes.</p>
</li>

<li>
<p><b>YPL248C</b> interacts with <b>YML051W</b> (<b>Gal80</b>),
which is shows a significant level of repression: it is depicted as
a reddish square.</p>
</li>

<li>Note that while <b>YML051W</b> shows evidence of significant
repression, most nodes interacting with <b>YPL248C</b> show
significant levels of induction: they are rendered as green or
black squares.</li>
</ol>
</li>

<li>
<p>Go to the NCBI website (<A HREF="http://www.ncbi.nlm.nih.gov/">
http://www.ncbi.nlm.nih.gov/</A>),
and search the <b>Gene</b> database for <b>YPL248C</b>. The items
returned should include <b>Gal4</b>. Click on the link for
<b>Gal4</b> to get more information.</p>
</li>

<li>
<p>Reading the description of <b>Gal4</b>, you will see that it is
a transcription factor that is repressed by <b>Gal80</b>.</p>
</li>

<li>
<p>Putting all of this together, we see that the transcriptional
activation activity of <b>Gal4</b> is repressed by <b>Gal80</b>.
So, repression of <b>Gal80</b> increases the transcriptional
activation activity of <b>Gal4</b>. Even though the expression of
<b>Gal4</b> itself did not change much, the <b>Gal4</b> transcripts
were much more likely to be active transcription factors when
<b>Gal80</b> was repressed. This explains why there is so much
up-regulation in the vicinity of <b>Gal4</b>.</p>
</li>
</ol>

<p style="margin-left: 0.75in; margin-bottom: 0in"><br>
</p>

<p><b>Good work!</b> Network analysis and expression data are a
powerful combination, and now you have the skills to do some
substantial analysis. Go reward yourself with a good cup of
coffee.</p>

<? include "tut.footer.php"; ?>
<? include "../footer.php"; ?>
</div>
</body>
</html>


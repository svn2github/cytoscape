<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org">
<meta http-equiv="content-type" content=
"text/html; charset=ISO-8859-1">
<title>Cytoscape Online Tutorial</title>
<link rel="stylesheet" type="text/css" media="screen" href=
"../css/cytoscape.css">
<meta http-equiv="CONTENT-TYPE" content="text/html; charset=utf-8">
<meta name="AUTHOR" content="Melissa Cline">
<style type="text/css">
  <!--
    @page { size: 8.27in 11.69in; margin-right: 1.25in; margin-top: 1in; margin-bottom: 1in }
    P { margin-bottom: 0.08in; direction: ltr; color: #000000; widows: 0; orphans: 0 }
    P.western { font-family: "Nimbus Roman No9 L", "Times New Roman", serif; font-size: 12pt; so-language: en-US }
    P.cjk { font-family: "Times New Roman", serif; font-size: 12pt }
    P.ctl { font-family: "Times New Roman", serif; font-size: 10pt }
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
<h2>Gene Ontology Analysis with Cytoscape</h2>
</center>

<p>Gene Ontology (GO) is now an essential resource in
bioinformatics. It defines a controlled vocabulary of terms in
biological process, molecular function, and cellular location, and
relates the terms in a somewhat-organized fashion. Expert curators
assign genes to GO categories, and the majority of genes in
organisms including human and yeast now have GO annotations. This
section of the tutorial outlines the resources available to you
under Cytoscape for examining a network (or sub-network), and
asking "but what do these genes DO?</p>

<p>If you have completed the basic Cytoscape tutorials, in this
tutorial you will</p>

<ul>
<li>
Learn how to navigate the Cytoscape Gene Ontology wizard to
apply GO annotations to Cytoscape nodes
</li>

<li>
Learn how to look for enriched GO categories using the BiNGO
plugin.
</li>
</ul>
<P>
This tutorial and accompanying lectures were delivered at 
<A HREF="http://www.csc.fi/math_topics/">Computer Science Center</A> in 
Helsinki, Finland.  The lecture slides of background material are
available <A HREF="GO.ppt">here</A> and an accompanying video
presentation will be available soon.

<p>This tutorial features the following plugins:</p>

<ul>
<li>
The BiNGO plugin, developed by the <A HREF="http://www.psb.ugent.be/cbd/">Computational Biology 
Division</A>, Dept. of Plant Systems Biology, Flanders Interuniversitary Institute for
Biotechnology (VIB), described in a publication in 
<A HREF="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=15972284&query_hl=1&itool=pubmed_docsum">
Bioinformatics</A> in 2005.
</li>
</ul>

<p>and the following data files</p>

<ul>
<li>
<A HREF="GO/galFiltered.symbol.sif">galFiltered.symbol.sif</A>: a version of the file
galFiltered.sif used in the introductory tutorials. In this
version, the yeast locus tags are replaced with the corresponding
gene symbols. Please download this file before you start.
</li>
</ul>

<p>If you have not already done so, download and install Cytoscape
on your local computer, following the instructions given in the
Cytoscape manual.</p>

<p>Download and install the BiNGO plugin, as follows:</p>
<OL>
<li>
Go to the BiNGO page at <a href=
"http://www.psb.ugent.be/cbd/papers/BiNGO/">http://www.psb.ugent.be/cbd/papers/BiNGO/</a>. 
This site also provides some excellent documentation on BiNGO.
</li>

<li>
Click on the Download link at the left side of the page.
</li>

<li>
Read the BiNGO license agreement. Accept the terms of the
license by clicking the indicated button, which will start the
download of a file called BiNGO.zip
</li>

<li>
Copy that file into your Cytoscape plugins directory and unzip
it.
</li>

<li>
If you are currently running Cytoscape, exit and restart.
</li>
</ol>

<p>First, we shall learn the steps to load Gene Ontology data into
Cytoscape.</p>

<ol>
<li>
Start Cytoscape and load the network
<b>galFiltered.symbol.sif</b>. As you may recall from previous
tutorials, this file contains a network of yeast (<i>S.
cervisiae)</i> proteins from the galactose pathway.
</LI>
<li>
Under the <b>File</b> menu in your Cytoscape Desktop, select
<b>Load</b> and <b>Gene Ontology Server</b>.</p>
</li>

<li>
Next, a window for the <b>Gene Ontology Wizard</b> should
appear, and you will be asked which format you want to load:
<b>Cytoscape BioDataServer</b> or <b>Gene Ontology</b>. The
<b>Cytoscape BioDataServer</b> functionality is the older one,
maintained for backwards compatibility.</p>
</li>

<li>
The <b>Gene Ontology Wizard</b> will bring up an input form, as
shown below:
</li>


<P><img src="GO/Fig2.jpg" width="50%">

<li>
Fill in the form as follows:
<ol>
<li>
Click the <b>Select Obo File</b> button, and select the file
gene_ontology.obo from your sample data directory.
</li>
<li>
Click the <b>Add Gene Association</b> file button, and select
the file gene_association.sgd from your sample data directory.
</li>
<li>
Ensure that your default species name is Saccharomyces
cerevisiae. If it is not, click the checkbox next to <b>Overwrite
default species name with</b>, and scroll down to Saccharomyces
Cerevisiae on the pull-down menu.
</li>
<li>
<i><b>Important note:</B></I> each gene association file is
species-specific. For whatever network data you will be working
with, you must (1) set your default species as appropriate to the
network data, and (2) load the gene association file for that species.
</li>
<li>
Click <b>Finish</b>.
</li>
<li>
Note that if you're ever repeating these steps in the future
with a different species, you will need a Gene Association file for
that species. You can download one from The Gene Ontology Project
at the hyperlink marked <b>Current Annotations</b>. If you have any
doubt on the formal species name, you can check it by using the
<b>Gene Ontology Wizard</b> hyperlink <b>Taxonomy name/id Status
Report Page</b>.
</li>
</ol>
</li>

<li>
The <b>Gene Ontology Wizard</b> should go away. The <b>Loading
Gene Ontology Database</b> window should appear, as shown below:
</li>
<P><img src="GO/Fig3.jpg" width="40%">

<li>
Close this window to continue.
</li>

<li>
<b>Usage notes:</b> this
process loads the Gene Ontology data for a single species into
Cytoscape. If you are working with networks from two different
species, you will need to run two different instances of Cytoscape
to apply Gene Ontology data to your networks.
</li>
Now, we will apply Gene Ontology annotations to the nodes in the
network, and browse through them.
<li>
On the Cytoscape Desktop, click on the
<b>Annotation</b> button, shown below: 
<P><img src="GO/Fig4.jpg" width="5%"></p>
</li>

<li>
This will bring up a window labeled <b>Annotation</b>, shown
below: 
<P><img src="GO/Fig5.jpg" width="90%"></p>
</li>

<li>
Click on the <B>+</B> sign next to Biological Process. This should
bring up one link per GO level, 1 through 13. These levels
correspond to the depth of the tree to work at: higher levels 
represent classifications which are more general.
</li>

<li>
Click on 3, and click the button labeled <b>Apply Annotation to
All Nodes</b>.
</li>

<li>
On the right-hand panel, an entry should appear labeled <b>GO
Biological Process (level 3)<i>.</i></b> with a <b>+</b> sign at
the left. Click on the <b>+</b> sign to expand this list.
</li>

<li>
<p>The right-hand panel should now list the Level 3 terms, as shown
below:</p>
</li>

<p><img src="GO/Fig6.jpg"width="50%""><br clear="LEFT">
</p>

<li>
On the right-hand panel, click on the term <b>Biopolymer
metabolism</b>. Notice how
this action selects several nodes on the Cytoscape canvas. These
nodes are genes assigned to the <b>Biopolymer metabolism</b> process.
</li>

<li>
In the <b>Node Attribute Browser</b>, click the <b>Select
Attributes</b> button. Notice how <b>GO Biological Process (level
3)</b> appears as one of the options. Select this attribute.
</li>

<li>
Click on the background of your Cytoscape canvas to un-select
the selected nodes. Select some additional nodes with your
mouse.
</li>

<li>
The <b>Node Attribute</b> browser should now show the Level 3
terms for the selected nodes, as shown below: 
<P><img src="GO/Fig8.jpg" width="50%"></p>
</li>

<li>
<p>Bear in mind that a node may be assigned to several GO terms,
given the complex nature of inheritance under GO.</p>
</li>

<li>
<p>Return to the <b>Annotation</b> window, select some other Level
3 GO Biological Process, and notice how nodes selected on the
Cytoscape canvas and listed in the <b>Node Attribute Browser</b>
change accordingly</p>
</li>

<li>
<p>See if you can find any sections of the network with a
concentration of some annotation term.</p>
</li>

<li>
<b>Usage notes:</b> For this
process to work, the nodes in your Cytoscape network must match the
gene names <i>exactly</i>. If you
plan on using this functionality, we recommend that you use
HUGO-approved gene symbols as node names, with the entire name
being in uppercase.
</li>
</OL>

<p>Did you find an area that seemed enriched for some GO term? Here
we will use the <b>BiNGO</b> plugin to see if that enrichment is
statistically-significant.</p>

<OL>
<li>
Under the <b>Select</b> menu,
select <b>Node</b> and<b>Select by name</b> to bring up the <b>Select Nodes by
Name</b> window. Enter <b>Gal4</b> to select the Gal4 tanscription
factor.
</li>

<li>
Select the immediate neighbors of this node, and their immediate neighors.
</li>

<li>
Select these nodes and all edges into a new network, creating a
child network. This will make subsequent steps in this tutorial
easier.
</li>

<li>
Select all the nodes in this child network.
</li>

<li>
In the <b>Plugins</b> menu, select <b>BiNGO</b>. This should
bring up a window called <b>BiNGO Settings.</b>
</li>

<li>
Fill in your <b>BiNGO Settings</b> as follows:

<ol>
<li>
Give your cluster a short name such as "test".
</li>

<li>
Leave the <b>Get Cluster from Network</b> box checked.
</li>

<li>
Under <b>Select a statistical test</b>, select
<b>Hypergeometric</b>. Binomial testing is used when the amount of
data is very large, but hypergeometric testing is appropriate for
most Cytoscape usage scenarios.
</li>

<li>
Under <b>Select a multiple testing correction</b>, choose
Benjamini &amp; Hochberg False Discovery Rate (FDR). This is less
conservative than Bonferroni testing, but still sufficient for most
cases.
</li>

<li>
Under <b>Choose a significance level</b>, enter 0.05. This
threshold controls which GO classes are detailed in the output.
This is not a conservative threshold, but later, one can choose GO
classes with lower P values interactively.
</li>

<li>
Under <b>Select the categories to be visualized</b>, select
<b>Overrepresented after correction</b>. With very few exceptions,
this is the setting you will want.
</li>

<li>
Under <b>Select reference set</b>, select <b>Test cluster versus
complete annotation</b>. This will compare your set of nodes to all
genes in the yeast genome.
</li>

<li>
Under <b>Select ontology</b>, select <B>GO Biological
Process</B>.
</li>

<li>
Under <b>Select organism/annotation and gene identifier</b>,
scroll down to <b>Saccharomyces cerevisiae</b>, and check the box
next to <b>Gene Symbol</b>.
</li>

<li>
Click <b>Start BiNGO</b>.
</li>
</ol>
</li>

<li>
<p>After a brief pause, a network will appear on your canvas such
as the one shown below</p>
</li>

<img src="GO/Fig9.jpg" width="50%">
</p>

<li>
<p>Within this network</p>
<UL>
<li>
Each node represents GO some term, and is labeled accordingly.
If you zoom into the network, you can see the labels.
</li>

<li>
The topology depicts the hierarchy of GO biological
processes.
</li>

<li>
The yellow and orange nodes represent terms with significant
enrichment, with darker orange representing a higher degree of
significance, as shown by the legend on your screen:
</li>
<P><img src="GO/Fig13.jpg" width="30%"></P>

<li>
White nodes are terms with no significant enrichment, but are
included because they have a significant child term. Branches of GO
with no significant terms are not shown.
</li>

<li>
The size of each node in a <b>BiNGO</b> graph is proportional to
the number of nodes in your query set with that term.
</li>
</ul>
</li>

<li>
Go to the <b>Node Attribute</b> browser, and look at the
available attributes. You should see several more, including:

<ul>
<li>
<b>description_test</b>: the name of the GO biological
process
</li>

<li>
<b>adjustedPValue_test</b>: the p-value for the node, adjusted
for multiple hypothesis testing (note that the un-adjusted p-value
is also there, with the name <b>pValue_test</b>, but this P value is less useful for most
applications).
</li>

<li>
<b>n_test</b>: the number of genes in the yeast genome with this
GO term.
</li>

<li>
<b>x_test</b>: the number of nodes that you have selected which
have this GO term.
</li>

<li>
<b>N_test:</b> the total
number of genes in the yeast genome with GO annotations.
</li>

<li>
<b>X_test:</b> the total
number of genes that you have selected. These last four quantities
are used in the calculation of the adjusted P value.
</li>
</ul>
</li>

<LI>Select these
attributes. Now, select some nodes in your <b>BiNGO</b> graph, and look at their attributes
under the <b>Node Attribute Browser</b>

<li>
Select some of these terms, and browse through the nodes in your
<b>BiNGO</b> graph.
</li>

<li>
Here is a good case for Cytoscape's hiding controls. When we
zoom into this network, we see the following:

<p><img src="GO/Fig14.jpg" width="50%"></P>
Notice how the region on the right contains two nodes of marginal
significance, plus several nodes of no significance included
because they are parents of these nodes.</p>
</li>

<li>
Select these nodes.
</li>

<li>
Click on the <b>Hide Selected Region</b> button (shown)
</li>

<P><img src="GO/Fig10.jpg" width="5%"></p>

<li>
These nodes will disappear from the canvas, as shown:
</li>

<P><img src="GO/Fig11.jpg" width="50%"></p>

<li>
Whenever you want, you can make these nodes visible again with
the <b>Show All Nodes and Edges</b> button (below). Experiment with
this.
</li>

<p><img src="GO/Fig12.jpg" width="5%"></p>

<li>
<b>BiNGO</b> will optionally produce an output file listing the
p-values of all nodes with significant enrichment, as follows:
</li>

<ol type="i">
<li>
Return to the child network you created previously, and make
sure that all nodes are still selected.

<li>
Return to your <b>BiNGO</b> <b>Settings</b> window.
</li>

<li>
Specify a new cluster name: <b>test2</b>.
</li>

<li>
Near the bottom of the window, click on <b>Check box for saving
data</b>.
</li>

<li>
Click on the button labeled <b>Save BiNGO Data file in:</b>, and
select a directory for BiNGO's output file
</li>

<li>
Rerun <b>BiNGO</b>.
</li>

<li>
In the specified directory, you should now have a file called
<b>test.2.bgo</b>. Your screen should show a new window titled
<b>test.2 BiNGO Results</b>. Both of these should summarize your
<b>BiNGO</b> parameters, and report on the enrichment of all terms
meeting your p-value threshold.
</li>
</ol>
</li>

<li>
Recall that this <b>BiNGO</b> graph reports on the enrichment of a
subnetwork centered on the <b>Gal4</b> transcription factor. But recall also that
this entire network consists of nodes involved in one single
pathway: galactose utilization. So when we look at the
enriched GO terms in your <b>BiNGO</b> graph, which terms relate to galactose
utilization in general, and which relate to the <b>Gal4</b>
subnetwork specifically? Here, we
shall see how to answer that question.
</li>
<OL type="i">
<li>
Return to your
parent network. Verify that the sub-network centered on <b>Gal4</b>
is still selected. If it is not,
repeat the steps to select <b>Gal4</b>, its immediate neighbors, and their
immediate neighbors.
</li>

<li>
return to your <b>BiNGO</b> settings window, and choosing
<b>Test cluster versus network</b> under <b>Select reference
set</b>. Specify a new name in
the</span> <b>Cluster name:</b> box, "test3".

<li>
Rerun <b>BiNGO</b>.
</li>

<li>
Compare the new <b>BiNGO</b> network against the old one. You
should see fewer significant GO terms in the new <b>BiNGO</b> network.
Which terms are lost?
These are probably associated with galactose utilization in
general.
</li>

<li>
Go to the <b>Node Attribute</b> browser, and click on <b>Select
Attributes</b>. Note that the available attributes include
<b>adjustedPValue_test</b> (reporting enrichment against the
completed genome) and <b>adjustedPValue_test3</b> (reporting
enrichment against the full network). Select these two attributes
for a side-by-side comparison of the p-values of some nodes in the
<b>BiNGO</b> graphs.
</li>
</ol>
</li>
</ol>
<B>One final usage note:</B> Users performing GO analysis with Cytoscape
should be aware that the Gene Ontology wizard in Cytoscape and the BiNGO 
plugin get their GO data from two different places, which are not synchronized.
Both the GO wizard and BiNGO use internal data files derived from 
<A href="http://www.geneontology.org">http://www.geneontology.org</A>, but 
these data files are derived at different times.  Consequently, there can be 
slight differences between the two representations of GO.  In practice, the
two representations will mostly be consistent with each other.  But this has
one benefit to the user: BiNGO offers users flexibility in defining their own
ontology and annotation files.  See 
<A HREF="http://www.psb.ugent.be/cbd/papers/BiNGO/annotations.htm">
http://www.psb.ugent.be/cbd/papers/BiNGO/annotations.htm</A> for more information.
<P>
With that disclaimer given, let's say that BiNGO shows enrichment in 
some GO category that you find especially interesting, and you want to
see the nodes in your network with that GO category.  The following steps
outline how you can do that, bearing in mind that there may sometimes be
inconsistency between any two representations of GO.  We will illustrate the
process with  node 5975, "carbohydrate metabolism".

<OL>
<li>
Go to the parent network on the Cytoscape canvas, and un-select
any nodes currently selected.

<li>
Go to one of your BiNGO graphs and carefully count the shortest
path to the term from the node 'biological process'. That indicates
the level of the term. For instance, 'carbohydrate metabolism''
is a distance of four from 'biological process: there are
three nodes in between them.
</li>

<li>
If you still have the <b>Annotation</b> window open, return to it. Otherwise, re-open
it by clicking on the <b>Add Annotation Ontology to
Nodes</b> button.
</li>

<p><img src="GO/Fig15.jpg" WIDTH="50%"></p>

<li>
Under the <b>Annotation</b> window, and select the <b>Biological
Process</b> annotation for that level: Level 4 for the case of
"carbohydrate metabolism". Click on the button labeled <b>Apply
Annotation to All Nodes</b>
</li>

<li>
<p>On the right side of the window, expand the list of GO terms for
Level 4. Scroll down the list to "carbohydrate metabolism" and
click on it.</p>
</li>

<li>
<p>Look at your parent network on the Cytoscape canvas. Your
network should appear as shown below:</p>
</li>
<p><img src="GO/Fig15.jpg" width="50%">

<li>
Notice how many of the Carbohydrate Metabolism nodes come from two
portions of the network, and that these two sub-networks consist
mostly of Carbohydrate Metabolism nodes. This suggests that these
are the portions of the graph specific to carbohydrate metabolism
activity.
</li>
</ol>
</OL>

<p><b>Congratulations!</b> Now, you know almost as much about Gene
Ontology as your instructor! Go off and do great things!</p>

<? include "tut.footer.php"; ?>
<? include "../footer.php"; ?>
</p>
</div>
</body>
</html>


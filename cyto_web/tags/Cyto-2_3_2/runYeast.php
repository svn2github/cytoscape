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
<h2>Cytoscape Budding Yeast Demo</h2>
</center>

Run Cytoscape with full 
<a href="http://www.geneontology.org" target="_top">Gene Ontology</a>
<!--a href="http://www.genome.ad.jp/kegg/" target="_top">KEGG</a --> annotation, a thesaurus
for translating between gene names and ORF names, and SGD links
for all genes.    See below for how to load your own data.

<p>
Begin by clicking here: &nbsp;
<font size=+1>
<a href="tut/runYeast/runYeast.jnlp">
WEB START</a> </font>&nbsp; (download size: 7 MB)

This starts Cytoscape on your own computer, after downloading the program and annotation
from our website.  (On subsequent runs, 
the program or annotation will not be downloaded again unless we have new versions or new 
annotation for you to use.) If Cytoscape does not start, please look at the
<a href="tutorial.php">instructions</a> for some places to get help.<center>
<h3> Loading Your Network</h3>
</center>

<p align="left"> Once Cytoscape starts, you may load your own network for 
visualization and analysis.</p>

<center>
<h4> File Format </h4>
</center>

<h4>By example: </h4>
<pre>
YNL216W pd YLR044C
YBR019C pd YOL051W
YBR019C pd YGL035C
YER179W
YOL051W pd YBR020W
YOL051W pd YLR081W
YOL051W pp YPL248C
YOL051W pd YBR018C
YGL035C pd YLR044C
...
</pre>

<h4>Comments</h4>
<ol>
   <li> The interaction file consists of one or more lines.
  <p> <li> Each line normally has the format: <pre>        nodeA  interactionType  nodeB </pre>
  <p> <li> A tab is used to separate the three fields in each line.<p><li> Some genes or proteins in your data set may not have any known interactions.  In that case,
       simply put the gene name on a line all by itself: <pre>        nodeC </pre>
   <p><li> You can use any names you wish for the <b><i>'interactionType'</i></b>, but
        some <a href="#int">conventional names</a> have evolved.
</ol>

<center>
<h4> Load the File </h4>
</center>
From the <b>File</b> menu, choose <b>Load</b> then <b>Graph...</b>.  &nbsp;  An "open file"
dialog appears, allowing you to navigate to and select the file you wish to load.  Once
you click <b><i>Open</i></b>, Cytoscape will load your network, lay it out using the
current default layout strategy, and render the network using the
current visual style. You will likely want to lay out the network using one of 
the layouts in the layout menu.<center>
<h2> <a name="int"></a>Some Conventions for Interaction Names </h2>
</center>
<ul>
   <li> <b><i> pp </i></b>: &nbsp; protein-protein interaction
   <li> <b><i> pd </i></b>: &nbsp; protein-DNA interaction
   <li> <b><i> pm </i></b>: &nbsp; protein-metabolite interaction
   <li> <b><i> mp </i></b>: &nbsp; metabolite-protein interaction
   <li> <b><i> geneFusion </i></b>: &nbsp; 
   <li> <b><i> cogInference </i></b>: &nbsp; 
   <li> <b><i> pullsDown </i></b>: &nbsp; 
   <li> <b><i> activates</i></b>: &nbsp;
   <li> <b><i> degrades</i></b>: &nbsp;
   <li> <b><i> inactivates</i></b>: &nbsp;
   <li> <b><i> inhibits</i></b>: &nbsp;
   <li> <b><i> interactsWith</i></b>: &nbsp;
   <li> <b><i> phosphorylates</i></b>: &nbsp;
   <li> <b><i> ubiquitylates</i></b>: &nbsp;
   <li> <b><i> upRegulates</i></b>: &nbsp;

    </div>
    <? include "footer.php"; ?>
  </body>
</html>

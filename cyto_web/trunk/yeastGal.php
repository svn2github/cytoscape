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
<h2> Explore mRNA Expression Changes Across Twenty Conditions
 </h2>
</center>

This example takes up where the first one leaves off.  Here you
will learn how to display changes in expression, and how to discover
correlations in gene expression.<p>
Begin by clicking here:  
<font size="+1">
<a href="galactoseInduction/cy.jnlp">WEB START</a>.</font> This starts Cytoscape on your own computer, after downloading the 
program and annotation from our website. (On subsequent runs, the program or 
annotation will not be downloaded again unless we have new versions or new 
annotation for you to use.) If Cytoscape does not start, please look at the
<a href="tutorial.php">instructions</a> for some places to get help.</p><p> If at any point you wish
to restart this tutorial, just click again on the <b>WEB START</b> link just above. <p>

The 331 genes in this network were selected from the full yeast genome based 
on their having significant expression change in at least one of twenty
conditions:   The wild  type (wt) strain and nine genetically altered yeast strains, 
perturbed environmentally by growth in the presence (+gal) or absence (-gal) of 2% galactose sugar. 
 Each altered strain has a complete deletion of one of the GAL genes, which encode proteins needed for
the metabolism of galactose.   In this tutorial, each experimental condition is 
denoted by the genetic composition of the strain, followed by the medium. 
For example, <b>wt-gal</b> denotes the wild-type strain grown in the absence of galactose, 
and <b>gal1+gal</b> denotes the GAL1 deletion strain grown in the presence of galactose. 
In the microarray experiment, each condition is directly compared with a standard: the wild-type
strain, grown on the same medium. This comparison is summarized as
the logarithm of the ratio of the condition over the standard, 
and this value carries the designation "ratio".

<br><br>
  For a detailed description of the experiment see
  
  Trey Ideker, Vesteinn Thorsson et al. <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=11340206&amp;dopt=Abstract"> 
  "Integrated genomics and proteomic analyses of a systematically perturbed metabolic network." </a>

 

</p><center>
<h3> Find co-regulated (and anti-regulated) genes</h3>
</center>

We have 331 genes and 20 conditions -- a huge amount of data.  Rather than probing relatively
well-known genes within this network (the transcription factor GAL4, for instance, and the
genes it regulates) we will instead  embark upon some <i><b>discovery science</b></i>
picking one gene at random from the network, and then using Cytoscape to ferret out
other genes whose expression is correlated across many conditions.  In the course of this
exercise -- and even if we turn up little of direct biological interest -- you will
learn more about how to explore your data with Cytoscape.<ol>
   <li> From the <b>Select</b> menu, choose <b>Nodes</b> and <b> By Name...</b>.  Enter
       <i> IMD3 </i> into the dialog box, click <b>Search</b> and observe that that node is
       now selected. It may be difficult to see the selected node, since the 
   network is large. To get around this problem, click the <b>Zoom Selected 
   Region</b> button on the toolbar to zoom to the selected gene.<p> </p></li><li> <i>Right-click</i> on the selected node and choose the <b>SGD</b> 
   link under the <b>Web Info</b> menu.  This will drive your browser to the
        the <a href="http://genome-www.stanford.edu/Saccharomyces"> Stanford Saccharomyces 
        Genome Database</a> entry for this gene.  We see (after a little exploration
        starting from the SGD IMD3 web page) that IMD3 is one of 3 IMP dehydrogenase genes in yeast.
   <p></p></li><li>  Return now to the Cytoscape display.  From the plugins menu, choose
          <b> Expression Correlation Finder...</b>

   <p></p></li><li> Two things happen.  First, all the genes in the network are colored in shades
           of green and red, indicating degrees of positive and negative correlation of
           the expression vector of every gene with the expression vector of IMD3.
           Second, a dialog box appears, with some explanatory text, and with a slider
           allowing you to choose values between 0 (no correlation) and 100 (perfect
           correlation or anti-correlation).

   <p></p></li><li> Move the slider so that it reads about 92.  Click on the button labeled
           <b>Select nodes above threshold</b>:  three nodes should be selected; in addition
           to IMD3, there is also RPP2B and RPL5.   You may right-click on the 
           Cytoscape window background, and then select the SGD tab in the node browser
           that pops up, in order to learn what is known about these genes.

   <p></p></li><li> Return to the <b>Plugins</b> menu, and this time select <b>Expression Profile
           Viewer </b>.  You should see an x-y plot of the expression vectors for the
           3 genes -- 3 nearly identical curves.  Note that
           even though there is nearly perfect correlation of the expression vectors of
           these three genes, there are no physical interactions among the 3, either
           direct or indirect, in this data set.    (Of course, direct or indirect 
           interaction may be recorded in other, more complete, or more recent, data sets.)

   <p></p></li><li> This correlation does not in itself constitute a significant result.
           But the correlation is at least interesting, and if the statistical significance of the 
           expression measurements is sufficiently high this striking correlation may suggest hypotheses worthy of further investigation.</li></ol>


    </div>
    <? include "footer.php"; ?>
  </body>
</html>

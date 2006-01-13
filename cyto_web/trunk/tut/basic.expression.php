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
  <STYLE>
  <!--
    @page { size: 8.5in 11in; margin-right: 1.25in; margin-top: 1in; margin-bottom: 1in }
    P { margin-bottom: 0.08in; direction: ltr; color: #000000 }
    P.western { font-family: "Times New Roman", serif; font-size: 12pt; so-language: en-US }
    P.cjk { font-family: "Times New Roman", serif; font-size: 12pt }
    P.ctl { font-family: "Times New Roman", serif; font-size: 12pt }
    A:link { color: #0000ff }
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
       <? include "../nav.php"; ?>
       <? include "nav_tut.php"; ?>
       <div id="indent">
       <center>
	   <h2> Basic Expression Analysis </h2>
	</center>
	<p>
<P>If you have completed the <A HREF="getting.started.php">Getting Started</A>
  and <A HREF="filters.editor.php">Filters and Editor</A> tutorials, 
  this tutorial will show you some expression analysis basics available
  wth Cytoscape.
  This tutorial will introduce you to:</P>
<UL>
  <LI> Input formats for expression data
  <LI> Coloring nodes by expression data values
  <LI> Assessing expression data in the context of a biological network
</UL>
This tutorial features the following data files:
<UL>
  <LI> <A HREF="basic.expression/galFiltered.sif">galFiltered.sif</A>,
       also distributed in the Cytoscape testData directory. This network
       contains protein-protein and protein-DNA interactions associated
       with Galactose metabolism in yeast.
  <LI> <A HREF="basic.expression/galExpData.pvals">galExpData.pvals</A>,
       also distributed in the Cytoscape testData directory. This file 
       contains gene expression measurements for three pertubation
       experiments.  In each experiment, the level of one key protein
       was perturbed artificially.
  <LI> <A HREF="basic.expression/galExpData.mrna">galExpData.mrna</A>,
       <b>not</b> distributed in the Cytoscape testData directory. This file 
       contains a subset of the data from <b>galExpData.pvals</b>.
</UL>
For further information on these datasets, see 
<a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=11340206&dopt=Abstract">Science 2001 292:929-34</a>.
Please download these data files to your local disk before starting.

<P>Begin by  clicking here:
<a href="webstart/cyto2.2.060106.jnlp">WEB START</a> 
(approximate download size: 22 MB) This starts Cytoscape on your own
computer, after downloading the program and annotations from our
website.  

This tutorial and accompanying lectures were delivered at 
<A HREF="http://www.csc.fi/math_topics/">Computer Science Center</A> in 
Helsinki, Finland.  The lecture slides of background material are
available <A HREF="basic.expression.ppt">here</A> and an accompanying video
presentation is available <A HREF="http://rm.tv.funet.fi:8080/ramgen/fi/csc/kurssit/2005/cytoscape/_cytos02.rm">here</A>.
<P>

  <H3>Loading expression data</H3>
<P>Here, we will explore the basic structures for applying expression
data to a Cytoscape network.  In this section, you will learn about
the expression data formats expected by Cytoscape, formatting the
nodes in your network according to expression values.</P>
<OL>
    <LI><P>Start Cytoscape
    and load the network <B>galFiltered.sif</B>.
    After detaching CytoPanels 1 and 3, maximizing the canvas,
    and applying the spring-embedded layout , you should see a nework similar 
    to the one below.</P>
    <IMG SRC="basic.expression/Fig1.jpg" WIDTH=473 HEIGHT=414></P>

    <LI><P>Using your favorite text editor, open the file 
        <B>galExpData.mrna</B>.  The first few lines of the file are as
        follows:</P>

    <IMG SRC="basic.expression/Fig2.jpg" WIDTH="45%"></P>
    <LI><P>Here is the structure of this file.  </P>
    <OL TYPE=i>
      <LI><P>The first line consists of labels.  
      </P>
      <LI><P>All columns are separated by a single whitespace character, 
             such as a space or a tab.  
      </P>
      <LI><P>The first column contains node names, and must match the 
             names of the nodes in your network <B>exactly!</B>.  
      </P>
      <LI><P>The second
      column contains common locus names. This column is optional, and
      the data is not currently used by Cytoscape, but including this
      column makes the format consistent with the output of many
      microarray analysis packages – and makes the file easier to read!
      read.  
      </P>
      <LI><P>The remaining columns contain experimental data, one column
            per experiment, and one line per node. In this case, there are 
            three expression results per node.  
      </P>
    </OL>
    <LI><P>Under the <B>File</B>
    menu, select <B>Load</B>, and <B>Expression Matrix File</B>, and
    load this file.  After a brief load, a status window will appear,
    indicating how many experimental conditions were found (three) and
    what type significance values were included (none).</P>
    <LI><P>Now we will use
    the <B>Node Attribute Browser</B> to browse through the expression
    data, as follows.</P>
    <OL TYPE=i>
      <LI><P>Select a node in the Cytoscape canvas</P>
      <LI><P>In the Node
      Attribute Browser, click the <B>Select Attributes</B> button, and
      select the attributes <B>gal1RGexp</B>, <B>gal4RGexp</B>, and
      <B>gal80exp</B>.</P>
      <LI><P>Under the  <B>Node Attribute Browser</B>,
      you should see your node listed with their expression values, as
      shown.</P>
      <IMG SRC="basic.expression/Fig3.jpg" WIDTH="75%"></P>
    </OL>
  </OL>
</OL>
<P>
<H3>Coloring nodes</H3>
<P>
Probably the most common use of expression data in Cytoscape is to 
set the visual attributes of the nodes in a network according to expression
data.   This creates a powerful visualization, portraying functional 
relation and experimental response at the same time.  Here, we will walk 
through the steps for doing this.</P>
  <OL>
    <LI><P>Go to the <B>Set Visual Properties</B> menu under 
        <B>Visualization</B>.</P>
    <LI><P>Create a new visual style named <B>Gal80</B> by duplicating the 
           default style.</P>
    <LI><P>Define the node color of this visual style as follows:</P>
    <OL TYPE=i>
      <LI><P>Under <B>Mapping</B>,
      click on the pull-down menu labeled <B>None</B> and select
      <B>RedGreen.</B> 
      </P>
      <LI><P>In the pull-down
      menu labeled <B>MapAttribute</B>, select the attribute <B>gal80RGexp</B>.
       This specifies that each node will be colored on a color
      continuum according to <B>Gal80</B> expression, as follows: 
      </P>
      <OL>
        <LI><P>Large negative
        values (indicating high repression) are colored red</P>
        <LI><P>Small negative
        values (indicating slight repression) are colored pink</P>
        <LI><P>Values close to
        zero are colored white</P>
        <LI><P>Small positive
        values (indicating slight induction) are colored light green</P>
        <LI><P>Large positive
        values (indicating high induction) are colored bright green.</P>
        <LI><P>Extreme values
        (negative values less than -2.5 and positive values greater than
        2.1) are colored blue and black respectively.</P>
      </OL>
      <LI><P>Note that the
      default node color of pink falls within this spectrum.  A useful
      trick is to choose a color outside this spectrum, to distinguish
      nodes with no expression value defined from those with slight
      repression.  Under <B>Default</B>, click on <B>Change Default</B>,
      and select a default color of grey.  
      <LI><P>The <B>Set Visual Properties</B>
      menu should appear as follows: </P>
      <IMG SRC="basic.expression/Fig5.jpg" WIDTH=336 HEIGHT=462></P>
      <LI><P>Click on <B>Apply
      to Network</B>.  You should see most nodes colored pink, green, or
      white, with a few grey nodes and a few black nodes.</P>
    </OL>
  </OL>
<P>
<H3>Using P Values</H3>
<P>
Expression values, such as the ones abov, generally rpresent fold changes,
log ratios comparing the expression level in the experiment to some baseline
condition  But some genes have greater variation in their expression levels 
normally, so many microarray data analysis packages also report significance
measures, such as P values,to indicate if the observed fold change exceeds
the normal variability for the gene.  Here, we shall explore the P value 
support in Cytoscape, and one way of using expression values and P values 
together in setting visual properties.
<OL>
   <LI> Using your favorite text editor, open the file <B>galExpData.pvals</B>.
        You will see the following format:
        <P><IMG SRC="basic.expression/Fig4.jpg" WIDTH="50%">
   <LI> Notice how thereare two columns labeled <B>gal1RG</B>, 
        two labeled <B>gal4RG</B>, and two labeled <B>gal80R</B>.  
        In each case, the first of the two columns contains the expression 
        value for the experiment, while the second contains the P value.
   <LI> Load the expression matrix file <B>galExpData.pvals</B>.
   <LI> In the <B>Node Attribute Browser</B>, look at the list of 
        available attributes.  Notice how there are pairs of 
        attribute names, such as <B>gal80Rexp</B> and <B>gal80Rsig</B>.
        The first is the expression value, and the second is the P value.
   <LI> Select some nodes, and look at their expression values and P values
        under the <B>Node Attribute Browser</B>.  Notice how while the 
        expression data value ranges from about -3 to +3 in these cases,
        the P value ranges from 0 to 1 - as it should
   <LI> Now, we will explore setting node shapes according to P values.
        <OL>
	   <LI> Go to <B>Set Visual Style</B> under <B>Visualization</B>.
           <LI> Go to the <B>Node Shape</B> tab.
	   <LI> In the pull-down menu under <B>Mapping</B>, select
                <B>BasicContinuous</B>
	   <LI> In the <B>Map Attribute</B> pull-down menu, select
                <B>gal80Rsig</B>.
	   <LI> By default, you will see a button labeled <B>Below</B>, 
                a button labeled <B>Above</B>, and three <EM>points</EM>, 
                three input fields
                each with a <B>Del</B> button at the left and a <B>Equal</B>
                button at the right. 
           <LI> Click on one of the three <B>Del</B> buttons to delete
                one of the three points.
           <LI> Click on the <B>Below</B> button.  This should bring up
                the <B>Select Appearance</B> menu, shown below:
                <P><IMG SRC="basic.expression/Fig8.jpg" WIDTH="20%">
           <LI> Select a square.
           <LI> In the line below <B>Below</B>, set the number in the 
                input field to 0, click on the <B>Equals</B> button,
                and select a square.
           <LI> In the following line, set the number in the input field
                to 0.05.
           <LI> The <B>Set Visual Style</B> window should appear as 
                follows:
                <P><IMG SRC="basic.expression/Fig9.jpg" WIDTH="50%"> 
                <P>This will have the effect of depicting nodes with 
	        P values of less than 0.05 as squares, and all other nodes
                as circles.
           <LI> Click on <B>Apply to Network</B>  On your Cytoscape canvas,
                your node shapes should change, as shown below:
                <P><IMG SRC="basic.expression/Fig10.jpg" WIDTH="50%">
        </OL>
    </OL>
<P>
<H3>A Biological Analysis Scenario</H3>
This section presents one scenario on how expression data can be combined with
network data to tell a biological story.</P>
First, here is
some background on your data.  You are working with yeast, and the
genes <B>Gal1</B>, <B>Gal4</B>, and <B>Gal80</B> are all yeast
transcription factors.  Your expression experiments all involve
some pertubation of these transcription factor genes.  <B>Gal1</B>,
<B>Gal4</B>, and <B>Gal80</B> are also represented in your
interaction network, where they are labeled according to yeast
locus tags: <B>Gal1</B> corresponds to <B>YBR020W</B>, <B>Gal4</B>
to <B>YPL248C</B>, and <B>Gal80</B> to <B>YML051W</B>.</P>
<OL>
    <LI><P>Your network
    contains a combination of protein-protein (pp) and protein-DNA (pd)
    interactions.  Here, we shall filter out the protein-protein
    interactions to focus on the protein-DNA interactions.</P>
    <OL TYPE=i>
      <LI><P>Create a String Pattern filter to select edges with text 
      attributes of <B>interaction</B> that match the pattern <B>pp</B>.  
      For more information, see the 
      <A href="filters.editor.php">tutorial</A> on filters and editing.</P>
      <LI><P>Click on <B>Apply
      selected filter</B>.  This should select 251 of the 362 edges.</P>
      <LI><P>Under the <B>Edit</B>
      menu, select <B>Delete Selected Nodes/Edges</B></P>
      <LI><P>Apply a graph
      layout algorithm to see the edges that remain.  Using the <B>yFiles
      Organic</B> layout, your network should now appear as follows:</P>
      <P><IMG SRC="basic.expression/Fig6.jpg" WIDTH="60%"></P>
    </OL>
    <LI><P>Notice that all
    three black (highly induced) nodes are in the same region of the
    graph.    Zoom into the graph to see more details.</P>
    <LI><P>Notice that there
    are two nodes that interact with all three black nodes: <B>YPL248C</B>
    and <B>YOL051W</B>.  Select these two nodes and their immediate
    neighbors, and copy them to a new network.  This makes it easier
    to focus on the interactions involving these nodes.
    With some layout and
    zooming, this new network should appear similar to the one shown: 
    <P><IMG SRC="basic.expression/Fig7.jpg" WIDTH="60%" BORDER=1></P>
    <LI><P>With a little
    exploration in the node attribute browser, you should see the
    following:</P>
    <OL TYPE=i>
      <LI><P>The two nodes
      that interact with all three black nodes are <B>YOL051W</B>
      (<B>Gal11</B>, a general transcription cofactor with many
      interactions) and <B>YPL248C</B> (<B>Gal4</B>). 
      </P>
      <LI><P>Both nodes show
      fairly small changes in expression, and neither change is 
      statistically-significant: are rendered as light-colored circles.
      the 0.01 threshold.  These slight changes in expression suggest
      that the critical change affecting the black nodes might be 
      somewhere else in the network.
      either of these nodes.</P>
      <LI><P><B>YPL248C</B>
      interacts with <B>YML051W</B> (<B>Gal80</B>), which is shows a
      significant level of repression: it is depicted as a reddish square.  
      <LI>Note that while <B>YML051W</B> shows evidence of significant
      repression, most nodes interacting with <B>YPL248C</B> show 
      significant levels of induction: they are rendered as green or black
      squares.
    </OL>
    <LI><P>Go to the NCBI
    website (<<A HREF="http://www.ncbi.nlm.nih.gov/">http://www.ncbi.nlm.nih.gov/</A>),
    and search the <B>Gene</B> database for <B>YPL248C</B>.  The items
    returned should include <B>Gal4</B>.  Click on the link for <B>Gal4</B>
    to get more information.</P>
    <LI><P>Reading the
    description of <B>Gal4</B>, you will see that it is a transcription
    factor that is repressed by <B>Gal80</B>.  
    </P>
    <LI><P>Putting all of
    this together, we see that the transcriptional activation activity
    of <B>Gal4</B> is repressed by <B>Gal80</B>.  So, repression of
    <B>Gal80</B> increases the transcriptional activation activity of
    <B>Gal4</B>. Even though the expression of <B>Gal4</B> itself did not 
    change much, the <B>Gal4</B> transcripts were much more likely to be
    active transcription factors when <B>Gal80</B> was repressed.
    This explains why there is so much up-regulation in
    the vicinity of <B>Gal4</B>.</P>
  </OL>
</OL>
<P STYLE="margin-left: 0.75in; margin-bottom: 0in"><BR>
</P>
<P><B>Good work!</B> 
Network analysis and expression data are a powerful combination, and
now you have the skills to do some substantial analysis.  Go reward
yourself with a good cup of coffee. 
</P>
</BODY>
</HTML>

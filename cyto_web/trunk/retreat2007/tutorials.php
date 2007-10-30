<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
  <title>5th Annual Cytoscape Retreat and Symposium - Tutorials</title>
  <link rel="stylesheet" type="text/css" media="screen" href="css/cytoretreat.css">
  <link rel="../shortcut icon" href="../images/cyto.ico">
  <!--[if lt IE 7]>
  <script defer type="text/javascript" src="js/pngfix.js"></script>
  <![endif]-->
  <script src="js/onloadheaderonly.js" type="text/javascript"></script>  
</head>

<body style="background-color: rgb(255, 255, 255);">

<!-- The header with changing pictures -->

<? include "header.php"; ?>

<!-- Retreat content table-->

<table>
 <tr valign="top">
  <!-- Left retreat navigation bar -->
  <td width="10%">
   <? include "nav_retreat.php"; ?>
  </td>
  <!-- This page content -->
  <td width="70%">
  <div id="content">
  <div class="item">
  <div class="top">
   <h2><a name="top">Tutorials</a></h2>
  </div>
<br>  
<p>
Find the descriptions of the Tutorials presented at the retreat here. 
</p>
<hr>
<div class="top">
 <h2>Overview Tutorials</h2>
</div>
<br>
<table class="programme">
 <tr>
  <td class="subheader" width="20%"><a href="#cyto"><strong>Cytoscape Tutorials</strong></a></td>
  <td class="subheader" width="80%">The intended audience is end users for the software i.e. scientific researchers in fields such as bioinformatics, computational biology, molecular biology and 'omics.</td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><a href="#dev"><strong>Developers Tutorials</strong></a></td>
  <td class="subheader" width="80%">For developers of plugins; (basic)knowledge of Java programming required</td>
 </tr>
</table>  

<hr>
<div class="top">
 <h2>Tutorial descriptions</h2>
</div>

<br>
<h3><a name="cyto">Cytoscape Tutorial</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Name and contact information for tutors</p></td>
 </tr>
 <tr>
  <td><p>Guy Warner, Safety & Environmental Assurance Centre, Unilever, Colworth Park, Sharnbrook, Bedfordshire MK44 1LQ. Guy{dot}Warner[at]unilever{dot}com<br>
  <br> 
Andrew Garrow, Safety & Environmental Assurance Centre, Unilever, Colworth Park, Sharnbrook, Bedfordshire MK44 1LQ. Andrew{dot}Garrow[at]unilever{dot}com<br>
<br> 
Yeyejide Adeleye, Safety & Environmental Assurance Centre, Unilever, Colworth Park, Sharnbrook, Bedfordshire MK44 1LQ. Yeyejide{dot}Adeleye[at]unilever{dot}com 
</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Length of tutorial</p></td>
 </tr>
 <tr>
  <td><p>1.5 - 2 hours</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Audience</p></td>
 </tr>
 <tr>
  <td><p>
  The intended audience is end users for the software i.e. scientific researchers in fields such as bioinformatics, computational biology, molecular biology and 'omics. 
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Background required</p></td>
 </tr>
 <tr>
  <td><p>
Basic understanding of molecular biology, knowledge of publicly available bioinformatics databases and some experience of gene expression analysis (or any high-throughput data analysis) is desirable but not essential
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Relevance of the tutorial and interest to the community</p></td>
 </tr>
 <tr>
  <td><p>
Traditionally, the results of HTP data analysis are a list of bio-molecules that are believed to be significantly differentially expressed between experimental conditions. As data become more complex (e.g. time series data), statistical approaches such as clustering and classification have been used to reveal patterns within a data set. 
However, statistical analyses of gene expression data (and any molecular state data) that list differentially expressed genes may not be sufficient to allow researchers to generate new hypothesis and to gain insights into mechanisms underlying the conditions and systems being investigated. 
This is because cellular processes are carried out not through individual bio-molecules but via complex interactions between genes, proteins and metabolites: that is, through biological networks. Understanding this organisation and analysing molecular state data in the context of biological networks is crucial to obtaining a picture of cellular activity. 
Cytoscape is an open-source software package for the visualisation and analysis of biological networks and the integration of molecular state data. Cytoscape provides functionality to layout and search networks; to visually integrate networks with expression profiles, phenotypes, and other molecular states; and to link to databases of functional annotations.
In this tutorial we hope to provide an introduction to network-based analysis using the Cytoscape software that will be of relevance to researchers with an interest in the analysis of biological data in the context of biological networks and pathways.
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Tutorial Overview</p></td>
 </tr>
 <tr>
  <td><p>
  1. Introduction to Cytoscape Graphical User Interface:
This section will introduce the Cytoscape UI and the central organising metaphors of Cytoscape such as: A network consists of genes/proteins/metabolites (nodes) and interactions represented as links (edges between nodes). 
The introduction will also include an overview of Cytoscape's core and extended functionality.
<br>
<br>
2. Constructing and Loading Biological Networks into Cytoscape:
This section will describe how to construct and load biological networks and associated annotations. Networks can be constructed from interaction data found in databases such as BIND, DIP and HPRD. Integrating data from these databases is a major bioinformatics challenge and will not be covered in this tutorial however certain Cytoscape plug-ins can be used to retrieve data from external sources and even directly from literature (Agilent Literature Plugin). Suitable biological networks will be provided for participants.
Concepts of annotating nodes (e.g. with Gene Ontology information) & edges (e.g. published evidence for interactions) and the ability to link out to external sources of information will be covered. 
File formats; Saving sessions; Exporting visualisations; etc will also be covered. 
<br>
<br>
3. Visualisation and Analysis of Biological Networks:
After loading a biological network and associated annotations, participants will be shown how to visualise and manipulate networks using the various graphical layout algorithms available in Cytoscape. 
The ability to set visual aspects of the nodes and edges (e.g. shape, colour, size) based on attribute values will be demonstrated. 
Biological networks can be very large and complex, therefore participants will be shown how to reduce this complexity using some of Cytoscape's functionality. For example: identifying highly connected regions of the network (MCODE); determining shortest paths between nodes of interest. 
Also included within this section would be how to query biological networks using Cytoscape search functions.
<br>
<br>
4. Integration of Molecular State Data with Biological Networks:
This section will cover the integration of molecular state data onto biological networks in order to analyse high throughput data in a biological context. DNA microarray data will be used as an example throughout the tutorial however, it should be noted that any type of data can be used.
A certain amount of processing (normalisation, quality control, differential gene expression analysis) is required prior to loading any expression data into Cytoscape. These topics are out of scope for this tutorial but suitable datasets will be provided to participants. File formats for the expression data will be discussed. 
After integrating molecular state data with biological networks, participants will be shown how to use visual styles (e.g. colouring nodes by expression values, node size according to P-values etc) to aid network analysis and visualisation.
<br>
<br>
5. Advanced Sub-network Identification (Data driven):
Participants will be show how to find sub-networks of importance by searching the network to identify regions of importance using Cytoscape's filtering capabilities. 
Other more complex methods of sub-network identification using both expression and interaction data will be covered: The jActiveModules Plugin is used to identify 'active' sub-networks containing genes (nodes) that are not only significantly expressed (over particular conditions) but that are also highly connected. In this way, areas of the network affected by a particular experimental condition can be identified.
<br>
<br>
6. Questions and Answer Session (10 mins):
Brief Q&A session to wrap-up the tutorial and provide further support to delegates as necessary.

  </p></td>
 </tr>
 </table>
 
<br>
<h3><a name="dev">Developers Tutorials</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p><strong>Tutorial</strong></p></td>
 </tr>
 <tr>
  <td><p><strong>CyGroups and Implementing a CyGroupViewer</strong></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Tutor</p></td>
 </tr>
 <tr>
  <td><p>Scooter Morris</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>This tutorial will introduce the new CyGroups mechanism in Cytoscape and walk through the implementation of a CyGroupViewer.  The namedSelection and metaNodePlugin2 group viewers will be used as examples. </p></td>
 </tr>
</table>

<table class="programme">
 <tr>
  <td class="subheader"><p><strong>Tutorial</strong></p></td>
 </tr>
 <tr>
  <td><p><strong>Layouts - how to implement a new layout algorithm in cytoscape</strong></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Tutor</p></td>
 </tr>
 <tr>
  <td><p>Scooter Morris</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>This tutorial will walk through some of the steps required to implement a new layout algorithm into Cytoscape.  It will cover the CyLayoutAlgorithm interface and the AbstractLayout class that can be used as a starting point.  We will also cover the new "Tunables" approach to constructing a Settings panel for users to use to tune the algorithm.</p></td>
 </tr>
</table>

<table class="programme">
 <tr>
  <td class="subheader"><p><strong>Tutorial</strong></p></td>
 </tr>
 <tr>
  <td><p><strong>Vizmapper</strong></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Tutor</p></td>
 </tr>
 <tr>
  <td><p>Keichiri Ono - Mike Smoot</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>Learn how to adapt visual properties of Cytoscape programmatically</p></td>
 </tr>
</table>

<table class="programme">
 <tr>
  <td class="subheader"><p><strong>Tutorial</strong></p></td>
 </tr>
 <tr>
  <td><p><strong>Webservices</strong></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Tutor</p></td>
 </tr>
 <tr>
  <td><p>Keichiri Ono</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>How to use the newly developed webservices api.</p></td>
 </tr>
</table>

  </div>
  </div>

  </td>  <!--End Main page content -->

  <!-- Sponsors box -->
  <td width="20%">
   <? include "hosted_box.php"; ?>
   <? include "sponsors_box.php"; ?>
  </td>
 </tr>
</table>
<? include "footer.php"; ?>
</body>
</html>

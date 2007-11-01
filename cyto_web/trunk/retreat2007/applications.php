<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
  <title>5th Annual Cytoscape Retreat and Symposium - Application showcase and Plugins</title>
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
   <h2><a name="top">Application showcase and Plugins</a></h2>
  </div>
<br>  
<p>
Find the descriptions of the Applications and Plugins presented at the retreat here.
</p>
<hr>
<div class="top">
 <h2><a name="table_top">Overview Application showcase and Plugins (in order of appearance)</a></h2>
</div>
<br>

<table class="programme">
 <tr>
  <td class="subheader" width="20%"><strong>Mario Albrecht</strong></td>
  <td class="subheader" width="80%"><a href="#albrecht">NetworkAnalyzer</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Dorothea Emig</strong></td>
  <td class="subheader" width="80%"><a href="#albrecht">DomainGraph, BiLayout</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Ben Hitz</strong></td>
  <td class="subheader" width="80%"><a href="#hitz">SgdInteractionsPlugin</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Maital Ashkenazi</strong></td>
  <td class="subheader" width="80%"><a href="#ashkenazi">EnhancedSearch</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Willem Ligtenberg</strong></td>
  <td class="subheader" width="80%"><a href="#ligtenberg">Reconn</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Aaron Barsky</strong></td>
  <td class="subheader" width="80%"><a href="#barsky">Cerebral</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Sabry Razick</strong></td>
  <td class="subheader" width="80%"><a href="#razick">Bioscape</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Eugene Rakhmatulin</strong></td>
  <td class="subheader" width="80%"><a href="#rakhmatulin">Metacore</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Thomas Kelder</strong></td>
  <td class="subheader" width="80%"><a href="#kelder">GPML-plugin</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Evrim Itir Karac</strong></td>
  <td class="subheader" width="80%"><a href="#karac">Molecular Interaction Maps</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Yves Deville</strong></td>
  <td class="subheader" width="80%"><a href="#deville">BioEdge</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Robert Kincaid</strong></td>
  <td class="subheader" width="80%"><a href="#kincaid">VistaClara</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Alan Kuchinsky</strong></td>
  <td class="subheader" width="80%"><a href="#kuchinsky">Literature search, Hyperedges, Workflow</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Alex Pico</strong></td>
  <td class="subheader" width="80%"><a href="#pico">BubbleRouter</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Christoph Schwarz</strong></td>
  <td class="subheader" width="80%"><a href="#schwarz">Vispara</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Steven Maere</strong></td>
  <td class="subheader" width="80%"><a href="#maere">BinGO</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Gary Bader</strong></td>
  <td class="subheader" width="80%"><a href="#bader">PathwayCommons, GoSlimmer, Thematic Maps</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>July Dickerson</strong></td>
  <td class="subheader" width="80%"><a href="#dickerson">MetNet tools</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Matthias Reimann</strong></td>
  <td class="subheader" width="80%"><a href="#reimann">EagleVista</a></td>
 </tr>
 <tr>
  <td class="subheader" width="20%"><strong>Scooter Morris</strong></td>
  <td class="subheader" width="80%"><a href="#morris">CyGroups, StructureViz, SFLDLoader</a></td>
 </tr>
</table>  


<hr>
<div class="top">
 <h2>Detailed abstracts</h2>
</div>

<br>
<h3><a name="albrecht">NetworkAnalyzer - DomainGraph - BiLayout</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Mario Albrecht, Dorothea Emig</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p>paper submitted, <a href="http://medbioinf.mpi-inf.mpg.de/" target="_blank">http://medbioinf.mpi-inf.mpg.de</a></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
NetworkAnalyzer: Analysis of network topologies.
DomainGraph: Visualization of domain interaction graphs.
BiLayout: Layout of bipartite networks.
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="networkanalyzer" height="258" width="700" src="images/plugins/networkanalyzer_700_258.png"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="hitz">SgdInteractionsPlugin</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Benjamin Hitz</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
An interface (in Perl) between the SGD database (interactions from BioGRID) and cytoscape using the XGMML format + webstart. The idea is to make cytoscape available to casual users of SGD, and to display networks (currently protein-protein physical & genetic interactions) in an informative and esthetically pleasing manner to someone who does not have to process files or learn the vizmapper.  </p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="ashkenazi">EnhancedSearch</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Maital Ashkenazi </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p><a href="http://conklinwolf.ucsf.edu/genmappwiki/Google_Summer_of_Code_2007/Maital" target="_blank">http://conklinwolf.ucsf.edu/genmappwiki/Google_Summer_of_Code_2007/Maital</a></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
Cytoscape ESP (Enhanced Search Plugin) enables searching complex biological networks on multiple attribute fields using logical operators and wildcards. Queries use an intuitive syntax and simple search line interface. ESP complements existing search functions in Cytoscape, allowing users to easily identify nodes, edges and subgraphs of interest, even for very large networks. ESP is written in Java and based on the high performance open-source Lucene information retrieval library (http://lucene.apache.org/).  </p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>


<br>
<h3><a name="ligtenberg">ReConn</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Willem Ligtenberg </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p><a href="http://bmi.bmt.tue.nl/reconn/" target="_blank">http://bmi.bmt.tue.nl/reconn/</a></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
ReConn is a new JAVA plugin for Cytoscape to visualize pathway
information from Reactome in Cytoscape. ReConn is used for micro-array
data analysis, in silico knockout experiments and is a platform to make
better use of the Reactome database schema. For example pathways can be
grown from a compound and several organisms can be handled in one and the same view.  
</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="reconn" height="369" width="668" src="images/plugins/reconn_1136_737.png"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="barsky">Cerebral</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Aaron Barsky ea</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p>Barsky A, Gardy JL, Hancock REW, and Munzner T. (2007) Cerebral: a Cytoscape plugin for layout of and interaction with biological networks using subcellular localization annotation. Bioinformatics 23(8):1040-2. 
  <br><a href="http://www.pathogenomics.ca/cerebral/index.html" target="_blank">http://www.pathogenomics.ca/cerebral/index.html</a></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
Cerebral (Cell Region-Based Rendering And Layout) is an open-source Java plugin for the Cytoscape biomolecular interaction viewer. Given an interaction network and subcellular localization annotation, Cerebral automatically generates a view of the network in the style of traditional pathway diagrams, providing an intuitive interface for the exploration of a biological pathway or system. The molecules are separated into layers according to their subcellular localization. Potential products or outcomes of the pathway can be shown at the bottom of the view, clustered according to any molecular attribute data - protein function - for example. Cerebral scales well to networks containing thousands of nodes. Version 2.0 will
allow microarray expression data analysis in the context of the
biomolecular interaction graph across multiple conditions.
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="cerebral" height="462" width="456" src="images/plugins/cerebral_913_923.png"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="razick">Bioscape</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Sabry Razick </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
Communicates with the data-warehouse we are building. This plugin uses SOAP to send and receive information over network. It is useful to analyze interactions of a given protein. Special feature is it is capable of retrieving information on other proteins related to the given protein (same sequence, other proteins coded by the same gene etc..) and their interactions.  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="bioscape" height="384" width="512" src="images/plugins/bioscapeplugin_1024_768.png"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="rakhmatulin">Metacore Integration</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Eugene Rakhmatulin ea</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
Communication with the commercially available network tool Metacore  </p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="kelder">GPML plugin</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Thomas Kelder ea</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
The GPML plugin for Cytoscape is a converter between Cytoscape networks and the GPML (GenMAPP Pathway Markup Language) pathway format. Pathways in the GPML format are not restricted to nodes and edges, but can contain additional shapes and labels that serve as visual annotations. This plugin makes it possible to combine the facilities of Cytoscape and the pathway analysis tools GenMAPP and PathVisio to improve the current pathway content. The plugin provides copy/paste functionality to easily transfer (parts of) networks and pathways back and forth between these tools. It also enables Cytoscape users to include visual annotations in a Cytoscape network, for example to help interpretation or clarify images of the network for publication.  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="gpmlplugin" height="291" width="700" src="images/plugins/gpml-plugin-1_1397_582.png"></p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="gpmlplugin" height="419" width="609" src="images/plugins/gpml-plugin-apo_1218_838.png"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="karac">MIM (Molecular Interaction Map) Tools </a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>E. Itir Karac </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
Construction and drawing of biochemical networks in MIM
Notation and analysis of MIMs with graph theoretical algorithms. This will be implemented as a Cytoscape plugin in the near future
  </p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="deville">BioEdge</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Yves Deville ea</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p><a href="http://bioedge.info.ucl.ac.be" target="_blank">http://bioedge.info.ucl.ac.be</a></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
BioEdge is a set of tools dedicated to the analysis of (biological) networks. 
The current functionalities of BioEdge tools are:
<br>* Statistical summary of a network: average (in/out) degree, number of (weak/strong) connected components...
<br>* Pathway inference with (constrained) shortest paths approach. 
<br>* Extraction of (weak/strong) connected components.
<br>* Extraction of context of subgraphs (pathways).
<br>* Motif detection.
<br>* Extraction of relevant subgraph.
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="bioedgekwalks" height="512" width="640" src="images/plugins/bioedge_kwalks_1280_1024.png"></p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="bioedgecontext" height="512" width="640" src="images/plugins/bioedge_context_1280_1024.png"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="kincaid">VistaClara</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Robert Kincaid </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p>R. Kincaid, VistaClara: an interactive visualization for exploratory analysis of DNA microarrays, Proceedings of the 2004 ACM symposium on Applied computing, ACM Press, Nicosia, Cyprus 2004</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
VistaClara was originally designed as a standalone tool for highly interactive visual analysis of gene and protein expression data. The VistaClara Plugin integrates this same functionality within Cytoscape as an additional data panel. The design employs a concept from the field of Information Visualization called the reorderable matrix. The familiar heat map view of expression data is used as the primary user interface, but also includes gene-based metadata. Such metadata might include ontology annotations, chromosome location, computational classifications, etc. Genes can be sorted by metadata columns or expression value. Experiments can be reordered as well. A graphical tabular view is coordinated with a condensed overview of the entire expression data set enabling the user to view both details and global context simultaneously. Single experiments can be quickly selected by simply clicking on a column header to cause that experiments color map to be projected onto the corresponding Cytoscape network view. This mechanism facilitates rapid selection and comparison of experiment data within the context of the network display. Selection of gene sets is synchronized between VistaClara and the active network view to allow rapid navigation of interesting features from either the expression or the network context.  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="vistaclara" height="558" width="790" src="images/plugins/vistaclara_790_558.jpg"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

 
<br>
<h3><a name="kuchinsky">Literature Search - Hyperedges - Excentric Labels - Nature Protocols Workflow Panel - Gradient NodeViews</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Allan Kuchinsky</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p>A. Vailaya, P. Bluvas, R. Kincaid, Allan Kuchinsky, M. L. Creech, A. Adler: "An architecture for biological information extraction and representation". Bioinformatics 21(4): 430-438 (2005)</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
(1). Agilent Literature Search is a meta-search tool for automatically querying multiple text-based search engines and extracting associations among genes/proteins of interest. Computationally extracted associations are grouped into a network that is viewed and manipulated in Cytoscape.  In addition to Agilent Literature Search, I do some very quick demonstrations of plugins for enhanced expressiveness and usability in Cytoscape: (2) Hyperedges, an extension to the Cytoscape API and editor that allows for the construction and editing of biochemical networks and other networks where Edges may have more than one Node,  (3) Excentric Labels: a dynamic technique of neighborhood labeling for data visualization. When the cursor stays more than one second over an area where objects are available, all labels in the neighborhood of the cursor are shown without overlap.   This plugin was developed jointly with Ethan Cerami of MSKCC and based upon functionality in the InfoVis toolkit developed by Jean-Daniel Fekete at Inria, (4) Nature Protocols Workflow Panel, a vertical menu that presents to the user an ordered set of actions for importing networks, importing data, analyzing networks, and publishing results, as described in our Cytoscape publication in Nature Protocols, and (5) Gradient NodeViews, a plugin that uses Custom Node Graphics to provide a sense of depth to NodeViews via gradient color fill and beveled borders, as an approach towards publication-quality graphics output from Cytoscape.  This is an early experiment and is work done with Ben Gross at MSKCC.  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshots</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="litsearch_hypedge" height="540" width="720" src="images/plugins/agilent-1_720_540.png"></p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="gradient_protocols" height="540" width="720" src="images/plugins/agilent-2_720_540.png"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="pico">BubbleRouter</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Alex Pico, Allan Kuchinsky, Kristina Hanspers, Nathan Salomonis</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
You have a network or list of genes from an array study or literature search and you want to organize and layout the nodes in a biologically meaningful way. Instead of using the current layout algorithms that rely on network-based parameters, the Bubble Router plugin provides an interactive layout experience. You can draw rectangular regions anywhere on the canvas and then route nodes to the region by selecting an attribute and value from the available node attributes. You can also load and reference species-specific cellular component attributes that we've prepared and will distribute with the plugin, allowing layouts based on a compact set of cell compartments such as Nucleus, Cytoplasm, Extracellular, Plasma Membrane, etc. The BubbleRouter is also a test vehicle for two additional constructs we are experimenting with for Cytoscape: multi-layered canvas and arbitrary graphical annotations. The multi-layered canvas extends the current notion of a network view to also support FOREGROUND and BACKGROUND layers, which can be used to place annotations, background figures, etc. Arbitrary graphical annotations are graphical elements -- such as brackets, freestanding text, and arbitrary lines and shapes -- that are not tied to Cytoscape Nodes and Edges.
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="networkanalyzer" height="287" width="525" src="images/plugins/bubblerouter_1047_573.jpg"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="schwarz">Vispara </a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Christoph Schwarz ea </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p><a href="http://www.vispara.org" target="_blank">www.vispara.org</a></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
The Vispara plugin is designed to bypass Cytoscape's restriction that nodes can only have one of eight predefined shapes, e.g. circles or rectangles.
More precisely it changes the appearance of the nodes in a network
representing protein interactions so that the paralogue distribution of the
proteins can be identified by their sheer looks. This is achieved by defining a visual style which covers the original nodeviews and by adding custom graphics to the single nodeviews. These custom graphics are basically common java.awt.shapes which are calculated based on the paralogous distribution of the particular proteins.
  </p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>
 
<br>
<h3><a name="maere">BiNGO</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Steven Maere ea.</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p>Maere, S. , Heymans, K. and Kuiper, M. (2005) BiNGO: a Cytoscape plugin to assess overrepresentation of Gene Ontology categories in biological networks. Bioinformatics 21, 3448-3449.</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
  BiNGO is a Java-based tool to determine which Gene Ontology (GO)
categories are statistically overrepresented in a set of genes or a subgraph of a biological network. BiNGO is implemented as a plugin for Cytoscape, which is a an open source bioinformatics software platform for visualizing and integrating molecular interaction networks. BiNGO maps the predominant functional themes of a given gene set on the GO hierarchy, and outputs this mapping as a Cytoscape graph. Gene sets can either be selected or computed from a Cytoscape network (as subgraphs) or compiled from sources other than Cytoscape (e.g. a list of genes that
are significantly upregulated in a microarray experiment). The main advantage of BiNGO over other GO tools is the fact that it can be used directly and interactively on molecular interaction graphs. Another plus is that BiNGO takes full advantage of Cytoscape's versatile visualization environment. This allows you to produce customized high-quality figures.
Features include :
   <br>* assessing overrepresentation or underrepresentation of GO categories
   <br>* Graph or gene list input
   <br>* batch mode : analyze several clusters simultaneously using same
     settings
   <br>* Different GO and GOSlim ontologies
   <br>* Wide range of organisms
   <br>* Evidence code filtering
   <br>* Hypergeometric or binomial test for overrepresentation
   <br>* Multiple testing correction using Bonferroni (FWER) or
     Benjamini&Hochberg (FDR) correction
   <br>* Interactive visualization of results mapped on the GO hierarchy.
   <br>* extensive results in tab-delimited text file format
   <br>* making and using your own annotation files is easy
   <br>* open source
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="attention" height="627" width="733" src="images/plugins/bingonetwork_733_627.jpg"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="bader">PathwayCommons - GoSlimmer - Thematic Map</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Gary Bader ea</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p><a href="http://baderlab.org" target="_blank">http://baderlab.org</a></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
1: The Pathway Commons web start. Enables viewing pathways directly from the pathwaycommons.org website
<br>
<br>2. The GO slimmer plugin, for creating a custom set of reduced GO terms
for analysis. Webstart available from http://yeastgenomics.ca/resources
<br>
<br>3. The thematic map plugin, for linking attributes based on connectivity
between gene and protein products.
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="pathwaycommons" height="517" width="750" src="images/plugins/goslimmer_1500_1034.png"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="dickerson">MetNet tools</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Julie Dickerson, Josette Etzel, Kyongryun Lee, Rao</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p><a href="http://metnet.vrac.iastate.edu/MetNet_fcmodeler.htm" target="_blank">http://metnet.vrac.iastate.edu/MetNet_fcmodeler.htm</a></p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
1: Subgraph: Functions for flexible subgraph creation, node identification, cycle finder, and path finder in directed and undirected graphs. It also has a function to select the p-neighborhood of a selected node or group of nodes which can be
selected by pathway name, node type, by a list in a file, cycles and/or pathways in
the network. This generates a set of plug-ins called: path and cycle finding, pneighborhoods and subgraph selection.
<br>
<br>2: OmicsViz: Omics Data Translation and Viewing: the Pvals plug-in provides translation capabilities between different sets of node names such as probe sets and gene loci or between probe sets of different species. For example, probe sets from grape or soybean may be mapped onto probe sets for Arabidopsis. This allows users to see how their genes of interest map onto the pathways for Arabidopsis and to view the expression values.   </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Screenshot</p></td>
 </tr>
 <tr>
  <td><p><img border="0" align="middle" alt="networkanalyzer" height="540" width="720" src="images/plugins/metnet_720_540.png"></p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="reimann">EagleVista</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Matthias Reimann </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
Through a novel visualization modules in networks can be detected and displayed in a way which reduces the number of edges significantly. As a result, complex networks can be displayed in a much more convenient way. The plugin includes a few algorithms to get the new visualization from a ordinary graph in cytoscape.  </p></td>
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>

<br>
<h3><a name="morris">CyGroups - StructureViz - BatchTool</a></h3>
<br>

<table class="programme">
 <tr>
  <td class="subheader"><p>Developer(s)</p></td>
 </tr>
 <tr>
  <td><p>Scooter Morris</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Structure-Function: SFLDLoader, structureViz</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Reference</p></td>
 </tr>
 <tr>
  <td><p>Morris J, Huang C, Babbitt P, and Ferrin T. (2007) structureViz: linking Cytoscape and UCSF Chimera.  Bioinformatics 23(17): 2345-2347<br>
  <br><a href="http://www.rbvi.ucsf.edu/Research/cytoscape" target="_blank">http://www.rbvi.ucsf.edu/Research/cytoscape</a>
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
structureViz links the visualization of biological (and biological relationships expressed as networks) provided by Cytoscape with the visualization and analysis of macromolecular structures and sequences provided by the molecular visualization package: UCSF Chimera.  structureViz uses node annotations to load appropriate structures into Chimera for visualization and analysis.  If multiple structures are loaded, Chimera's alignment features may be used to compare the structures.  The results of the alignment can be reflected back to the Cytoscape network by the addition of an edge, with the alignment scores added as edge attributes.
SFLDLoader provides an interface to the SFLD Database (http://sfld.rbvi.ucsf.edu) which is a highly curated database of protein superfamilies.  XGMML networks downloaded from SFLD include structural information as well as sequence data that may be used by structureViz in addition to other tools.
  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>CyGroups: metaNodes, namedSelections, and groupTool</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
The new CyGroup capability within cytoscape provides the opportunity for the addition of significant functionality and visualizations that were not available (or very difficult) before.  However, without group viewers, this functionality is not visible and hence of questionable utility.  The namedSelectionPlugin is a group viewer that implements a simple model of groups: essentially the ability to "remember" a selected group of nodes for later reselection.  The namedSelection viewer provides a JTree in CytoPanel 1 that can be used to view all of the members of a named selection, and individually select those nodes, or the entire group.  The metaNodePlugin2 is a replacement for the original metaNode plugin that was developed by Iliana Avila-Compillo of the Institute for Systems Biology.  metaNodePlugin2 uses the new CyGroup mechanism to implement a simple expand/contract view of groups.  Finally, the groupTool plugin provides an interface to the underlying CyGroups, including providing the ability to switch the viewer associated with a group (or assign a viewer if one does not exist) and select the nodes, internal edges, or external edges of a group.  The groupTool plugin is meant primarily as a tool for developers and advanced users.  </p></td>
 </tr>
 <tr>
  <td class="subheader"><p>BatchTool</p></td>
 </tr>
 <tr>
  <td class="subheader"><p>Description</p></td>
 </tr>
 <tr>
  <td><p>
The batchTool plugin implements a rudimentary command language that may be used to automate some of the repetitive Cytoscape tasks.  Currently, batchTool implements the following commands:
<br>* apply mapName - applies the VizMap mapName to the current network
<br>* exit - exits Cytoscape
<br>* export network as [xgmml|gml|sif|psi-mi|psi-mi-1|pdf|svg|gif|png|jpg] to filename - exports the network in the indicated format to filename.
<br>* export [edge attributes|node attributes] to filename - exports the appropriate attributes to an attributes file
<br>* import [network|edge attributes|node attributes] filename - import the requested network or attributes file
<br>* layout algorithm setting1=value1 setting2=value2... - lay the current network out using algorithm and with the provided settings.  Note that only Cytoscape algorithms are supported (sorry, no Organic). Supported algorithms are: force-directed, grid, attributes-layout, hierarchical, circle, attribute-circle, degree-circle, isom, fruchterman-rheingold, and kamada-kawai.
<br>* open session - open the session named session
<br>* save [as filename] - save the session
Commands may be placed in a text file and run from the Cytoscape command line: cytoscape.sh -S my_batch_file
 </tr>
 </table>
 <br><a href="#table_top">Back to table</a>
 
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

Cytoscape jActiveModules plugin
------------------------------

I.  Introduction
================
This PlugIn enables Cytoscape to search for significant networks.


II.  Installation Instructions
==============================

To use the jActiveModules plugin, you must first obtain a copy of Cytoscape, 
Version 2.0.  You can download a copy from:  http://www.cytsoscape.org.  
Once you have downloaded Cytoscape and verified that it works, proceed with 
the next steps.  

1.  Download the jActiveModules.jar file from www.cytoscape.org/plugins2.php

2.  Place this jar file in your cytoscape plugins directory.

III.  Using the jActiveModules plugin
=============================

In model organisms such as yeast, large databases of protein-protein
and protein-DNA interactions have become an extremely important
resource for the study of protein function, evolution, and gene
regulatory dynamics. By integrating these interactions with
widely-available gene expression data, it is possible to generate
concrete hypotheses for the underlying mechanisms governing the
observed changes in gene expression.  The ActivePaths plugin is
designed to perform this integration systematically and at large
scale.  

The plugin works by filtering the molecular interaction network to
reveal active subnetworks, i.e., connected regions of the network that
show significant changes in expression over particular subsets of
conditions. It includes a rigorous statistical measure for scoring
subnetworks against expression data, along with a search algorithm for
identifying subnetworks with high score.  In this way, the activePaths
plugin is one example of how large-scale genomic approaches may be
used to uncover signaling and regulatory pathways in a systematic,
integrative fashion.

This plug-in implements methods described in the publication:
Trey Ideker, Owen Ozier, Benno Schwikowski, and Andrew F. Siegel,
Discovering regulatory and signaling circuits in molecular interaction
networks, ISMB2002 and Bioinformatics (in press).

DATA PREPARATION:
In order to run jActiveModules, expression data must be provided by user.
In addition, this expression data MUST supply p values. Note that these values must be between 0 and 1 (exclusive). (see the cytoscape manual for instructions on supplying p values with expression data). If thevsupplied p values are very small (ie, 0), jActiveModules will print a warning to standard error and round these values up.

OPERATION:
First, load a network (gml or sif) and associated expression data from the "File" menu. Note that expression data must be loaded in the form of an "Expression Matrix File" and not "Node Attributes". Next, specify the search and scoring parameters. From the menu bar, select "Plugins=>Active Modules: Set Parameters". This will bring up a dialog that allows you to choose from several different options.

"Number of Paths", "Iterations":
Starting from the specified "Number of Paths" (randomly seeded), the
algorithm anneals over the specified number of "Iterations".

"Start Temp" and "End Temp"
The annealing algorithm runs according to a decaying temperature
schedule; a typical starting temperature is around 1.0 or 2.0;
a typical ending temperature is around 0.01 or 0.001.

Annealing Extensions

"Quenching" is a final check that can run after annealing to ensure
  that the algorithm has found a local maximum.  It checks all
  one-node changes to the graph to see that none of them has a higher
  score than the current configuration.  If there is a better
  configuration, it takes that change, and quenches again iteratively
  until a local maximum is reached.


"Hubfinding" and "Regional Scoring" are two ways to
  counteract a topology problem in networks with high-degree nodes.
  "Hubfinding" turns off all low-scoring neighbors of hubs, so that
  the region around a hub will have a chance to be optimized.  The
  threshold for what defines a hub is set by the user.
   "Regional Scoring" is the most elegant solution of the three:
  instead of penalizing the hubs in some way, regional scoring
  restricts anneling to score "regions" of components.  For example:
  in a star-shaped network with hub H and leaf nodes A,B,C,D,E all
  connected to H, traditional annealing could find network A-H-C,
  among many others.  "Regional" annealing could not, though, because
  the region of A-H-C includes B, D, and E as well.  Thus in normal
  annealing, this network would have 6 one-node subnets, 5 two-node
  subnets, (5 choose 2) = 10 three-node subnets, (5 choose 3) = 10
  four-node subnets, (5 choose 4) = 5 five-node subnets, and one
  six-node subnet.  In contrast, regional annealing only permits
  5 two-node regions (the regions of the leaf nodes) and 1 six-node
  region (the region of the hub).

  If you use regional scoring, the algorithm will return only the
  "core" of the region.  To see the entire region, you must first
  show all nodes, select the subnet of interest, then press Ctrl-F
  to select the region surrounding it.

Seed Graph Options

"Non-Random Starting Graph" versus "Random Based on Current Time":
  During annealing, an integer is used to seed a pseudo-random number
  generator.  To reproduce an annealing run exactly, type the same
  number into the "seed:" text area.  To pick a random number, click
  the "Random Based on Current Time" button, and Cytoscape will put
  a numeric representation of the current time into the "seed:" area.

Search Extensions:
  Instead of annealing, this will greedily search for local optima in the graph, starting from all nodes in the graph.  The depth specifies the number of moves required to consider an area a local maximum.  The greater the depth, the better the optima, and the longer the algorithm takes.  Typical depths are 1 or 2.  Searching (rather than annealing) will return the set of all optima it found, in the order that it found them.  They may overlap.

"Search from selected nodes?"
  If you have a large graph, and the number of pairs of nodes (and thus starting points for searching) is quite large, you might consider selecting nodes that are likely to be near local optima, and starting the greedy search from there.


Once options are set, press "Dismiss" and the options will be saved. Before starting the search, make sure that the appropriate network is selected (it will be high-lighted in blue in the left panel). To begin active path finding, select "Plugins=>Active Modules: Find Modules" from the menu-bar. At the termination of the search, a matrix appears tabulating which experimental conditions (rows) activated each path (columns). Clicking on a column of this matrix selects the corresponding pathway in the main Cytoscape window (note that the nodes are selected even if the view is not currently displayed). In order to view these results, choose "Select=>To New Window=>Selected Nodes, All Edges" from the menubar.

The ActivePaths plug-in may also be controlled from the command line
by specifying the following options:

    --APt0 <double>         initial temperature
    --APtf <double>         final temperature
    --APni <integer>        number of iterations
    --APnp <integer>        number of paths
    --APhs <integer>        minimum hub size
    --APrs <integer>        random seed
    --APsd <integer>        search depth
    --APqu <true|false>     apply quenching
    --APmcb <true|false>    use mc correction at all
    --APmc <string>         monte carlo file name
    --APreg <true|false>    regional scoring true/false
    --APexit                exit after run
    --APhelp                prints this help


These options are used to automatically start ActivePaths once
Cytoscape is running.  It requires that a network file and expression
data are also specified from the command line.

FOR EXAMPLE: 
Load the sample network (sampleData/galFiltered.gml) and expression data
set (sampleData/galExpData.pvals) as described in the preceding
sections.  Choose PlugIns -> Find Active Paths and enter the following
values as options:

   Iterations ............... 10000
   Number of Paths .......... 5
   Start Temp ............... 1
   Final Temp ............... 0.01
   Display Interval ......... 10
   Quenching ................ (on)
   Hubfinding ............... (off)
   Regional scoring ......... (off)
   Rather than annealing... . (off)
   Search from selected... .. (off)
   Seed Graph ............... Non-Random

Press "Apply" and then "Generate" in the next dialog.  Annealing will
begin after a few seconds.  Although the run is nondeterministic,
annealing will typically produce a high scoring subnetwork of 35 nodes
and score 7 that is active in 14 out of the 20 experimental conditions
(indicated by red blocks in the matrix).  After the run, perform
layout on the resulting subnetworks by issuing the menu command
"Layout -> Organic" followed by "Whole graph".

Choosing "PlugIns -> Score Distribution" is an option to create a null
distribution of top scoring network results. When this option is selected, the
user first selects a set of search parameters using the previous dialog. In
addition, the user is prompted for a number of iterations and an output
filename. When this option is selected, the plugin will run the search on
randomized expression data "iterations" number of times. The score of each top
scoring result will be stored in the named output file. This information can
be used to generate a p value for the significance of high scoring networks.
Note that this process may take a very long time, depending on the length of
an individual search.

V.  Bugs / Feature Requests
============================

If you encounter a bug with this PlugIn, or have a feature suggestion, we  
encourage you to use the Cytoscape Bug Tracker:
http://www.cbio.mskcc.org/cytoscape/bugs/.  

If you log a bug, we will automatically email you when the bug is resolved.

VI.  Contacts
=============

Ideker Lab, Department of Bioengineering
University of California at San Diego
http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/

For any questions concerning this PlugIn, please contact:

Ryan Kelley:  rmkelley@ucsd.edu

This software is made available under the LGPL (Lesser General Public License).  
Until we develop a better source code distribution system, please email Ryan
for a complete copy of the source code.
 
This product was developed using software supplied by the Apache Software Foundation
(http://www.apache.org).

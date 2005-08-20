Cytoscape ExpressionCorrelation Plugin
---------------------------------------

I.  Introduction
=================
This directory contains a copy of the Cytoscape
ExpressionCorrelation Plugin.

The plugin computes a similarity network from either the
genes or conditions in an expression matrix.  Nodes in a
similarity network represent genes or conditions.  Links
represent similarity between vectors of the expression
levels of genes across all given conditions (gene
correlation network) or the similarity between vectors of
the expression levels of all genes in a single condition
(condition correlation network).  The plugin allows the user
to select an Expression Matrix of micro-array data directly
from Cytoscape and convert it to a visible interaction
Network in Cytoscape. The Similarity Matrix is computed
using the Pearson Correlation Coefficient. A histogram tool
is available for choosing a similarity strength threshold,
in order to ease creation of a reasonably sized network. No
statistical significance is currently implemented for the
similarity network.

II. About
=========
  
ExpressionCorrelation is freely available and open-source
molecular profile visualization software. Future directions
include using weights in the correlation calculations in
order to reduce data noise and downweight multiple
Affymetrix probe set IDs.  Also, other similarity metrics
will be considered and a statistical significance score for
similarity links.

Gene expression data (loaded in via Cytoscape) can be used
to create a gene or a condition correlation matrix. Any
correlation above or below given threshold values, is
displayed in Cytoscape as an 'edge' between two 'nodes' (the
nodes are the two genes or conditions that are correlated).
However, a correlation matrix can be very large, and often
cannot be stored in memory, so this program saves only the
relevant correlations as they are calculated. Calculation of
the correlation matrix is relatively fast.

One problem with this is that the cutoff values cannot be
lowered without recalculating the entire correlation matrix
(they could be raised but a method to do this is not
implemented here, instead to ignore low threshold values
Cytoscape can be set up to not display them). In addition to
losing the values below the threshold, another problem is
that Cytoscape begins to have trouble displaying networks
above several thousand edges. This means that good cutoff
values must be chosen before the network is created: good
cutoff values display as much of the network as possible
without causing problems with CPU memory or creating
cluttered networks.

To help users choose a good cutoff value, we added a
histogram feature, which shows the number of edges
associated with particular cutoff values and vice-versa. To
get the histogram, the correlation matrix must be calculated
(so it will be calculated once for the histogram and once
for the network creation), which could cause the entire
process to take up to twice as long. The process will be
twice as long if the matrix calculation is the time limiting
process, which is usually the case when networks contain a
few thousand edges or less. However, the edge/node creation
process quickly becomes the time limiting process when more
than a few thousand edges are created (in this case it could
take 100 times longer rather than just twice as long).

It is recommended that if the distribution of the
correlation values is not know by the user, then the
histogram should be used to limit the networks to a few
thousand edges.

ExpressionCorrelation is currently available at:
http://www.cytoscape.org/plugins2.php

III. Plugin Installation Instructions
=====================================

To use the ExpressionCorrelation Plugin, the user must first
obtain a copy of Cytoscape, Version 2.0.  The user can
download a copy from:  http://cytoscape.org/alpha.html.

(Important Note:  The user must be using Cytoscape 2.0.  The
ExpressionCorrelation Plugin does *not* work with earlier
versions of Cytoscape.)

Once the user has downloaded Cytoscape and verified that it
works, the user can install the ExpressionCorrelation
Plugin:

1.  Copy plugin/ ExpressionCorrelation.jar to the user
[Cytoscape_Home]/plugins directory.

The Plugin installation is now complete.


IV. Using the ExpressionCorrelation Plugin
==============================

To use the ExpressionCorrelation Plugin:

1.  Start Cytoscape.  For example, on Unix/Linux or MacOS X,
run:

cytoscape.sh

On Windows, run:

cytoscape.bat

2. From the Main Menu, Select "File" ---> "Load" --->
"Expression Matrix File."

3. From the Main Menu, Select "plugins" ---> "Correlate" --->

      3a. "Construct Similarity Network"
           This option will create the condition network and
the gene network simultaneously using the default cutoffs "-
0.95 & 0.95" or the user selected cutoffs from the previous
run of the ExpressionCorrelation Plugin, but will not create
the histogram of the data distribution. The two network file
name extensions along with the default cutoffs used will
appear in the top left frame of Cytoscape. If the network
has fewer than 500 similarity links, a view will be created
automatically and the network will appear in the right frame
of Cytoscape. Otherwise, a view will not be created. In this
case, to view the network: select the network by clicking on
its file name extension (it will turn green), and from the
Main Menu select
"Edit" ---> "Create View".

      3b. "Advanced Options"

1."Condition Network: Preview Histogram"
    This option will calculate and display the
histogram of the condition matrix expression data
distribution. In the histogram window the user can select
the low and high cutoffs by manually typing them into the
appropriate "Cutoff" text boxes. The user can choose to use
only one set of cutoffs by deselecting the "low" or "high"
checkbox. The user can select the number or percent of
interactions to be displayed, rather than selecting cutoffs,
by typing the number into the "Enter" text box and choosing
"Number of Interactions" or "Percent of Interactions".
Select "OK" to create the Condition Network using the
parameters specified. The parameters specified will be saved
for the duration of the Cytoscape session.

2."Condition Network: Using Defaults"
    This option will create the condition network
using the default cutoffs or the user selected cutoffs from
the previous execution of the Correlate Plugin in this
Cytoscape session.

3."Gene Network: Preview Histogram"
    This option will calculate and display the
histogram of the gene matrix expression data distribution
and create the gene network according to the parameters
specified by the user.

4."Gene Network: Using Defaults"
    This option will create the gene network using
the default cutoffs or the user selected cutoffs from the
previous run of the Correlate Plugin in this Cytoscape
session.


V. Biological Relevance
=======================

      The Correlate Plugin allows for comparison of multiple
networks of similarity relationships between genes that are
derived from different subsets of conditions. It may be used
to define modules (sets of genes - network nodes - in the
simplest form) that can differentiate between stages or
types of cancer. The differences between the networks can be
computed using an already existing Cytoscape Plugin Diff.

VI. Sample Data
===============
Sample data containing 300 expression experiments from the
Rosetta yeast compendium is packaged with the plugin.


VII. Bugs / Feature Requests
============================

Please use the contact details below to report bugs.

VI. Contacts
=============

Sander Group, Computational Biology Center
Memorial Sloan-Kettering Cancer Center, New York City
http://www.cbio.mskcc.org/

For any questions concerning this Plugin, please contact:

Gary Bader:  baderg AT mskcc.org
Elena Potylitsine: elena AT cbio.mskcc.org
Weston Whitakaer: weston AT cbio.mskcc.org

This software is made available under the LGPL (Lesser
General Public License), which means that you can freely use
it within your own software, but if you alter the code
itself and distribute it, you must make the source code
alterations freely available as well.

This product includes jmathplot developed by the Yann Richet
(http://jmathplot.sourceforge.net/).


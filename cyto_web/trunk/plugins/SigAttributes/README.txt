Cytoscape SigAttributes plugin
------------------------------

I.  Introduction
================
This PlugIn enables Cytoscape to search for aggregation of attribute values in subnetworks.

II.  Installation Instructions
==============================

To use the SigAttributes plugin, you must first obtain a copy of Cytoscape, 
Version 2.0.  You can download a copy from:  http://www.cytsoscape.org.  
Once you have downloaded Cytoscape and verified that it works, proceed with 
the next steps.  

1.  Download the SigAttributes.jar file from www.cytoscape.org/plugins2.php

2.  Place this jar file in your cytoscape plugins directory.

III.  Using the SigAttributes plugin
=============================
With the Cytoscape annotation server and other categorical node attributes, it becomes convenient to look for aggregation of attribute terms in a specified subnetwork.  Running SigAttributes prints the attribute terms that are enriched and creates a node attribute "Significant Attributes" where it puts the enriched values.  

DATA PREPARATION:
In orde to run SigAttributes, categorical data or an annotation server (GO, KEGG, etc.) must be provided by the user.  See the Cytoscape manual for setup instructions.  

OERATION:
First, load a network (gml or sif).  Then either set up an annotation server or load other categorical node attributes.  Select the nodes you are interested in.  Choose Plugins => Find Enriched Attributes to open the options menu.  There are a few options to specify. 

"By Attribute" "By Annotation"
This lets you select whether you want to use an annotation server or a node attributes.  

"Select Node Attribute" or "Select Annotation"
Select the Node Attribute you wish to check for enrichment.  
OR
Select the annotation server to use (branches of GO or KEGG).

"Pval Cutoff"
The maximum significance level to consider.  Default is 0.001.

"Max # Attributes"
The maximum number of attributes to consider.  If more are found, only the top X are chosen, based on p-value.  

"Attributes for Name Lookup"
Select the attribute that contains the name of the protein.  Usually 'Canonical Name'.  

Treat selected Nodes as "single group" or "Separate Groups"
Leave this as "single group" to treat look for attribute aggregation within the selected nodes relative to the entire protein set.  

Select OK.  

The program will print the number of terms found in the annotation server, the number of terms found in the selected group and a list of terms that are significantly enriched (if any are found).  Each selected node will be given a new attribute called "Significant Attributes" which lists the enriched terms.  


INNER WORKINGS
SigAttributes uses a null model of uniform distribution of terms for an attribute.  The number of times a term could be expected in a sample of network follows a hypergeometric distribution.  If the actual number of nodes matching the term exceeds the set p-value threshold, this term is reported.  
When using the annotation server and ontologies, an additional correction is used to take into account the hierarchical structure of terms.  Instead of reporting absolute aggregation of terms, the aggregation is calculated relative to the aggregation of the parent term.  This is to prevent the inclusion of an entire "branch" of the ontology as significant.  Usually only one or two levels along any branch will be found.  
A simple Bonferroni correction is used to correct for multiple hypothesis testing.  

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

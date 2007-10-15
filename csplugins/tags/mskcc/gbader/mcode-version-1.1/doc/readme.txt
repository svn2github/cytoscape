MCODE PlugIn v1.1
-----------------

I.  Introduction
================
This directory contains a copy of the MCODE PlugIn.

This PlugIn enables Cytoscape to cluster a network.

The current release includes support for basic clustering of the network.  Directed mode is
currently not implemented, but will be in MCODE v2.0.

II.  Installation Instructions
==============================

To use the MCODE PlugIn, you must first obtain a copy of Cytoscape,
Version 2.1.  You can download a copy from:  http://www.cytsoscape.org.
Once you have downloaded Cytoscape and verified that it works, proceed with
the next steps.

1.  Copy the MCODE.jar file to your [Cytoscape_Home]/plugins directory.

2.  Start Cytoscape.  For example, on Unix/Linux or MacOS X, run:

cytoscape.sh

On Windows, run:

cytoscape.bat

You may also load the plugin via the Plugin menu in Cytoscape.

III.  Using the PlugIn
======================

Once Cytoscape starts, load your network following the Cytoscape instruction manual.
Under the PlugIns->MCODE menu, set the parameters for MCODE and then select the menu
option to run MCODE.  If you want to run MCODE with different parameters again on the
same network, you should load the network up again and run MCODE with the new parameters.
You can also force MCODE to run again using the Advanced submenu.

IV.  Bugs / Feature Requests
============================

If you encounter a bug with this PlugIn, or have a feature suggestion, please
e-mail Gary Bader (details below).  Please look at TODO.txt before e-mailing a
feature request.

This software is open-source.  If you modify the source code and would like the
modification to be included in a subsequent release, e-mail a pre-built JAR file
for evaluation of the feature.  If the feature is deemed suitable for inclusion,
a source code patch will be requested.

V.  Contacts
=============

Sander Group, Computational Biology Center
Memorial Sloan-Kettering Cancer Center, New York City
http://www.cbio.mskcc.org/

For any questions concerning this PlugIn, please contact:

Gary Bader:  baderg AT mskcc.org

This software is made available under the LGPL (Lesser General Public License).

Please see the following paper for more information:

Bader GD, Hogue CW.

An automated method for finding molecular complexes in large protein interaction networks.
BMC Bioinformatics. 2003 Jan 13;4(1):2.
http://www.biomedcentral.com/1471-2105/4/2

http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&list_uids=12525261&dopt=Abstract

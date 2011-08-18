Cytoscape cPath PlugIn
--------------------------------

Table of Contents
=================
1.   Introduction
2.   About cPath
3.   PlugIn Installation Instructions
4.   Using the cPath PlugIn
5.   Configuring the cPath PlugIn
6.   Bugs / Feature Requests
7.   Contacts
8.   Release Notes

1.  Introduction
=======================================
This directory contains a copy of the Cytoscape cPath PlugIn.

The cPath PlugIn enables Cytoscape users to query, retrieve and visualize
protein-protein interactions stored in the cPath database.

2.   About cPath
=======================================

cPath aims to be a freely available cancer pathway database. Currently, only
information about protein-protein interactions collected from major interaction
databases that support the PSI-MI format is available. cPath is open-source
and is easy to locally install for private management of protein-protein
interactions. Future directions include support for the BioPAX format so that
entire pathways can be stored, queried and presented.

cPath is currently available at:  http://www.cbio.mskcc.org/cpath

3.  PlugIn Installation Instructions
=======================================

To use the cPath Plugin, you must first obtain a copy of Cytoscape, 
Version 2.0.  You can download a copy from:  http://cytoscape.org/.

(Important Note:  You must be using Cytoscape 2.0.  The cPath PlugIn does 
*not* work with earlier versions of Cytoscape.)

Once you have downloaded Cytoscape and verified that it works, install the 
cPath PlugIn:

1.  Copy all the .jar files in the /plugin directory to your
[Cytoscape_Home]/plugins directory.

2.  Restart Cytoscape from the command line.  For example, run:
    cytoscape.sh or cytoscape.bat.

-------------------------------------------------------------------------------
| Warning:  The cPath PlugIn *cannot* be loaded dynamically from Cytoscape    |
| via the PlugIns menu.  It can only be loaded by following the instructions  |
| above.                                                                      |
-------------------------------------------------------------------------------


4.  Using the cPath PlugIn
=======================================

To use the cPath PlugIn:

1.  Start Cytoscape.  For example, on Unix/Linux or MacOS X, run:

cytoscape.sh

On Windows, run:

cytoscape.bat

2.  From the Main Menu, Select "PlugIns" --> "Search cPath..."

2.  A new cPath query window will appear.  Enter your search terms, and click
the "Search" button.

Your search results will be automatically downloaded and displayed in the
main Cytoscape desktop window.

5.  Configuring the cPath PlugIn
=======================================
If you have a local installation of cPath, and want to point your plugin
to this local installation, you can do so my modifying the central Cytoscape
properties file:  cytoscape.props.

The property name is:  dataservice.cpath_read_location.

For example, the following line points the cPath PlugIn to a local copy
of cPath

dataservice.cpath_read_location=http://localhost:8080/cpath/webservice.do

6.  Bugs / Feature Requests
=======================================

If you encounter a bug with this plugin, or have a feature suggestion, we
encourage you to use the Cytoscape Bug Tracker:
http://www.cbio.mskcc.org/cytoscape/bugs/.  

If you log a bug, we will automatically email you when the bug is resolved.

Updates regarding this plugin will be posted to the Cytoscape-announce
mailing list.  You can subscribe to the mailing list or browse the archives
at:  http://groups-beta.google.com/group/cytoscape-announce.

7.  Contacts
=======================================

Sander Group, Computational Biology Center
Memorial Sloan-Kettering Cancer Center, New York City
http://www.cbio.mskcc.org/

For any questions concerning this plugin, please contact:

Gary Bader:  baderg AT mskcc.org
Ethan Cerami:  cerami AT cbio.mskcc.org

This software is made available under the LGPL (Lesser General Public License).  

This product includes software developed by the Apache Software Foundation
(http://www.apache.org).

VIII.  Release Notes
=======================================

Beta1:  August, 2004
    -- Initial Release of plugin.

Beta2:  October, 2004
    -- PlugIn  now supports retrieval of arbitrarily large data sets.  For
       example, you can now download all human records from cPath.  This
       functionality is provided by connecting to the newly revised
       cPath XML Web Services API.
    -- PlugIn refactored to use the new csplugins Task Framework, and common
       Progress Bar component.  Progress of data retrieval and time remaining
       estimates are therefore presented to the end user.  Users can now also
       cancel long-running queries.
    -- Because very large data sets can now be downloaded, the TreeView
       for viewing / navigating Interactions/Interactors is no longer practical.
       The TreeView is therefore no longer available.
    -- In place of the TreeView, users can now select nodes/edges in the main
       Cytoscape window and view full Interaction/Interactor details.
    -- If number of interactors < the Cytoscape View Threshold (default=500), a
       Cytoscape view is automatically created.  Otherwise, a view is not
       automatically created.
    -- PlugIn Console now provides more informative log messages to the user.
       For example, it reports progress of each call to cPath, and reports
       status of and warnings associated with mapping of cPath data to
       Cytoscape.
    -- Quick Reference Manual is updated and now available via a more prominent
       Help button, instead of via the cPath PlugIn Menu.
    -- About Dialog box is now available via a more prominent About button,
       instead of via the cPath PlugIn Menu.
    -- Added details regarding configuration of cPath PlugIn, so that
       users can point to local installations of cPath (see README.txt file).
       This feature was available in beta1, but was undocumented.
    -- Bug Fix:  Beta1 PlugIn attempted to map interactions to Cytoscape Edges
       via a matrix view (by default.)  However, if the number of interactors is
       large, this can result in a huge number of edges
       (where # of edges = n(n+1) / 2).  The Beta2 PlugIn now only maps
       interactions with 5 or fewer interactors.  Those interactions with more
       than 5 interactors are reported to the end user as warnings.

Beta 3:  June, 2006
    -- Updated to Work with Cytoscape 2.3 (remains backward compatible to earlier
       Cytoscape versions).



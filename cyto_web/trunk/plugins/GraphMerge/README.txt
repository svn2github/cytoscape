Cytoscape GraphMerge plugin
---------------------------

I. Introduction
===============

This Cytoscape plugin glues two or more network together.  For example,
suppose I load a network A into Cytoscape.  Suppose I create subnetworks
B and C, both subnetworks of my original network A.  Using the GraphMerge
plugin, I am able to create a network D which is the union of networks B and C.


II. Installation Instructions
=============================

To use the GraphMerge plugin. simply drop the file GraphMerge.jar into
your Cytoscape plugins folder (which will be, for example,
'cytoscape-v2.1/plugins/').


III. Using the GraphMerge Plugin
================================

This plugin is started by selecting "Plugins" -> "Merge all networks" from
the Cytoscape menu system.  The plugin will only be functional when two or
more networks are defined in the Cytoscape Desktop.

Once activated, this plugin will pop up a "Merge Networks" window.  From
the "Available Networks" panel on the left of this window, select one network.
Click the button containing the arrow pointing right.  Then, select another
network from the panel on the left of the popup window.  Again, press the
same button containing the arrow pointing right.  Now, The "OK" button should
become active.  When pressed, the "OK" button will trigger the creation of
a merged network, which will be the union of all networks displayed in the
right panel in the "Merge Networks" popup window.


IV. Bugs / Feature Requests
===========================

If you encounter a bug with this plugin we encourage you to use the
Cytoscape Bug Tracker:
http://www.cbio.mskcc.org/cytoscape/bugs/ .


V. Contacts
===========

Ideker Lab, Department of Bioengineering
University of California at San Diego
http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/

For any questions concerning this plugin, please contact, via email:

Ryan Kelley:  rmkelley <at> ucsd.edu

This software is made available under the LGPL.

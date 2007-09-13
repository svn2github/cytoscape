
Dynamic Expression Plug-in

I. Introduction

This plug-in loads an expression data file (consult the Cytoscape manual to learn
about the format of this type of file) and then allows the user to color the nodes
in a network according to their expression values. The GUI works like a VCR, with
play, pause, and stop buttons. If the user presses the play button, the plug-in will
iterate over all the conditions in the expression file and color the nodes according
to their corresponding expression values.

II. Installation

Simply copy the file dynxpr.jar into your Cytoscape plugins folder (which will be,
 for example, 'cytoscape-v2.1/plugins/'). Tested for Cytoscape 2.1.

III. Using the plug-in

The plug-in starts up with default minimum and maximum expression values: -1 and 1.
It colors nodes with a color ranging from blue to red, depending on the value
of their expression (blue hue if close to -1, red hue if close to 1).

To change the colors or the minimum and maximum expression values, press the icon
that looks like a multi-colored pie on the Cytoscape tool-bar (this is the Visual 
Styles dialog). Then edit the current visual style. Look for the "dynamicXpr" Node
Color mapping, and you should be able to reset colors or add/remove points in the
continuous range of values. After making changes, you can run the plug-in, and you
will see the effects of your new settings.

Additionally, the plug-in assigns to each node at each condition a "significance"
attribute (if present in the loaded expression file). So, if you wish to map this
attribute to a visual node attribute, you can do so by creating a node visual
calculator (again, using the Visual Styles dialog) and then running the plug-in.
For example, some people like to map the significance to the node border thickness.

IV. Contact

Scooter Morris at scooter <at> cgl.ucsf.edu

V. Authors
This plugin was originally developed by Iliana Avila-Campillo from the
Institute for Systems Biology.  It has been updated and is now maintained by
Scooter Morris at the Resource for Biocomputing, Visualization, and
Informatics at UCSF.

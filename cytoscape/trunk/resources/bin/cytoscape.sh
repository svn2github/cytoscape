#!/bin/sh
#
# Run cytoscape from a jar file
# this is a linux-only version
#-------------------------------------------------------------------------------

#export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./plugins

java -Dswing.aatext=true -Dawt.useSystemAAFontSettings=lcd -Xss5M -Xmx1512M -jar cytoscape.jar cytoscape.CyMain -p plugins "$@"


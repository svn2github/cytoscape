#!/bin/sh
#
# Run cytoscape from a jar file
# this is a linux-only version
#-------------------------------------------------------------------------------

#export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./plugins

java -d64 -Dswing.aatext=true -Dawt.useSystemAAFontSettings=lcd -Xss5M -Xmx512M -jar cytoscape.jar cytoscape.launcher.CytoscapeLauncher -p plugins "$@"


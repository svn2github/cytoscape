#!/bin/sh
#
# Run cytoscape from a jar file
# this is a UNIX-only version
#-------------------------------------------------------------------------------

# Attempt to generate cytoscape.vmoptions if it doesn't exist!
if [ ! -e cytoscape.vmoptions  -a  -x ./gen_vmoptions.sh ]; then
    ./gen_vmoptions.sh
fi

if [ -r cytoscape.vmoptions ]; then
    java -d64 -Dswing.aatext=true -Dawt.useSystemAAFontSettings=lcd `cat cytoscape.vmoptions` -cp cytoscape.jar cytoscape.CyMain "$@"
else # Just use sensible defaults.
    java -d64 -Dswing.aatext=true -Dawt.useSystemAAFontSettings=lcd -Xss100M -Xmx1550M -cp cytoscape.jar cytoscape.CyMain "$@"
fi


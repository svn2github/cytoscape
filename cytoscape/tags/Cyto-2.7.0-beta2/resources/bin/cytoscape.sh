#!/bin/sh
#
# Run cytoscape from a jar file
# this is a UNIX-only version
#-------------------------------------------------------------------------------

script_path=$(dirname -- $0)

# Attempt to generate cytoscape.vmoptions if it doesn't exist!
if [ ! -e cytoscape.vmoptions  -a  -x $script_path/gen_vmoptions.sh ]; then
    $script_path/gen_vmoptions.sh
fi

if [ -r cytoscape.vmoptions ]; then
    java `cat cytoscape.vmoptions` -jar $script_path/cytoscape.jar -p $script_path/plugins "$@"
else # Just use sensible defaults.
    java -d64 -Dswing.aatext=true -Dawt.useSystemAAFontSettings=lcd -Xss10M -Xmx1550M \
	-jar $script_path/cytoscape.jar -p $script_path/plugins "$@"
fi


#!/bin/sh
#
# Run cytoscape from a jar file
# This script is a UNIX-only (i.e. Linux, Mac OS, etc.) version
#-------------------------------------------------------------------------------

script_path="$(dirname -- $0)"

# Attempt to generate Cytoscape.vmoptions if it doesn't exist!
if [ ! -e "$HOME/.cytoscape/Cytoscape.vmoptions"  -a  -x "$script_path/gen_vmoptions.sh" ]; then
    "$script_path/gen_vmoptions.sh"
fi

if [ -r Cytoscape.vmoptions ]; then
    java `cat "$HOME/.cytoscape/Cytoscape.vmoptions"` -jar "$script_path/cytoscape.jar" -p "$script_path/plugins" "$@"
else # Just use sensible defaults.
    java -d64 -Dswing.aatext=true -Dawt.useSystemAAFontSettings=lcd -Xss10M -Xmx1550M \
	-jar "$script_path/cytoscape.jar" -p "$script_path/plugins" "$@"
fi


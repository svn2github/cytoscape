#!/bin/sh
# Generates the Cytoscape.vmoptions file

dot_cytoscape_path="$HOME/.cytoscape"

if [ ! -e $dot_cytoscape_path ]; then
    /bin/mkdir $dot_cytoscape_path
fi

if `java -version 2>&1 | grep -- 64-Bit > /dev/null`; then # We have a 64 bit JVM.
    echo -Xms20m >  "$dot_cytoscape_path/Cytoscape.vmoptions"
    echo -Xmx20g >> "$dot_cytoscape_path/Cytoscape.vmoptions"
    echo -d64       >> "$dot_cytoscape_path/Cytoscape.vmoptions"
else # Assume a 32 bit JVM.
    echo -Xms10m   >  "$dot_cytoscape_path/Cytoscape.vmoptions"
    echo -Xmx1550m >> "$dot_cytoscape_path/Cytoscape.vmoptions"
fi

# Shared JVM options
echo -Dswing.aatext=true               >> "$dot_cytoscape_path/Cytoscape.vmoptions"
echo -Dawt.useSystemAAFontSettings=lcd >> "$dot_cytoscape_path/Cytoscape.vmoptions"

exit 0

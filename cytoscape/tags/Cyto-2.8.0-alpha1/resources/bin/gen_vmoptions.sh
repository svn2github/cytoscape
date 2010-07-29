#!/bin/sh
# Generates the Cytoscape.vmoptions file
if `java -version 2>&1 | grep -- 64-Bit > /dev/null`; then # We have a 64 bit JVM.
    echo -Xms20m >  Cytoscape.vmoptions
    echo -Xmx20g >> Cytoscape.vmoptions
    echo -d64       >> Cytoscape.vmoptions
else # Assume a 32 bit JVM.
    echo -Xms10m   >  Cytoscape.vmoptions
    echo -Xmx1550m >> Cytoscape.vmoptions
fi

# Shared JVM options
echo -Dswing.aatext=true               >> Cytoscape.vmoptions
echo -Dawt.useSystemAAFontSettings=lcd >> Cytoscape.vmoptions

exit 0

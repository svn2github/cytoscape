#!/bin/sh
# Generates the cytoscape.vmoptions file
if `java -version 2>&1 | grep -- 64-Bit > /dev/null`; then # We have a 64 bit JVM.
    echo -Xms20m    >  cytoscape.vmoptions
    echo -Xmx20000m >> cytoscape.vmoptions
else # Assume a 32 bit JVM.
    echo -Xms10m   >  cytoscape.vmoptions
    echo -Xmx1550m >> cytoscape.vmoptions
fi

# Shared JVM options
echo -d64                              >> cytoscape.vmoptions
echo -Dswing.aatext=true               >> cytoscape.vmoptions
echo -Dawt.useSystemAAFontSettings=lcd >> cytoscape.vmoptions

exit 0

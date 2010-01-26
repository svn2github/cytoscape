#!/bin/sh
# Generates the cytoscape.vmoptions file
if `java -version 2>&1 | grep -- 64-Bit > /dev/null`; then
    echo -Xms20m > cytoscape.vmoptions
    echo -Xmx20000m >> cytoscape.vmoptions
else # Assume a 32 bit JVM.
    echo -Xms10m > cytoscape.vmoptions
    echo -Xmx1550m >> cytoscape.vmoptions
fi

exit 0

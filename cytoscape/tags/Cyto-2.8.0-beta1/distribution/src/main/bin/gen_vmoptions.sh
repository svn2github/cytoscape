#!/bin/sh
# Generates the Cytoscape.vmoptions file

script_path="$(dirname -- $0)"

if `java -version 2>&1 | grep -- 64-Bit > /dev/null`; then # We have a 64 bit JVM.
    echo -Xms20m >  "$script_path/Cytoscape.vmoptions"
    echo -Xmx20g >> "$script_path/Cytoscape.vmoptions"
    echo -d64       >> "$script_path/Cytoscape.vmoptions"
else # Assume a 32 bit JVM.
    echo -Xms10m   >  "$script_path/Cytoscape.vmoptions"
    echo -Xmx1550m >> "$script_path/Cytoscape.vmoptions"
fi

# Shared JVM options
echo -Dswing.aatext=true               >> "$script_path/Cytoscape.vmoptions"
echo -Dawt.useSystemAAFontSettings=lcd >> "$script_path/Cytoscape.vmoptions"

exit 0

#!/bin/sh
# Generates the Cytoscape.vmoptions file

#vm_options_path="$HOME/.cytoscape"
vm_options_path=.

if [ ! -e $vm_options_path ]; then
    /bin/mkdir $vm_options_path
fi

if `java -version 2>&1 | grep -- 64-Bit > /dev/null`; then # We have a 64 bit JVM.
    echo -Xms20m >  "$vm_options_path/Cytoscape.vmoptions"
    echo -Xmx20g >> "$vm_options_path/Cytoscape.vmoptions"
    echo -d64       >> "$vm_options_path/Cytoscape.vmoptions"
else # Assume a 32 bit JVM.
    echo -Xms10m   >  "$vm_options_path/Cytoscape.vmoptions"
    echo -Xmx1550m >> "$vm_options_path/Cytoscape.vmoptions"
fi

# Shared JVM options
echo -Dswing.aatext=true               >> "$vm_options_path/Cytoscape.vmoptions"
echo -Dawt.useSystemAAFontSettings=lcd >> "$vm_options_path/Cytoscape.vmoptions"

exit 0

#!/bin/sh
#
# Run cytoscape from a jar file
# this is a linux-only version 
#-------------------------------------------------------------------------------

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./plugins

java -Xmx512M -jar cytoscape.jar -y giny -b GO/annotationAndSynonyms --JLD plugins $*

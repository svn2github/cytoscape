#!/bin/sh
#
# Run cytoscape from a jar file
# this is a linux-only version 
#-------------------------------------------------------------------------------

#export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./plugins

java -Xmx512M -classpath cytoscape.jar:.:$CLASSPATH cytoscape.CyMain -y giny -b annotation/manifest --JLD plugins $*

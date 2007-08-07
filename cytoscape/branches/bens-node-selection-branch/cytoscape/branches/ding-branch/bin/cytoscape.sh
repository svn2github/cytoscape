#!/bin/sh
#
# Run cytoscape from a jar file
# this is a linux-only version
#-------------------------------------------------------------------------------

#export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./plugins

java -Xmx512M -jar cytoscape.jar -vp vizmap.props cytoscape.CyMain --JLD plugins $*

#Use this command line to use organism specific gene annotations by default
#java -Xmx512M -jar cytoscape.jar cytoscape.CyMain -b annotation/manifest --JLD plugins $*

#!/bin/sh

rm -rf $HOME/.cytoscape

java -jar ../../../../cytoscape/lib/cytoscape.jar --JLD ../../../../cytoscape/plugins --JLW ../sequence/build/sequence.jar --JLW build/getInteractions.jar -p ../data/cytoscape.project


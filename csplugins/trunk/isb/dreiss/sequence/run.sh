#!/bin/sh

rm -rf $HOME/.cytoscape

java -jar ../../../../cytoscape/lib/cytoscape.jar --JLD ../../../../cytoscape/plugins --JLW build/sequence.jar -p ../data/cytoscape.project


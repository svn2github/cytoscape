#!/bin/sh

rm -rf $HOME/.cytoscape

java -jar ../../../../cytoscape/lib/cytoscape.jar --JLD ../../../../cytoscape/plugins --JLW ../sequence/build/sequence.jar --JLW build/visualClustering.jar --JLW ../cytoTalk/build/cytoTalk.jar -p data/cytoscape.project


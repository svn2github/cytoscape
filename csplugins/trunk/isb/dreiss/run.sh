#!/bin/sh

rm -rf $HOME/.cytoscape

java -jar ../../../cytoscape/lib/cytoscape.jar --JLD ../../../cytoscape/plugins -p data/cytoscape.project


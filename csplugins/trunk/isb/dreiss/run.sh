#!/bin/sh

rm -rf $HOME/.cytoscape

java -jar ../../../cytoscape/lib/cytoscape.jar --JLD ../../../cytoscape/plugins --JLW build/csplugins.jar -p data/cytoscape.project


#!/bin/sh

rm -rf $HOME/.cytoscape

java -jar ../../../../cytoscape/lib/cytoscape.jar --JLD ../../../../cytoscape/plugins --JLW ../sequence/build/sequence.jar --JLW build/getInteractions.jar -i ../../../../cytoscape/testData/transcript.sif -s 'Saccharomyces cerevisiae'


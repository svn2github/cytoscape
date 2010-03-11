#!/bin/bash

base=/Users/mes/cellar/Data/cytoscape_performanc_releases

function runTest {
	echo Testing version: $1
	ant clean >> run.log
	ant -Dcytoscape.dir=$base/Cytoscape_$1 -Dcytoscape.version=$1 run  >> run.log
}

> run.log

runTest 2.5.0
runTest 2.5.1
runTest 2.5.2
runTest 2.6.0
runTest 2.6.1
runTest 2.6.2
runTest 2.6.3
runTest trunk 

java -cp track.cyperf.jar:ui.cyperf.jar cytoscape.performance.ui.HTMLResults *.perf

echo Open the file results.html to see the results!!!

#!/bin/bash

base=/cellar/users/mes/Cytoscape-performance

ant clean > run.log
ant -Dcytoscape.dir=$base/2.3.1/cytoscape -Dplugin.root.dir=$base/2.3.1/coreplugins -Dcytoscape.version=2.3.1 run > run.log

ant clean > run.log
ant -Dcytoscape.dir=$base/2.3.2/cytoscape -Dplugin.root.dir=$base/2.3.2/coreplugins -Dcytoscape.version=2.3.2 run > run.log

ant clean > run.log
ant -Dcytoscape.dir=$base/2.4.0/cytoscape -Dplugin.root.dir=$base/2.4.0/coreplugins -Dcytoscape.version=2.4.0 run > run.log

ant clean > run.log
ant -Dcytoscape.dir=$base/current/cytoscape -Dplugin.root.dir=$base/current/coreplugins -Dcytoscape.version=current run  > run.log

java -cp track.cyperf.jar:ui.cyperf.jar cytoscape.performance.ui.HTMLResults *.perf
java -cp track.cyperf.jar:ui.cyperf.jar cytoscape.performance.ui.AlignedResults *.perf

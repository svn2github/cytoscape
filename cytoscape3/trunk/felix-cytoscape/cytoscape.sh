#!/bin/bash

rm -rf cache

# the Xbootclasspath is needed to get the look and feel correct
# the felix config.properties tells felix where to look for the config props

java  -Xbootclasspath/a:bundle/com.jgoodies.looks_2.1.2.jar:bundle/cytoscape-sun.jhall_1.0.0.jar \
      -Dfelix.config.properties="file:conf/config.properties" \
	  -jar bin/felix.jar


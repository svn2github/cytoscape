#!/bin/bash

# the Xbootclasspath is needed to get the look and feel correct
# the felix config.properties tells felix where to look for the config props

java  -Xbootclasspath/a:bundle/looks-2.1.4.jar:bundle/jhall.jar \
      -Dfelix.config.properties="file:conf/config.properties" \
	  -jar bin/felix.jar


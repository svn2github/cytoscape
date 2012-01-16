#!/bin/bash

GROUP_ID=cytoscape-temp
VERSION=2.0-b23-20110303

for FILE in jogl_repacked/*.jar
do
	ARTIFACT_ID=`basename $FILE .jar`
	mvn install:install-file -Dfile=$FILE -DgroupId=$GROUP_ID -DartifactId=$ARTIFACT_ID -Dversion=$VERSION -Dpackaging=jar
done

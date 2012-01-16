#!/bin/bash

# build renderer
pushd paperwing-impl
mvn clean install
popd

# build patched parts of Cytoscape core
for i in presentation-impl swing-application-impl vizmap-gui-impl
do
	pushd $i
	mvn clean install
	popd
done

# build Cytoscape
pushd gui-distribution
mvn clean install
popd




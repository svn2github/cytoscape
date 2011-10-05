#!/bin/bash

FREEHEP_VERSION=2.1.3

mkdir build
pushd build

curl -OL https://github.com/freehep/freehep-vectorgraphics/tarball/vectorgraphics-${FREEHEP_VERSION} || exit

tar xvzf vectorgraphics-${FREEHEP_VERSION} || exit
pushd freehep-freehep-vectorgraphics-*

mvn clean package || exit

for JAR in */target/*.jar
do
    bnd wrap ${JAR}
done

for BAR in */target/*.bar
do
    ARTIFACT_ID=$(dirname $(dirname ${BAR}))
    mvn deploy:deploy-file -DgroupId=cytoscape-temp -DartifactId=${ARTIFACT_ID} -Dversion=${FREEHEP_VERSION} -Dpackaging=jar -Dfile=${BAR} -DrepositoryId=thirdparty -Durl=http://code.cytoscape.org/nexus/content/repositories/thirdparty/
done

popd

popd
rm -rf build
#!/bin/bash

FREEHEP_VERSION=2.1

mkdir build
pushd build

curl -OL https://github.com/freehep/freehep-io/tarball/freehep-io-${FREEHEP_VERSION} || exit

tar xvzf freehep-io-${FREEHEP_VERSION} || exit
pushd freehep-freehep-io-*

mvn clean package || exit

for JAR in target/*.jar
do
    bnd wrap ${JAR}
done

ARTIFACT_ID=freehep-io
BAR=target/freehep-io-${FREEHEP_VERSION}.bar
mvn deploy:deploy-file -DgroupId=cytoscape-temp -DartifactId=${ARTIFACT_ID} -Dversion=${FREEHEP_VERSION} -Dpackaging=jar -Dfile=${BAR} -DrepositoryId=thirdparty -Durl=http://code.cytoscape.org/nexus/content/repositories/thirdparty/

popd

popd
rm -rf build
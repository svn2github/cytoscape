#!/bin/bash

PROTOSTUFF_VERSION=1.0.7
ARTIFACT_ID=protostuff-core-json-osgi

mkdir build
pushd build

curl -OL http://protostuff.googlecode.com/files/protostuff-${PROTOSTUFF_VERSION}.zip || exit

unzip protostuff-${PROTOSTUFF_VERSION}.zip || exit

mkdir temp
pushd temp
for JAR in ../protostuff-${PROTOSTUFF_VERSION}/dist/{protostuff-api,protostuff-core,protostuff-json}-${PROTOSTUFF_VERSION}.jar
do
    unzip ${JAR}
done

rm -rf META-INF

zip -r ../${ARTIFACT_ID}.jar *
popd

bnd wrap ${ARTIFACT_ID}.jar

BAR=${ARTIFACT_ID}.bar
mvn deploy:deploy-file -DgroupId=cytoscape-temp -DartifactId=${ARTIFACT_ID} -Dversion=${PROTOSTUFF_VERSION} -Dpackaging=jar -Dfile=${BAR} -DrepositoryId=thirdparty -Durl=http://code.cytoscape.org/nexus/content/repositories/thirdparty/

popd
rm -rf build
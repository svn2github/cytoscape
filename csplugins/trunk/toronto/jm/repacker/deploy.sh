#!/bin/bash

GROUP_ID=$1
VERSION=$2

for FILE in bundles/*.jar
do
    ARTIFACT_ID=`basename ${FILE} .jar`
    mvn install:install-file -Dfile=${FILE} -DgroupId=${GROUP_ID} -DartifactId=${ARTIFACT_ID} -Dversion=${VERSION} -Dpackaging=jar
done

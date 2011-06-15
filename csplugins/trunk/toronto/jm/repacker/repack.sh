#!/bin/bash

BUNDLE_NAME=$1
shift

rm -rf temp-all temp-natives temp-jar bundles
mkdir -p temp-all
mkdir -p temp-natives
mkdir -p temp-jar
mkdir -p bundles

pushd temp-all
for JAR in "$@"
do
    unzip -o "../${JAR}"
    python ../extract_natives.py ${JAR} ../temp-natives
done

jar cvf ../temp-jar/${BUNDLE_NAME}.jar *
popd

pushd temp-natives
jar cvf ../temp-jar/${BUNDLE_NAME}-natives.jar *
popd

./wrap.sh temp-jar/*.jar
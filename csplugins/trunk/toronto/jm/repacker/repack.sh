#!/bin/bash

BUNDLE_NAME=$1
shift

rm -rf temp-all temp-natives
mkdir -p temp-all
mkdir -p temp-natives

pushd temp-all
for JAR in "$@"
do
    unzip -o "../${JAR}"
    python ../extract_natives.py ${JAR} ../temp-natives
done

jar cvf ../${BUNDLE_NAME}.jar *
popd

pushd temp-natives
jar cvf ../${BUNDLE_NAME}-natives.jar *
popd

rm -rf bundles
mkdir -p bundles
./wrap.sh
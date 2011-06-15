#!/bin/bash

function abspath {
    python -c 'import os, sys; print os.path.realpath(sys.argv[1])' "$1"
}

BUNDLE_NAME=$1
shift

SCRIPT_DIR=$(abspath .)
JAR_DIR=$(abspath temp-jar)
NATIVES_DIR=$(abspath temp-natives)
STAGING_DIR=$(abspath temp-staging)
BUNDLES_DIR=$(abspath bundles)

rm -rf "${JAR_DIR}" "${NATIVES_DIR}" "${STAGING_DIR}"
mkdir -p "${JAR_DIR}"
mkdir -p "${NATIVES_DIR}"
mkdir -p "${STAGING_DIR}"
mkdir -p "${BUNDLES_DIR}"

for JAR in "$@"
do
    JAR=$(abspath "$JAR")
    pushd "${STAGING_DIR}"
    unzip -o "${JAR}"
    python "${SCRIPT_DIR}/extract_natives.py" "${JAR}" "${NATIVES_DIR}"
    popd
done

pushd "${STAGING_DIR}"
jar cvf "${JAR_DIR}/${BUNDLE_NAME}.jar" *
popd

pushd "${NATIVES_DIR}"
jar cvf "${JAR_DIR}/${BUNDLE_NAME}-natives.jar" *
popd

"${SCRIPT_DIR}/wrap.sh" "${JAR_DIR}"/*.jar
#!/bin/bash

function abspath {
    python -c 'import os, sys; print os.path.realpath(sys.argv[1])' "$1"
}

BUILD_DIR=$(abspath build)
TEMP_DIR=$(abspath build/temp)
JAR_DIR=$(abspath build/jar)
FILES_FILE=$(abspath jogl.files)

source jogl.config

mkdir -p "${BUILD_DIR}"
mkdir -p "${TEMP_DIR}"
mkdir -p "${JAR_DIR}"

# Download JOGL
pushd "${TEMP_DIR}"
for FILE in $(cat "${FILES_FILE}")
do
    URL=${JOGL_BASE_URL}/jogl-${JOGL_VERSION}-${FILE}
    curl -C - -k -O "${URL}"
done

for FILE in *.7z
do
    7z x "${FILE}"
done
mv */jar/*.jar "${JAR_DIR}"
popd

pushd ${BUILD_DIR}

# Check out repacker
svn co http://chianti.ucsd.edu/svn/csplugins/trunk/toronto/jm/repacker/

# Repackage JOGL
pushd repacker
    ./repack.sh jogl "${JAR_DIR}"/*.jar
    ./deploy.sh cytoscape-temp ${JOGL_VERSION}
popd

# Check out bundles that need to be patched
svn co http://chianti.ucsd.edu/svn/core3/gui-distribution/trunk gui-distribution

for BUNDLE in gui-distribution swing-application-impl vizmap-gui-impl presentation-impl viewmodel-impl
do
    svn co http://chianti.ucsd.edu/svn/core3/impl/trunk/${BUNDLE}
done

# Check out paperwing-impl
svn co http://chianti.ucsd.edu/svn/csplugins/trunk/toronto/yuedong/paperwing-impl

popd

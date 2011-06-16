#!/bin/bash

function abspath {
    python -c 'import os, sys; print os.path.realpath(sys.argv[1])' "$1"
}

TEMP_DIR=$(abspath temp)
JAR_DIR=$(abspath jar)
FILES_FILE=$(abspath jogl.files)

source jogl.config

mkdir -p "${TEMP_DIR}"
mkdir -p "${JAR_DIR}"

# Download JOGL
pushd "${TEMP_DIR}"
for FILE in $(cat "${FILES_FILE}")
do
    URL=${JOGL_BASE_URL}/jogl-${JOGL_VERSION}-${FILE}
    curl -O "${URL}"
done

for FILE in *.7z
do
    7z x "${FILE}"
done
mv */jar/*.jar "${JAR_DIR}"
popd

# Check out repacker
svn co http://chianti.ucsd.edu/svn/csplugins/trunk/toronto/jm/repacker/

# Repackage JOGL
pushd repacker
    ./repack.sh jogl "${JAR_DIR}"/*.jar
    ./deploy.sh cytoscape-temp ${JOGL_VERSION}
popd

# Check out bundles that need to be patched
for BUNDLE in gui-distribution swing-application-impl vizmap-gui-impl
do
    svn co http://chianti.ucsd.edu/svn/core3/${BUNDLE}/trunk ${BUNDLE}
done

# Check out paperwing-impl
svn co http://chianti.ucsd.edu/svn/csplugins/trunk/toronto/yuedong/paperwing-impl

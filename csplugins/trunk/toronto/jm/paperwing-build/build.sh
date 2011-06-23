#!/bin/bash

function abspath {
    python -c 'import os, sys; print os.path.realpath(sys.argv[1])' "$1"
}

function apply_patches {
    for PATCH in $1/*.patch
    do
        patch -r ignore.rej -p0 -N < ${PATCH}
        rm ignore.rej
    done
}

DIST_DIR=$(abspath dist)
PATCH_DIR=$(abspath patches)

rm -rf ${DIST_DIR}
mkdir -p ${DIST_DIR}

# Resolve dependencies
pushd swing-application-impl
mvn dependency:resolve
popd

# Build main bundle
pushd paperwing-impl
apply_patches "${PATCH_DIR}/paperwing-impl"
mvn clean install
cp target/paperwing-impl-*.jar ${DIST_DIR}
popd

# Copy over non-core dependencies
cp repacker/bundles/*.jar ${DIST_DIR}

# Patch core so it doesn't look for ding
for BUNDLE in swing-application-impl vizmap-gui-impl presentation-impl
do
    pushd ${BUNDLE}
    apply_patches "${PATCH_DIR}/${BUNDLE}"
    mvn clean install
    popd
done

# Patch gui-distribution with our boot delegation options
pushd gui-distribution
grep bootdelegation distribution/src/main/bin/cytoscape.sh || sed -i '' -E 's/cytoscape-launcher.jar/-Dorg.osgi.framework.bootdelegation=sun.*,com.sun.*,apple.* cytoscape-launcher.jar/' distribution/src/main/bin/cytoscape.sh
mvn clean install

pushd distribution/target/cytoscape-*/cytoscape-*/

# Copy over our bundles
cp ${DIST_DIR}/*.jar bundles/plugins

# Remove ding bundles
rm bundles/startlevel-3/ding-*
popd

popd

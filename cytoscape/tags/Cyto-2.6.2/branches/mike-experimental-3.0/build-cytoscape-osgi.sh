#!/bin/bash

set -e

BNDDIR=`pwd`/osgi/bnd
BND="java -jar $BNDDIR/bnd.jar"
INST_EQINOX=build/equinox-cytoscape
INST_FELIX=build/felix-cytoscape
TMPDIR=build/tmp-osgi


if [[ ! -d build || ! -e cytoscape.jar ]]; then
	echo "everything needs to be compiled first!"
	exit 1
fi

# bnd the jars
echo
echo "wrapping the jars"
rm -rf $TMPDIR
mkdir $TMPDIR

#cp *.jar $TMPDIR
cp lib/*.jar $TMPDIR

cd $TMPDIR

for each in `ls -1 *.jar`
do
	$BND wrap -output $each $each
done

$BND wrap -properties $BNDDIR/cytoscape.bnd -output cytoscape.jar ../../cytoscape.jar
$BND wrap -properties $BNDDIR/jaxb-api.bnd -output jaxb-api.jar ../../lib/jaxb-api.jar
$BND wrap -properties $BNDDIR/jaxb-impl.bnd -output jaxb-impl.jar ../../lib/jaxb-impl.jar

cd -

# copy the jars to equinox
echo
echo "setting up equinox"
rm -rf $INST_EQINOX
cp -r osgi/equinox-template $INST_EQINOX
cp $TMPDIR/*.jar $INST_EQINOX/plugins
cp plugins/core/* $INST_EQINOX/old_plugins
cp osgi-plugins/* $INST_EQINOX/plugins

# copy the jars to felix
echo
echo "setting up felix"
rm -rf $INST_FELIX
cp -r osgi/felix-template $INST_FELIX 
cp $TMPDIR/*.jar $INST_FELIX/bundle
cp plugins/core/* $INST_FELIX/plugins
cp osgi-plugins/* $INST_FELIX/bundle

# clean up
rm -rf $TMPDIR

echo
echo "done"

#!/bin/bash

set -e

BNDDIR=`pwd`/osgi/bnd
BND="java -jar $BNDDIR/bnd-0.0.198.jar"
INST_EQINOX=build/equinox-cytoscape

if [[ ! -d build || ! -e cytoscape.jar ]]; then
	echo "everything needs to be compiled first!"
	exit 1
fi

rm -rf $INST_EQINOX
cp -r osgi/equinox-template $INST_EQINOX

$BND wrap -properties $BNDDIR/cytoscape.bnd cytoscape.jar
mv $BNDDIR/cytoscape.bar $INST_EQINOX/plugins/cytoscape.jar

cd lib
$BND wrap *.jar
mv *.bar ../$INST_EQINOX/plugins

cd ../$INST_EQINOX/plugins
python -c "import os; [ os.rename(f,'.'.join(f.split('.')[:-1])+'.jar') for f in os.listdir('.') ]"

cd ../../../

$BND wrap -properties $BNDDIR/jaxb-api.bnd lib/jaxb-api.jar
mv $BNDDIR/jaxb-api.bar $INST_EQINOX/plugins/jaxb-api.jar

$BND wrap -properties $BNDDIR/jaxb-impl.bnd lib/jaxb-impl.jar
mv $BNDDIR/jaxb-impl.bar $INST_EQINOX/plugins/jaxb-impl.jar

cp plugins/core/* $INST_EQINOX/old_plugins

cp osgi-plugins/* $INST_EQINOX/plugins

echo "done"

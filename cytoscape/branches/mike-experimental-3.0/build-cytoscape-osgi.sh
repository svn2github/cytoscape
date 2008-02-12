#!/bin/bash

set -e

bnd="java -jar `pwd`/bnd/bnd-0.0.198.jar"
INSTALL=build/cytoscape-osgi

if [[ ! -d build || ! -e cytoscape.jar ]]; then
	echo "everything needs to be compiled first!"
	exit 1
fi

rm -rf $INSTALL
cp -r osgi-template $INSTALL

$bnd wrap -properties bnd/cytoscape.bnd cytoscape.jar
mv bnd/cytoscape.bar $INSTALL/plugins/cytoscape.jar

cd lib
$bnd wrap *.jar
mv *.bar ../$INSTALL/plugins

cd ../$INSTALL/plugins
python -c "import os; [ os.rename(f,'.'.join(f.split('.')[:-1])+'.jar') for f in os.listdir('.') ]"

cd ../../../

$bnd wrap -properties bnd/jaxb-api.bnd lib/jaxb-api.jar
mv bnd/jaxb-api.bar $INSTALL/plugins/jaxb-api.jar

$bnd wrap -properties bnd/jaxb-impl.bnd lib/jaxb-impl.jar
mv bnd/jaxb-impl.bar $INSTALL/plugins/jaxb-impl.jar

cp plugins/core/* $INSTALL/old_plugins

cp osgi-plugins/* $INSTALL/plugins

echo "done"

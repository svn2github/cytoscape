#!/bin/bash

target=cytoscape

rm -rf $target

# set up felix dir
cp -r felix-cytoscape $target

# copy over bundles
cp application/target/*.jar $target/bundle
cp application/target/classes/*.jar $target/bundle
cp automatic.layout/target/automatic.layout-1.0-SNAPSHOT.jar $target/bundle
cp automatic.layout/target/classes/colt*.jar $target/bundle

#copy over plugins
cp biopax/target/biopax-1.0-SNAPSHOT.jar $target/plugins
cp editor/target/editor-1.0-SNAPSHOT.jar $target/plugins
cp merge/target/merge-1.0-SNAPSHOT.jar $target/plugins
cp manual.layout/target/manual.layout-1.0-SNAPSHOT.jar $target/plugins
cp psi.mi/target/psi.mi-1.0-SNAPSHOT.jar $target/plugins
cp quickfind/target/quickfind-1.0-SNAPSHOT.jar $target/plugins
cp filters.old/target/filters.old-1.0-SNAPSHOT.jar $target/plugins
cp filters/target/filters-1.0-SNAPSHOT.jar $target/plugins
cp sbml.reader/target/sbml.reader-1.0-SNAPSHOT.jar $target/plugins
cp table.import/target/table.import-1.0-SNAPSHOT.jar $target/plugins
cp attribute.browser/target/attribute.browser-1.0-SNAPSHOT.jar $target/plugins
cp linkout/target/linkout-1.0-SNAPSHOT.jar $target/plugins
cp cpath/target/cpath-1.0-SNAPSHOT.jar $target/plugins


# build the config file based on the bundles in the bundle dir
cd $target
cat conf/first-half.config.props.tmpl > conf/config.properties
for each in `ls -1 bundle/*.jar` 
do
	e=`echo $each | sed 's/bundle/file:bundle/g'`
	echo "$e \\" >> conf/config.properties
done
cat conf/second-half.config.props.tmpl >> conf/config.properties
cd -





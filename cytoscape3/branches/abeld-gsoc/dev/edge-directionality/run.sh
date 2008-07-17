#! /bin/bash

#
# run branch version of cytoscape
#

set -u # barf on unitialized variables
set -e # abort on first error

cd application
mvn install
cd ..

cd editor
mvn install
cd ..

cd filters
mvn install
cd ..
cd filters.old
mvn install
cd ..

cd table.import
mvn install
cd ..

cd vizmap
mvn install
cd ..

#cd /home/abeld/WORK/gsoc/cytoscape3-trunk/cytoscape3_pristine
#mvn pax:run

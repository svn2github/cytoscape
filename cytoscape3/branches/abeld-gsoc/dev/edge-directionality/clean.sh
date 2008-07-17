#! /bin/bash

#
# run branch version of cytoscape
#

set -u # barf on unitialized variables
set -e # abort on first error

cd application
mvn clean
cd ..

cd editor
mvn clean
cd ..

cd filters
mvn clean
cd ..
cd filters.old
mvn clean
cd ..

cd table.import
mvn clean
cd ..

cd vizmap
mvn clean
cd ..

#! /bin/bash

set -e

cd viewmodel-api-columns
mvn clean install
cd ..

cd viewmodel-impl-columns
mvn clean install
cd ..

cd vizmap-api-columns
mvn clean install
cd ..

cd vizmap-impl-columns
mvn clean install
cd ..

cd column-oriented-presentation
mvn clean install
cd ..

#cd default-mappings
#mvn clean install
#cd ..

cd column-oriented-integration_test
mvn clean install
mvn pax:run
cd ..

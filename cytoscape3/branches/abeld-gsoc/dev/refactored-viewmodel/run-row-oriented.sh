#! /bin/bash

set -e

cd viewmodel-api
mvn clean install
cd ..

cd viewmodel-row-based-impl
mvn clean install
cd ..

cd vizmap-api
mvn clean install
cd ..

cd vizmap-impl
mvn clean install
cd ..

cd presentation
mvn clean install
cd ..

cd default-mappings
mvn clean install
cd ..

cd integration_test
mvn clean install
mvn pax:run
cd ..

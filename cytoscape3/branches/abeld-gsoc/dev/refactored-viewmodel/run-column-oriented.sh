#! /bin/bash

set -e

cd column-oriented-viewmodel
mvn clean install
cd ..

cd column-oriented-vizmap
mvn clean install
cd ..

cd presentation
mvn clean install
cd ..

cd column-oriented-integration_test
mvn clean install
mvn pax:run
cd ..
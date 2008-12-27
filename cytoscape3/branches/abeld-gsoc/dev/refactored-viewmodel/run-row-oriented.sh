#! /bin/bash

set -e

cd viewmodel
mvn clean install
cd ..

cd vizmap
mvn clean install
cd ..

cd presentation
mvn clean install
cd ..

cd integration_test
mvn clean install
mvn pax:run
cd ..

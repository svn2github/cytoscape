#!/bin/bash

BND_CMD=bnd

for FILE in *.jar
do
    FRAGMENT_HOST=$(basename ${FILE} -natives.jar)
    echo ${FRAGMENT_HOST}
    ./genheaders.py ${FILE} ${FRAGMENT_HOST}
    if [ -f "${FILE}.properties" ]
    then
        ${BND_CMD} wrap -properties "${FILE}.properties" "${FILE}"
    else
        ${BND_CMD} wrap "${FILE}"
    fi
done

mkdir -p bundles
for FILE in *.bar
do
    mv "${FILE}" "bundles/$(basename ${FILE} .bar).jar"
done

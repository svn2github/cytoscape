#!/bin/bash

BND_CMD=bnd

for FILE in "$@"
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
for FILE in "$@"
do
    SOURCE=$(dirname ${FILE})/$(basename ${FILE} .jar).bar
    mv "${SOURCE}" "bundles/$(basename ${FILE})"
done

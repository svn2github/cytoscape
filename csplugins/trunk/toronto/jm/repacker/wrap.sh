#!/bin/bash

BND_CMD="java -jar bnd.jar"

if [[ ! -s bnd.jar ]]
then
    curl http://dl.dropbox.com/u/2590603/bnd/biz.aQute.bnd.jar > bnd.jar
fi

function fixpath {
    if [ -z $(which cygpath) ]
    then
        echo "$@"
    else
        cygpath -w "$@"
    fi
}

for FILE in "$@"
do
    FRAGMENT_HOST=$(basename ${FILE} -natives.jar)
    echo ${FRAGMENT_HOST}
    ./genheaders.py ${FILE} ${FRAGMENT_HOST}
    JAR=$(fixpath ${FILE})
    if [ -f "${FILE}.properties" ]
    then
        ${BND_CMD} wrap -properties "${JAR}.properties" "${JAR}"
    else
        ${BND_CMD} wrap "${JAR}"
    fi
done

mkdir -p bundles
for FILE in "$@"
do
    SOURCE=$(dirname ${FILE})/$(basename ${FILE} .jar).bar
    mv "${SOURCE}" "bundles/$(basename ${FILE})"
done

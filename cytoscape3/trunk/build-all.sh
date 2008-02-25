#!/bin/bash

for each in `ls -1`
do
	if [[ -d $each ]]
	then
		echo trying $each
		cd $each
		mvn clean install
		cd ..
	fi
done

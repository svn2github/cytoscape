#!/bin/bash

dirname=`date "+%Y%m%d%H%M%S"`

mkdir $dirname
cd $dirname

function testArchetype {
	mvn archetype:generate -DarchetypeCatalog=local -DarchetypeArtifactId=$1 -DarchetypeGroupId=org.cytoscape.archetypes -DgroupId=org.example -DartifactId=$1 -Dversion=1.0-SNAPSHOT -Dcytoscape.api.version=3.0.0-alpha1 -DinteractiveMode=false| grep "BUILD FAILURE" > generate.out
	if [[ -s generate.out ]] 
	then
		exit "archetype generate $1 FAILED!"
	fi


	cd $1
	mvn clean test | grep "BUILD FAILURE" > error.out 
	if [[ -s error.out ]] 
	then
		echo "archetype build $1 FAILED!"
		exit 1
	fi
	cd ..
}

testArchetype api-provider-plugin
testArchetype task-plugin
testArchetype cyaction-plugin

cd ..


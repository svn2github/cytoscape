#!/bin/bash

find . -name "pom.xml"  -exec grep -H -A 2 "<dependency>" {} \; | grep -vE "groupId|dependency|--" | sed 's/pom.xml\- */ dependsOn /' | sed 's/\.*\///g' | sed 's/<artifactId>//g'

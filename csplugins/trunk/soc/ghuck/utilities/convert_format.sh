#!/bin/bash

mkdir gml

for file in *.graph
do
    ./chaco2gml $file gml/${file%%.graph}.gml    
done
#!/bin/bash

SAMPLE_DIR="/home/gerardo/samples/gml"
OUTPUT_FOLDER="/home/gerardo/output_images"

#mkdir $OUTPUT_FOLDER
cd $SAMPLE_DIR

for file in *.gml 
do
    echo import network ${SAMPLE_DIR}/${file}
    echo layout gpu-assisted-layout
    echo export network as ps to ${OUTPUT_FOLDER}/${file%%.*}.gpu.ps
    echo layout force-directed
    echo export network as ps to ${OUTPUT_FOLDER}/${file%%.*}.fd.ps
    echo destroy ${file}
done

echo exit
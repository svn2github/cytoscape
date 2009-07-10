#!/bin/bash

ant clean

ant compile

cd src/gpuGraphDrawing

make clean

make

cd ../../

ant jar

#!/bin/bash

ant compile

cd src/gpuGraphDrawing

make

cd ../../

ant jar

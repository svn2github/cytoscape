#!/bin/sh

for i in *.ps
do
    convert ${i} ${i%.*}.png
done
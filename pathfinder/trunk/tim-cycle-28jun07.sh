#!/bin/bash

DIR=/Users/cmak/Desktop/Tim-RIKEN-data/28jun07-small-PPI

./time.pl -e mqpcr -thresh min=$DIR/PMA.entrezids $DIR/exstented_PPI.sif $DIR/Normalized_THP-1+PMA_3th_rankinv_DAVID_processed_min600.txt $DIR/Tim-PMA-cycling-smallPPI
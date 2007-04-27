#!/bin/bash

DIR=/Users/cmak/Desktop/Tim-RIKEN-data/20apr07/

./time.pl -thresh 600 -e qpcr $DIR/David_candidates_network_v1.sif $DIR/Normalized_THP-1+LPS_6th_rankinv.mrna $DIR/Tim-LPS-600

./time.pl -thresh 600 -e qpcr $DIR/David_candidates_network_v1.sif $DIR/Normalized_THP-1+PMA_3rd_rankinv.mrna $DIR/Tim-PMA-600
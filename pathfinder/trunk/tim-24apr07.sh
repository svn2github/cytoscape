#!/bin/bash

DIR=/Users/cmak/Desktop/Tim-RIKEN-data/24apr07

./time.pl -e qpcr $DIR/David_candidates_network_v1_no_miRNA.sif $DIR/Normalized_THP-1+LPS_6th_rankinv_DAVID_processed_min600.txt $DIR/Tim-LPS-processed

./time.pl -e qpcr $DIR/David_candidates_network_v1_no_miRNA.sif $DIR/Normalized_THP-1+PMA_3th_rankinv_DAVID_processed_min600.txt $DIR/Tim-PMA-processed
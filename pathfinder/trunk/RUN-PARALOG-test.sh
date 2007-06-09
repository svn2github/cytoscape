#!/bin/bash

DIR=paralog-test-results
IN_DIR=paralog-test

PD_SIF=$IN_DIR/pd.mixed.sif
BLAST_SIF=$IN_DIR/blast.sif
CLASS_NA=$IN_DIR/class.na
VIZMAP=../../../thesis/paralogs/subt_with_EA.props

NA_BASENAME=$DIR/blast.mixed
SCORE_OUT=$DIR/blast.mixed.paralog-score.out
PD_FILTER_OUT=$DIR/mixed.filtered.sif

[ -d $DIR ] || mkdir $DIR

# Score the paralog compoents
 ./score-paralogs.pl $BLAST_SIF $CLASS_NA $PD_SIF -v -o $NA_BASENAME > $SCORE_OUT

# Filter binding SIF
 ./filter-sif.pl $PD_SIF $NA_BASENAME.src.na $NA_BASENAME.tgt.na > $PD_FILTER_OUT

# Create merged binding and BLAST sif file
cat $PD_FILTER_OUT $BLAST_SIF > $DIR/merged.sif

/Applications/Cytoscape_v2.4.1/cytoscape.sh -N $DIR/merged.sif -n $CLASS_NA -n $NA_BASENAME.src.na $NA_BASENAME.tgt.na -V  $VIZMAP
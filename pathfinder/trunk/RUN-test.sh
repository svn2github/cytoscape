#!/bin/bash

DIR=test-results

PD_SIF=~/Desktop/ChIP-papers/Harbison/Harbison2004_9.2_forpaper_YPD.1e-3.sif
BLAST_SIF=paralog-test/hxt-test.sif
CLASS_NA=../../cpos/trunk/SC-ChromosomeLocation-mtd=30000.na
VIZMAP=../../../thesis/paralogs/subt_with_EA.props
PID_EA=../../../thesis/paralogs/orf_trans.fasta.out.pID.ea

NA_BASENAME=$DIR/hxt-test.Harbison_YPD
SCORE_OUT=$DIR/hxt-test.paralog-score.out
PD_FILTER_OUT=$DIR/Harbison_YPD.1e-3.hxt-test.filtered.sif

[ -d $DIR ] || mkdir $DIR

# Score the paralog compoents
 ./score-paralogs.pl $BLAST_SIF $CLASS_NA $PD_SIF -v -o $NA_BASENAME > $SCORE_OUT

# Filter binding SIF
# ./filter-sif.pl $PD_SIF $NA_BASENAME.src.na $NA_BASENAME.tgt.na > $PD_FILTER_OUT

# Create merged binding and BLAST sif file
#cat $PD_FILTER_OUT $BLAST_SIF > $DIR/merged.sif

#/Applications/Cytoscape_v2.4.1/cytoscape.sh -N $DIR/merged.sif -n $CLASS_NA -n $NA_BASENAME.src.na $NA_BASENAME.tgt.na -n ~/data/orf2name.noa -V  $VIZMAP -e $PID_EA
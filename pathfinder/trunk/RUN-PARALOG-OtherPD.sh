#!/bin/bash

DIR=paralog-results-OtherPD

PD_SIF=~/Desktop/ChIP-papers/Harbison/Harbison2004_9.2_forpaper_Other.1e-3.sif
#BLAST_SIF=../../../thesis/paralogs/orf_trans.sif
BLAST_SIF=../../../thesis/paralogs/AbsID.gt.60.sif
CLASS_NA=../../cpos/trunk/SC-ChromosomeLocation-mtd=30000.na
VIZMAP=../../../thesis/paralogs/subt_with_EA.props
PID_EA=../../../thesis/paralogs/orf_trans.fasta.out.pID.ea
NAME_NA=~/Desktop/ChIP-papers/Harbison/orf2name+HarbisonOther.na

NA_BASENAME=$DIR/orf_trans.subt.Harbison_Other
SCORE_OUT=$DIR/orf_trans.paralog-score.out
PD_FILTER_OUT=$DIR/Harbison_Other.1e-3.paralog.filtered.sif

[ -d $DIR ] || mkdir $DIR

# Score the paralog compoents
./score-paralogs.pl $BLAST_SIF $CLASS_NA $PD_SIF -v -o $NA_BASENAME > $SCORE_OUT

# Filter binding SIF
./filter-sif.pl $PD_SIF $NA_BASENAME.src.na $NA_BASENAME.tgt.na > $PD_FILTER_OUT

# Create merged binding and BLAST sif file
cat $PD_FILTER_OUT $BLAST_SIF > $DIR/merged.sif

/Applications/Cytoscape_v2.4.1/cytoscape.sh -N $DIR/merged.sif -n $CLASS_NA -n $NA_BASENAME.src.na $NA_BASENAME.tgt.na -n $NAME_NA -V  $VIZMAP -e $PID_EA
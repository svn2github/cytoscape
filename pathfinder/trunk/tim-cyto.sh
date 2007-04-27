#!/bin/sh

OUT=$1

echo $OUT

/Applications/Cytoscape-v2.3.1/cytoscape.sh -N $OUT.sif -n $OUT-tme.na -n $OUT-ratio.na -n ~/data/entrez-gene-id-2-name.na

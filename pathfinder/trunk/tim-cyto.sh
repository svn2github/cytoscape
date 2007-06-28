#!/bin/sh

OUT=$1

echo $OUT

/Applications/Cytoscape_v2.4.1/cytoscape.sh -N $OUT.sif -n $OUT-tme.na -n $OUT-ratio.na -n ~/data/entrez-gene-id-2-name.na

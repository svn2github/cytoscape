#!/bin/sh

OUT=$1

cytoscape.sh -N $OUT.sif -n $OUT-tme.na -n $OUT-ratio.na -n ~/data/GO/TFs/MOL_FCN-alltfs.na -n ~/data/orf2name.noa

#
#
# Command line arguments:
#
# max path length
# interaction graph SIF file
# knockout pvalues
# expression pvalue threshold
# interaction graph edge attribute file
# protien-DNA edge pvalue threshold
# output directory
# base output file name
#
# -Xrunhprof:cpu=samples,depth=8
#
java  -Xmx512m fgraph.MPMain \
5 \
yeang-data-ORF.sif \
../../data/data_full_subset_orfs.pvals \
.05 \
yeang-data-ORF.eda \
1 \
testOut \
test_yeang \
normal \
STE12candidategenes.txt

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
java  -Xmx512m MaxProduct \
3 \
all.sif \
../../data/data_full_subset_orfs.pvals \
.02 \
../../data/all.edgeattr \
.01 \
testOut \
test_full


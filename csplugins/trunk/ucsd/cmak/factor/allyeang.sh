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
# ko cutoff. print models that explain >= cutoff knockout experiments#
#
# -Xrunhprof:cpu=samples,depth=8
#
set PATH=3
set EXP=0.02
set EDGE=0

set TS=`date +%F-%H.%M`

set DATA=$HOME/data/yeang-data

echo "### mp.sh time = ${TS}"
echo "### mp.sh MAX_PATH_LEN = ${PATH}"
echo "### mp.sh expression pvalue threshold =  ${EXP}"
echo "### mp.sh PD edge pvalue threshold = ${EDGE}"

java  -Xmx512m -Djava.util.logging.config.file=./logging.properties \
fgraph.MPMain \
${PATH} \
${DATA}/model.sif \
${DATA}/yeang-hughes.pvals \
${EXP} \
${DATA}/model.eda \
${EDGE} \
testOut \
test_yeang_model_${PATH}_${EXP}_${EDGE} \
3
#normal \
#${DATA}/STE12candidate-orfs.txt
#

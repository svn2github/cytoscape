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
set PATH=3
set EXP=0.02
set EDGE=0.001

set TS=`date +%F-%H.%M`

set DATA=$HOME/data/yeang-data

echo "### mp.sh time = ${TS}"
echo "### mp.sh MAX_PATH_LEN = ${PATH}"
echo "### mp.sh expression pvalue threshold =  ${EXP}"
echo "### mp.sh PD edge pvalue threshold = ${EDGE}"

java  -Xmx512m MPMain \
${PATH} \
${DATA}/yall.sif \
${DATA}/yeang-hughes.pvals \
${EXP} \
${DATA}/yall.edgeattr \
${EDGE} \
testOut \
test_yall_${PATH}_${EXP}_${EDGE}
#profile 
#STE12candidategenes.txt
#

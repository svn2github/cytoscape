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
#set OUT=$1

set PATH=2
set EXP=0.99
set EDGE=0.02

set TS=`date +%F-%H.%M`

set DATA=$HOME/data

set ALL=all-p0.02-06dec2004
set MMS=mms+pp-p0.02-01dec2004
set NETWORK=${MMS}
set NET_LABEL=mms

set DECOMPOSE=false

set RUN_LABEL=buffer_${NET_LABEL}_${DECOMPOSE}_${PATH}_${EXP}_${EDGE}
set OUT=prop-files/${RUN_LABEL}.props

rm ${OUT}

echo "### mp.sh time = ${TS}"
echo "### mp.sh MAX_PATH_LEN = ${PATH}"
echo "### mp.sh expression pvalue threshold =  ${EXP}"
echo "### mp.sh PD edge pvalue threshold = ${EDGE}"

echo "max.path.length=${PATH}" >> ${OUT}
echo "interaction.network=${DATA}/buffering/${NETWORK}.sif" >> ${OUT}
echo "expression.file=${DATA}/buffering/buffered-genes-20041208.pscores" >> ${OUT}
echo "expression.threshold=${EXP}" >> ${OUT}
echo "edge.attributes=${DATA}/buffering/${NETWORK}.ea" >> ${OUT}
echo "protein-DNA.threshold=${EDGE}" >> ${OUT}
echo "output.dir=result" >> ${OUT}
echo "output.filename=${RUN_LABEL}" >> ${OUT}
echo "min.ko.per.model=1" >> ${OUT}
echo "decomposeModel=${DECOMPOSE}" >> ${OUT}

echo ">>> Generated file: ${OUT}"

runmp.sh ${OUT}

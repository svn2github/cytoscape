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

set TS=`date +%F-%H.%M`
set DATA=$HOME/data/yeang-data

set PATH=3
set EXP=0.02
set EDGE=0
set DECOMPOSE=true
set YEANG_DATA_FORMAT=true

set NETWORK=yeang-all
set NET_LABEL=all

set RUN_LABEL=yeang_${NET_LABEL}_${DECOMPOSE}_${PATH}_${EXP}_${EDGE}
set OUT=prop-files/${RUN_LABEL}.props

rm ${OUT}

echo "### mp.sh time = ${TS}"
echo "### mp.sh MAX_PATH_LEN = ${PATH}"
echo "### mp.sh expression pvalue threshold =  ${EXP}"
echo "### mp.sh PD edge pvalue threshold = ${EDGE}"

echo "max.path.length=${PATH}" >> ${OUT}
echo "interaction.network=${DATA}/${NETWORK}.sif" >> ${OUT}
echo "expression.file=${DATA}/yeang-ko.eda" >> ${OUT}
echo "expression.threshold=${EXP}" >> ${OUT}
echo "edge.attributes=${DATA}/${NETWORK}.eda" >> ${OUT}
echo "protein-DNA.threshold=${EDGE}" >> ${OUT}
echo "output.dir=testOut" >> ${OUT}
echo "output.filename=${RUN_LABEL}" >> ${OUT}
echo "min.ko.per.model=3" >> ${OUT}
echo "decomposeModel=${DECOMPOSE}" >> ${OUT}
echo "yeang.data.format=${YEANG_DATA_FORMAT}" >> ${OUT}
#echo "candidate.genes=${DATA}/STE12candidategenes.txt" >> ${OUT}

echo ">>> Generated file: ${OUT}"

runmp.sh ${OUT}

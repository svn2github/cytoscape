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
set DATA=$HOME/data

set PATH=2
set EXP=0.78
set EDGE=0.01
set DECOMPOSE=false
set YEANG_DATA_FORMAT=false

set ALL=ymp-27Feb05-p0.05
#set ALL=all-p0.02-27Feb05
#set MMS=mms+pp-p0.02-07dec2004
set NETWORK=${ALL}
set NET_LABEL=ymp

set RUN_LABEL=buffer_${NET_LABEL}_${DECOMPOSE}_${PATH}_${EXP}_${EDGE}
set OUT=prop-files/${RUN_LABEL}.props

rm ${OUT}

echo "### mp.sh time = ${TS}"
echo "### mp.sh MAX_PATH_LEN = ${PATH}"
echo "### mp.sh expression pvalue threshold =  ${EXP}"
echo "### mp.sh PD edge pvalue threshold = ${EDGE}"

echo "max.path.length=${PATH}" >> ${OUT}
#echo "interaction.network=${DATA}/buffering/${NETWORK}.sif" >> ${OUT}
echo "interaction.network=${DATA}/location/${NETWORK}.sif" >> ${OUT}
echo "expression.file=${DATA}/buffering/TF_KOs_orf.logratios.pscores4.tab2" >> ${OUT}
echo "expression.threshold=${EXP}" >> ${OUT}
#echo "edge.attributes=${DATA}/buffering/${NETWORK}.eda" >> ${OUT}
echo "edge.attributes=${DATA}/location/${NETWORK}.eda" >> ${OUT}
echo "protein-DNA.threshold=${EDGE}" >> ${OUT}
echo "output.dir=result" >> ${OUT}
echo "output.filename=${RUN_LABEL}" >> ${OUT}
echo "min.ko.per.model=1" >> ${OUT}
echo "decomposeModel=${DECOMPOSE}" >> ${OUT}
echo "yeang.data.format=${YEANG_DATA_FORMAT}" >> ${OUT}
#echo "candidate.genes=${DATA}/STE12candidategenes.txt" >> ${OUT}

echo ">>> Generated file: ${OUT}"

runmp.sh ${OUT}

#cat result/${RUN_LABEL}-0.sif | add-pp.pl >> result/${RUN_LABEL}-0.sif

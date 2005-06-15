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
set DATA=$HOME/data-lab

set PATH=2
set EXP=0.5
set EDGE=0.02
set DECOMPOSE=false
set YEANG_DATA_FORMAT=false

set ALL=ymp-BOTH_binding_0.226_0.02000048_0.0464_0.004520108_FINAL.tab-9Jun05
#set ALL=ymp-BOTH_binding_0.2002_0.01_0.024_0.00200546_FINAL.tab-10Jun05

set NETWORK=${ALL}
set NET_LABEL=ymp_0.226_0.02_0.0045

set RUN_LABEL=buffer_${NET_LABEL}_${DECOMPOSE}_${PATH}_${EXP}_${EDGE}
set OUT=prop-files/${RUN_LABEL}.props

rm ${OUT}

echo "### mp.sh time = ${TS}"
echo "### mp.sh MAX_PATH_LEN = ${PATH}"
echo "### mp.sh expression pvalue threshold =  ${EXP}"
echo "### mp.sh PD edge pvalue threshold = ${EDGE}"

echo "max.path.length=${PATH}" >> ${OUT}
#echo "interaction.network=${DATA}/buffering/${NETWORK}.sif" >> ${OUT}
echo "interaction.network=${DATA}/data/location/TPM/${NETWORK}.sif" >> ${OUT}
echo "expression.file=${DATA}/data/buffering/BUF_KOs.ORFheader.lrpv" >> ${OUT}
echo "expression.threshold=${EXP}" >> ${OUT}
#echo "edge.attributes=${DATA}/buffering/${NETWORK}.eda" >> ${OUT}
echo "edge.attributes=${DATA}/data/location/TPM/${NETWORK}.ea" >> ${OUT}
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

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
set OUT=$1

set PATH=2
set EXP=0.99
set EDGE=0.02

set TS=`date +%F-%H.%M`

set DATA=$HOME/data

echo "### mp.sh time = ${TS}"
echo "### mp.sh MAX_PATH_LEN = ${PATH}"
echo "### mp.sh expression pvalue threshold =  ${EXP}"
echo "### mp.sh PD edge pvalue threshold = ${EDGE}"

echo "max.path.length=${PATH}" >> ${OUT}
echo "interaction.network=${DATA}/buffering/mms+pp-p0.02-01dec2004.sif" >> ${OUT}
echo "expression.file=${DATA}/buffering/buffered-genes.pscores" >> ${OUT}
echo "expression.threshold=${EXP}" >> ${OUT}
echo "edge.attributes=${DATA}/buffering/mms+pp-p0.02-01dec2004.ea" >> ${OUT}
echo "protein-DNA.threshold=${EDGE}" >> ${OUT}
echo "output.dir=result" >> ${OUT}
echo "output.filename=buffer_mms_newlr_mergeall_${PATH}_${EXP}_${EDGE}" >> ${OUT}
echo "min.ko.per.model=1" >> ${OUT}
echo "decomposeModel=true" >> ${OUT}



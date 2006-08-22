# Script to generate input file and run factor graph algorithm
#
# Input file are stored in the "prop-files" directory
#
# Command line arguments:
#
# max.path.length     
# interaction.network       interaction graph (Cytoscape SIF file format)
# expression.file           knockout pvalues
# expression.threshold      expression pvalue threshold
# edge.attributes           interaction graph edge attribute file
# protein-DNA.threshold     protien-DNA edge pvalue threshold
# output.dir                output directory
# output.filename           base output file name
# min.ko.per.model          only print models that explain at least
#                           this many knockout  experiments
# yeang.data.format         Old option.  Used for testing with old
#                           data format.
#
# To run with Java profiling
# -Xrunhprof:cpu=samples,depth=8
#

set TS=`date +%F-%H.%M`
set DATA=/cellar/users/cmak/data

set MAX_PATH_LEN=2
set EXP_THRESH=0.005
set EDGE_THRESH=0.001
set DECOMPOSE=false
set YEANG_DATA_FORMAT=false

set BUF_FILE=BUF_KOs.ORFheader.lrpv
#set BUF_FILE=BUF_KOs+follow_ups.ORFheader.lrpv
# Don't forget to set EXP to 0.004 for the followups
#set BUF_FILE=BUF_follow_ups_and_WT.ORFheader.lrpv
#set BUF_FILE=ENH_KOs.ORFheader.lrpv

set BUF_LABEL="buf"
#set BUF_LABEL="buf_followups"
#set BUF_LABEL="enh"

set ALL=ymp-BOTH_binding_0.1436_0.0010032486_0.0026_0.0001440665_FINAL.tab-12Aug05
set NETWORK=${ALL}
set NET_LABEL="ymp_t=0.1436_t=0.001_tsq=0.000268"

set INTERACTION_NETWORK=${DATA}/location/TPM/${NETWORK}.sif
set EXP_FILE=${DATA}/buffering/${BUF_FILE}
set EDGE_ATTR=${DATA}/location/TPM/${NETWORK}.ea

set RUN_LABEL=${BUF_LABEL}_${NET_LABEL}_${DECOMPOSE}_${MAX_PATH_LEN}_${EXP_THRESH}_${EDGE_THRESH}
set OUT=prop-files/${RUN_LABEL}.props

rm ${OUT}

echo "### mp.sh time = ${TS}"
echo "### mp.sh MAX_PATH_LEN = ${MAX_PATH_LEN}"
echo "### mp.sh expression file = ${BUF_FILE}"
echo "### mp.sh expression pvalue threshold =  ${EXP_THRESH}"
echo "### mp.sh PD edge pvalue threshold = ${EDGE_THRESH}"


echo "max.path.length=${MAX_PATH_LEN}" >> ${OUT}
echo "interaction.network=${INTERACTION_NETWORK}" >> ${OUT}
echo "expression.file=${EXP_FILE}" >> ${OUT}
echo "expression.threshold=${EXP_THRESH}" >> ${OUT}
echo "edge.attributes=${EDGE_ATTR}" >> ${OUT}
echo "protein-DNA.threshold=${EDGE_THRESH}" >> ${OUT}
echo "output.dir=result" >> ${OUT}
echo "output.filename=${RUN_LABEL}" >> ${OUT}
echo "min.ko.per.model=0" >> ${OUT}
echo "decomposeModel=${DECOMPOSE}" >> ${OUT}
echo "yeang.data.format=${YEANG_DATA_FORMAT}" >> ${OUT}

# candidate.genes option is unused and untested
#echo "candidate.genes=${DATA}/STE12candidategenes.txt" >> ${OUT}

echo ">>> Generated file: ${OUT}"


##
## Now, run the progam using the input file we just generated
##

runmp.sh ${OUT}

#cat result/${RUN_LABEL}-0.sif | add-pp.pl >> result/${RUN_LABEL}-0.sif

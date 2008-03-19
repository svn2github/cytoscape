# Script to generate input file and run factor graph algorithm
#
# Input file is stored in the "prop-files" directory
# Output files are stored in the $RESULT_DIR directory
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
set DATA=test-data
set RESULT_DIR=result

set MAX_PATH_LEN=3
set EXP_THRESH=0.02
set EDGE_THRESH=0 # Zero means use all edges
set DECOMPOSE=true
set YEANG_DATA_FORMAT=false
set MIN_KO_PER_MODEL=3

set BUF_FILE=yeang2005-expression.eda
set BUF_LABEL="yeang2005"

set INTERACTION_NETWORK=${DATA}/yeang2005-network.sif
set EXP_FILE=${DATA}/yeang2005-expression.eda
set EDGE_ATTR=${DATA}/yeang2005-network.eda
set NET_LABEL="yeang2005"

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
echo "output.dir=${RESULT_DIR}" >> ${OUT}
echo "output.filename=${RUN_LABEL}" >> ${OUT}
echo "min.ko.per.model=${MIN_KO_PER_MODEL}" >> ${OUT}
echo "decomposeModel=${DECOMPOSE}" >> ${OUT}
echo "yeang.data.format=${YEANG_DATA_FORMAT}" >> ${OUT}

echo ">>> Generated file: ${OUT}"


##
## Now, run the progam using the input file we just generated
##
mkdir $RESULT_DIR
runmp.sh ${OUT}


base=$1

cytoscape.sh -i ${base}.sif \
-n /cellar/users/cmak/data/all_orfs.noa \
-n ${base}_type.noa \
-j fgtest.eda \
-j ${base}_dir.eda \
-j ${base}_sign.eda  \
&

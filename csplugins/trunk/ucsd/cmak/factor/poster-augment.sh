#!/bin/sh

ddir=$HOME/data

data=$1
echo $data

test -f poster-result/${data}-0.sif &&
{
#$ddir/poster/network/mms-edge.sif \
    cat poster-result/${data}-0.sif \
	$HOME/darin/buftargets0.1-process.sif \
	> poster-result/${data}.sif

    mv poster-result/${data}-0.sif poster-result/${data}-0.siforig
}

cat $ddir/poster/network/type-0.1.noa \
    $HOME/darin/buftargets0.1-process.noa \
    poster-result/${data}_type.noa \
    | grep -v NodeType \
    > poster-result/${data}_type2.noa

sed -e '1 i\NodeType (class=java.lang.String)' poster-result/${data}_type2.noa > poster-result/${data}_type_aug.noa


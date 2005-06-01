#!/bin/sh

ddir=$HOME/data
outdir=result

data=$1
echo $data

test -f $outdir/${data}-0.sif &&
[ ! -f $outdir/${data}-0.siforig ] &&
{
    echo "appending pp edges"

    cat $outdir/${data}-0.sif \
	$ddir/pp.sif \
	> $outdir/${data}.sif

    mv $outdir/${data}-0.sif $outdir/${data}-0.siforig
    mv $outdir/${data}.sif $outdir/${data}-0.sif
}


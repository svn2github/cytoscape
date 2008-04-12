#!/bin/bash

(cd test-thumb; ln -s ../test/*.eps .)

../eps2png test/*.eps
../eps2png-thumb test-thumb/*.eps

function makeIndex {
dir=$1
echo "<html><body>" > $dir/index.html
for i in $dir/*.png
do
    x=`basename $i`
   echo "<img src=\"$x\"\><br>" >> $dir/index.html
done
echo "</body></html>" >> $dir/index.html
}

makeIndex test
makeIndex test-thumb

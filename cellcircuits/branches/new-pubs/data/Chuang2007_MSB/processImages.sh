#!/bin/bash

function makeIndex {
    dir=$1
    echo "<html><body>" > $dir/index.html
    for i in $dir/*.png
      do
      x=`basename $i`
      echo "$x<img border=1 src=\"$x\"\><br>" >> $dir/index.html
    done
    echo "</body></html>" >> $dir/index.html
}

SUBDIRS="Wang vandeVijver"
#SUBDIRS="Wang"
#SUBDIRS="vandeVijver"
for subdir in $SUBDIRS
do
  
  #../eps2png eps/$subdir/*.eps
  #mv eps/$subdir/*.png lrg_img/$subdir

  #../eps2png-thumb eps/$subdir/*.eps
  #mv eps/$subdir/*.png sml_img/$subdir
  
  echo "Making test indicies for $subdir"
  makeIndex lrg_img/$subdir
  makeIndex sml_img/$subdir
done

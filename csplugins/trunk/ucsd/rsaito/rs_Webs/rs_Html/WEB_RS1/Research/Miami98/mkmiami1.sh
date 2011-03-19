#!/bin/tcsh

set prefile = "temp"

foreach filename ( Slide[0-9].GIF )

   set curfile = `echo $filename | awk -F. '{ print $1".html" }'`
   echo "<img src = ${filename}>" > $curfile
   echo "<A HREF = ${curfile}>Next</a><p>" >> $prefile

   set prefile = ${curfile}

end


foreach filename ( Slide1[0-9].GIF )

   set curfile = `echo $filename | awk -F. '{ print $1".html" }'`
   echo "<img src = ${filename}>" > $curfile
   echo "<A HREF = ${curfile}>Next</a><p>" >> $prefile

   set prefile = ${curfile}

end


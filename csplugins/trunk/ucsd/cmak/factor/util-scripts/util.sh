#!/bin/tcsh

foreach file (AdjGraphTest.java)
    echo $file
    mv $file util/$file
    cvs remove $file
    cvs add util/$file
end

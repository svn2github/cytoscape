#!/bin/tcsh

foreach file (*.java)
    echo $file
    mv $file fgraph/$file
    cvs remove $file
    cvs add fgraph/$file
end

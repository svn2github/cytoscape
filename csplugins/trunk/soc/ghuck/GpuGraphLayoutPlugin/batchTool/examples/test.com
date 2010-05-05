import network pte.xgmml
import node attributes subgroup.noa
layout force-directed
set exportTextAsShape=false cytoscape.version.number=2.6.2
export network as pdf to /home/scooter/Desktop/test.pdf zoom=10
exit


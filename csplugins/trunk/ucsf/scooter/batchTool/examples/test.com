import network pte.xgmml
import node attributes subgroup.noa
layout force-directed
set exportTextAsShape=false cytoscape.version.number=2.6.2
set nodelinkouturl.BioCyc.HumanCyc=http://biocyc.org/HUMAN/NEW-IMAGE?type=NIL&object=%ID%
export network as pdf to /home/scooter/Desktop/test.pdf zoom=10
export network as XGMML to /home/scooter/Desktop/test.xgmml
exit


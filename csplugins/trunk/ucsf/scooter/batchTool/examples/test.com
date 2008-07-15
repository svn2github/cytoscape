import network pte.xgmml
import node attributes subgroup.noa
layout force-directed
export network as pdf to /Users/scooter/Desktop/test.pdf zoom=10
exit


#!/usr/bin/env python

from IVV_Packages.PPI_Pred.Display_Pred_Source1 \
     import Display_Pred_Source

print
print
while True:
    geneid1 = raw_input("Input Gene ID 1: ")
    geneid2 = raw_input("Input Gene ID 2: ")
    Display_Pred_Source(geneid1, geneid2)




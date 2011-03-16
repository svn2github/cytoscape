#!/usr/bin/env python
# Carriage return NOT CONSIDERED!!

import sys
from Data_Struct.Hash2 import Hash

homol_file = sys.argv[1]
convid_file = sys.argv[2]

convid = Hash("S")
convid.read_file(convid_file, [0], [1])

fh = open(homol_file, "r")

header = fh.readline()

print header
for line in fh:
    r = line.split("\t")
    r[0] = convid.val(r[0])
    print "\t".join(r)

    
    

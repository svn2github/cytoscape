#!/usr/bin/env python

import sys
sys.path.append("../")
import string

import Data_Struct.Hash
import Data_Struct.MultiDimDict
import Usefuls.DictKeyIterator

localization_file = "prot_localiz"
localization = Data_Struct.Hash.Hash("A")
localization.read_file(filename = localization_file,
		       Key_cols = [0], Val_cols = [1]);

counter = Data_Struct.MultiDimDict.MultiDimDict(1, 0)

for geneid in localization.keys():
    localiz = localization.val(geneid)
    if len(localiz) != 1: continue
    counter.plus_val((localiz[0],), 1)

counter_iter = Usefuls.DictKeyIterator.DictKey_Iterator(counter.get_all_data())
counter_dict = counter_iter.Squash_Dict()

locals = counter_dict.keys()

total = 0
for l in locals:
    total += counter_dict[l]

locals_main = ( "Nucleus",
                "Cytoplasm",
                "Plasma membrane",
                "Extracellular",
                "Integral to membrane",
                "Mitochondrion",
                "Endoplasmic reticulum",
                "Golgi apparatus",
                "Nucleolus" )

remain = total
for l in locals_main:
    if l in counter_dict:
        print counter_dict[l], l
        remain -= counter_dict[l]
    else:
        print 0, l

print remain, "Others"




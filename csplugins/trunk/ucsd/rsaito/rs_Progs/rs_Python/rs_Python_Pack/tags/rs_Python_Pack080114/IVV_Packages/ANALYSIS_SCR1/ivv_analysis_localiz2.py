#!/usr/bin/env python

import sys
sys.path.append("../")
import string

import Data_Struct.Hash
from IVV_info.IVV_filter import IVV_filter1
from IVV_info.IVV_info import IVV_info
import Data_Struct.MultiDimDict
import Usefuls.DictKeyIterator

ivv_info_file = "../../IVV/ivv_human7.3_info"
ivv_prey_filter = "../../IVV/basic_filter_list1"

localization_file = "prot_localiz"

loc_ct = Data_Struct.MultiDimDict.MultiDimDict(2, 0)

sys.stderr.write("IVV information...\n")
filter = IVV_filter1()
filter.set_Prey_filter_file(ivv_prey_filter)
ivv_info = IVV_info(ivv_info_file, filter) ### Switch

localization = Data_Struct.Hash.Hash("A")
localization.read_file(filename = localization_file,
		       Key_cols = [0], Val_cols = [1]);

prey_geneid_dict = {}

for prey in ivv_info.Prey_info().preys():
    bait = ivv_info.Prey_info().bait_ID(prey)
    prey_geneid = ivv_info.Prey_info().geneid(prey)

    if prey_geneid == "":
        continue

    bait_geneid = ivv_info.Bait_info().geneid(bait)

    if ivv_info.Bait_info().bait_type(bait) == "Bait": ### Switch

        localiz_bait = localization.val_force(bait_geneid)
        if len(localiz_bait) != 1 or localiz_bait[0] != "Nucleus":
            continue ### Switch

        prey_geneid_dict[ prey_geneid ] = ""

counter = Data_Struct.MultiDimDict.MultiDimDict(1, 0)
for prey_geneid in prey_geneid_dict:

    localiz_prey = localization.val_force(prey_geneid)
    if len(localiz_prey) != 1: continue

    counter.plus_val((localiz_prey[0],), 1)

#    print prey_geneid, localiz_prey[0]


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




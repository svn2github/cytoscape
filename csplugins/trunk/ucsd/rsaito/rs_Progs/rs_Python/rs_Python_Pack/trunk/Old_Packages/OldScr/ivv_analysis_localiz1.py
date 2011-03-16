#!/usr/bin/env python

import sys
sys.path.append("../")
import string

import Data_Struct.Hash
from IVV_info.IVV_filter import IVV_filter1
from IVV_info.IVV_info import IVV_info
import Usefuls.Usefuls1
import Usefuls.Table_maker
import Data_Struct.MultiDimDict
import Math.Matrix1

ivv_info_file = "../../IVV/ivv_human7.3_info"
ivv_prey_filter = "../../IVV/basic_filter_list1"

localization_file = "prot_localiz"

loc_ct = Data_Struct.MultiDimDict.MultiDimDict(2, 0)

sys.stderr.write("IVV information...\n")
filter = IVV_filter1()
filter.set_Prey_filter_file(ivv_prey_filter)
ivv_info = IVV_info(ivv_info_file, filter)

localization = Data_Struct.Hash.Hash("A")
localization.read_file(filename = localization_file,
		       Key_cols = [0], Val_cols = [1]);

output = Usefuls.Table_maker.Table_row()
for preyID in ivv_info.Prey_info().preys():

    output.append("Prey ID", preyID)

    prey_geneid = ivv_info.Prey_info().geneid(preyID)
    output.append("Prey geneid", prey_geneid)

    if prey_geneid == "": continue

    baitID = ivv_info.Prey_info().bait_ID(preyID)
    output.append("BaitID", baitID)

    if baitID == "Mock_Mock": continue
    if baitID == "Initial_Initial": continue

    bait_geneid = ivv_info.Bait_info().geneid(baitID)
    output.append("bait geneid", bait_geneid)

    localiz_prey = localization.val_force(prey_geneid)
    output.append("Prey localization", string.join(localiz_prey, ","))

    localiz_bait = localization.val_force(bait_geneid)
    output.append("Bait localization", string.join(localiz_bait, ","))

    localiz_common = Usefuls.Usefuls1.common(localiz_prey, localiz_bait)
    output.append("Common localization", string.join(localiz_common, ","))

    if len(localiz_prey) == 1 and len(localiz_bait) == 1:
        loc_ct.plus_val((localiz_prey[0], localiz_bait[0]), 1)

#    output.output("\t")

sma = Math.Matrix1.Matrix1()
sma.input_from_dict_Square(loc_ct.get_all_data())
print string.join(sma.get_label(), "\t")
for row in sma.get_matrix():
    print row

#!/usr/bin/env python

from SAT_Packages.SAT11K.Human_Cancer11k_Global import *
from SAT_Packages.SAT11K.Cancer11k_Gene_info1 \
    import Cancer11k_Gene_info

import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
from Calc_Packages.Math.StatsI import *

from Usefuls.ListList_Pick import ListList_Pick

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

""" Setting expression data type """

# cancer_keys = colon_cancer_keys
# normal_keys = colon_normal_keys
# expr_file   = rsc.Human11k_Cancer_Colon_random

cancer_keys = hepatic_cancer_keys
normal_keys = hepatic_normal_keys
expr_file   = rsc.Human11k_Cancer_Hepatic_random

use_key = cancer_keys

""" Setting expression data type (End) """

okay_expr_sheet = Cancer11k.Okay_Sheet(expr_file)

afas_exp_ll = []
for onc_id in okay_expr_sheet.conv_onc_afas.keys():
    afs_ids = okay_expr_sheet.conv_onc_afas.val(onc_id)
    afs_ids.sort()

    afas_exp = []
    for afs_id in afs_ids:

        exp_pat_antis_normal = okay_expr_sheet.get_data_accord_keys(
            afs_id, use_key)

        exp_ave = mean(exp_pat_antis_normal)
        afas_exp.append(exp_ave)

    afas_exp_ll.append(afas_exp)

print use_key

for i in range(5):
    exp_pos_data = ListList_Pick(afas_exp_ll, i)
    print i+1, mean(exp_pos_data), sem(exp_pos_data) # , len(exp_pos_data)

for i in (-1, -2, -3, -4, -5):
    exp_pos_data = ListList_Pick(afas_exp_ll, i)
    print i, mean(exp_pos_data), sem(exp_pos_data) # , len(exp_pos_data)


# print afas_exp_ll


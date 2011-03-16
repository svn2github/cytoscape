#!/usr/bin/env python

import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
import GNUplot.GNUplot_points2 as GNUplot
from Calc_Packages.Math.Vector1 import *
from Calc_Packages.Math.StatsI import *

from SAT_Packages.SAT11K.Human_Cancer11k_Global import *

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsAntis_Config")

""" Setting expression data type """

# cancer_keys = colon_cancer_keys
# normal_keys = colon_normal_keys
# expr_file   = rsc.Human11k_Cancer_Colon_dT

cancer_keys = hepatic_cancer_keys
normal_keys = hepatic_normal_keys
expr_file   = rsc.Human11k_Cancer_Hepatic_random

""" Setting expression data type (End) """


expr_sheet_cancer11k = Cancer11k.Okay_Sheet(expr_file)

print normal_keys
for afs_id in expr_sheet_cancer11k.conv_afas_onc.keys():
    onc_id = expr_sheet_cancer11k.conv_afas_onc.val_force(afs_id)

    exp_pat_sense_normal = expr_sheet_cancer11k.get_data_accord_keys(
        onc_id, normal_keys)
    exp_pat_antis_normal = expr_sheet_cancer11k.get_data_accord_keys(
        afs_id, normal_keys)

    print median(exp_pat_sense_normal), median(exp_pat_antis_normal)

print

print cancer_keys
for afs_id in expr_sheet_cancer11k.conv_afas_onc.keys():
    onc_id = expr_sheet_cancer11k.conv_afas_onc.val_force(afs_id)

    exp_pat_sense_cancer = expr_sheet_cancer11k.get_data_accord_keys(
        onc_id, cancer_keys)
    exp_pat_antis_cancer = expr_sheet_cancer11k.get_data_accord_keys(
        afs_id, cancer_keys)

    print median(exp_pat_sense_cancer), median(exp_pat_antis_cancer)


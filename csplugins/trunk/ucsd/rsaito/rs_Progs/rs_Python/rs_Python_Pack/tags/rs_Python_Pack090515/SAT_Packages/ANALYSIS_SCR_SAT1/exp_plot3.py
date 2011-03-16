#!/usr/bin/env python

import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
from Data_Struct.Plot2 import Plot
from Calc_Packages.Math.Vector1 import *
from Calc_Packages.Math.StatsI import *
from Calc_Packages.Math.Stats_OrderI import *

from SAT_Packages.SAT11K.Human_Cancer11k_Global import *

from SAT_Packages.SAT11K.OptParse_Cancer11k1 import OptParse_celltype_prim

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

pset = Plot()

""" Setting expression data type """

expr_file, normal_keys, cancer_keys, args_dummy = \
    OptParse_celltype_prim()

""" Setting expression data type (End) """

expr_sheet_cancer11k = Cancer11k.Okay_Sheet(expr_file)

for afs_id in expr_sheet_cancer11k.conv_afas_onc.keys():
    onc_id = expr_sheet_cancer11k.conv_afas_onc.val_force(afs_id)

    exp_pat_sense_normal = expr_sheet_cancer11k.get_data_accord_keys(
        onc_id, normal_keys)
    exp_pat_antis_normal = expr_sheet_cancer11k.get_data_accord_keys(
        afs_id, normal_keys)

    pset.add_point("Normal tissue",
                   (median(exp_pat_sense_normal),
                    median(exp_pat_antis_normal)))


for afs_id in expr_sheet_cancer11k.conv_afas_onc.keys():
    onc_id = expr_sheet_cancer11k.conv_afas_onc.val_force(afs_id)

    exp_pat_sense_cancer = expr_sheet_cancer11k.get_data_accord_keys(
        onc_id, cancer_keys)
    exp_pat_antis_cancer = expr_sheet_cancer11k.get_data_accord_keys(
        afs_id, cancer_keys)

    pset.add_point("Cancer tissue",
                   (median(exp_pat_sense_cancer),
                    median(exp_pat_antis_cancer)))

# pset.add_sd_cross("Normal tissue", "SD range for normal tissue", 100)
# pset.add_sd_cross("Cancer tissue", "SD range for cancer tissue", 100)

pset.add_md_cross("Normal tissue", "70% range for normal tissue", 0.15, 0.85)
pset.add_md_cross("Cancer tissue", "70% range for cancer tissue", 0.15, 0.85)

print "***** Data Summary *****"
print "Expression data file  :", expr_file
print "Keys for normal tissue:", normal_keys
print "Keys for cancer tissue:", cancer_keys
print "Average expression level of normal tissue   (sense):", pset.mean("Normal tissue")[0]
print "Average expression level of normal tissue   (antis):", pset.mean("Normal tissue")[1]
print "Average expression level of cancer tissue   (sense):", pset.mean("Cancer tissue")[0]
print "Average expression level of cancer tissue   (antis):", pset.mean("Cancer tissue")[1]
print "Median of expression level of normal tissue (sense):", pset.median("Normal tissue")[0]
print "Median of expression level of normal tissue (antis):", pset.median("Normal tissue")[1]
print "Median of expression level of cancer tissue (sense):", pset.median("Cancer tissue")[0]
print "Median of expression level of cancer tissue (antis):", pset.median("Cancer tissue")[1]
print "SD of expression level of normal tissue     (sense):", pset.sd_infer("Normal tissue")[0]
print "SD of expression level of normal tissue     (antis):", pset.sd_infer("Normal tissue")[1]
print "SD of expression level of cancer tissue     (sense):", pset.sd_infer("Cancer tissue")[0]
print "SD of expression level of cancer tissue     (antis):", pset.sd_infer("Cancer tissue")[1]
print "CC of expression level of SAT in normal tissue   :", pset.corr("Normal tissue")
print "CC of expression level of SAT in cancer tissue   :", pset.corr("Cancer tissue")

print

print "***** Plot *****"
# pset.output(("Normal tissue", "Cancer tissue",
#             "SD range for normal tissue",
#             "SD range for cancer tissue"))
pset.output(("Normal tissue", "Cancer tissue",
             "70% range for normal tissue",
             "70% range for cancer tissue"))

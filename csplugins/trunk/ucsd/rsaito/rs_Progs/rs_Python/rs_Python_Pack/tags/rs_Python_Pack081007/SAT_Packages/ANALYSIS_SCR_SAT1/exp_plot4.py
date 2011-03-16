#!/usr/bin/env python

import SAT_Packages.SAT11K.Human_Cancer11k3 as Cancer11k
from Data_Struct.Plot2 import Plot
from Calc_Packages.Math.Vector1 import *
from Calc_Packages.Math.StatsI import *
from Calc_Packages.Math.Stats_OrderI import *

from SAT_Packages.SAT11K.Human_Cancer11k_Global import *
from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kI

import SAT_Packages.SAT11K.Cancer11k_Gene_info1 as Gene_info

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

def change_calc(after, before):
    return math.log(after / before) / math.log(10.0)

pset = Plot(("Normal tissue", "Cancer tissue",
             "70% range for normal tissue",
             "70% range for cancer tissue"))

""" Setting expression data type """

opt = Option_Cancer11kI()
onco_tumor_selector = opt.get_misc1() # Selection of Oncogenes / Tumor suppressors

""" Setting expression data type (End) """

cancer11k = Cancer11k.Human_Cancer11k(opt)

cancer_gene_info = Gene_info.Cancer11k_Gene_info_OncTS(
    rsc.Human11k_Cancer_gene_info,
    rsc.Human11k_Cancer_category_info_func)

for afs_id in cancer11k.get_exp_sheet().conv_afas_onc.keys():
    (exp_pat_sense_normal,
     exp_pat_sense_cancer,
     exp_pat_antis_normal,
     exp_pat_antis_cancer) = cancer11k.get_four_exp_pat(afs_id)

    onc_id = cancer11k.get_exp_sheet().conv_afas_onc.val_force(afs_id)
    oncts = cancer_gene_info.get_OncTS_from_onc_id(onc_id)

    if onco_tumor_selector == "" or onco_tumor_selector == oncts:

        diff_sense = vector_pair(exp_pat_sense_cancer,
                                 exp_pat_sense_normal,
                                 change_calc)
        diff_antis = vector_pair(exp_pat_antis_cancer,
                                 exp_pat_antis_normal,
                                 change_calc)
  
        patient = get_centre_closer_to_median((diff_sense, diff_antis))[0]
        sense_normal = exp_pat_sense_normal[patient]
        sense_cancer = exp_pat_sense_cancer[patient]
        antis_normal = exp_pat_antis_normal[patient]
        antis_cancer = exp_pat_antis_cancer[patient]
        
        pset.add_point("Normal tissue",
                       (sense_normal, antis_normal))
    
        pset.add_point("Cancer tissue",
                       (sense_cancer, antis_cancer))

    # print afs_id, sense_normal, antis_normal, sense_cancer,antis_cancer
    
    

# pset.add_sd_cross("Normal tissue", "SD range for normal tissue", 100)
# pset.add_sd_cross("Cancer tissue", "SD range for cancer tissue", 100)

pset.add_md_cross("Normal tissue", "70% range for normal tissue", 0.15, 0.85)
pset.add_md_cross("Cancer tissue", "70% range for cancer tissue", 0.15, 0.85)

print "***** Data Summary *****"
print "Expression data file  :", opt.get_exp_file()
print "Keys for normal tissue:", opt.get_normal_keys()
print "Keys for cancer tissue:", opt.get_cancer_keys()
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
pset.output()

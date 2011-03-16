#!/usr/bin/env python

import sys

import SAT_Packages.SAT11K.Human_Cancer11k4 as Cancer11k
from SAT_Packages.SAT11K.Human_Cancer11k_Global import *
from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kI
import SAT_Packages.SAT11K.Cancer11k_Gene_info1 as Gene_info

from Calc_Packages.Math.StatsI import *
from Calc_Packages.Math.Stats_OrderI import *

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")


""" Setting expression data type """

opt = Option_Cancer11kI()
onco_tumor_selector = opt.get_misc1() # Selection of Oncogenes / Tumor suppressors

""" Setting expression data type (End) """

cancer11k = Cancer11k.Human_Cancer11k(opt, rsc.Human11k_Cancer_AFAS_ID_Conv)

cancer_gene_info = Gene_info.Cancer11k_Gene_info_OncTS(
    rsc.Human11k_Cancer_gene_info,
    rsc.Human11k_Cancer_category_info_func)

normal_sense_expS = []
cancer_sense_expS = []
normal_antis_expS = []
cancer_antis_expS = []

# for id in cancer11k.get_exp_sheet().row_labels():

onc_id_done = {}

for afs_id in cancer11k.get_exp_sheet().conv_afas_onc.keys():
    (exp_pat_sense_normal,
     exp_pat_sense_cancer,
     exp_pat_antis_normal,
     exp_pat_antis_cancer) = cancer11k.get_four_exp_pat(afs_id)

    onc_id = cancer11k.get_exp_sheet().conv_afas_onc.val_force(afs_id)
    oncts = cancer_gene_info.get_OncTS_from_onc_id(onc_id)
      
    # print id, oncts, cancer11k.get_exp_sheet().get_data_accord_keys(id, opt.get_normal_keys())
    if onco_tumor_selector == "" or onco_tumor_selector == oncts:
        if onc_id not in onc_id_done:
            normal_sense_expS.append(mean(exp_pat_sense_normal))
            cancer_sense_expS.append(mean(exp_pat_sense_cancer))
            onc_id_done[ onc_id ] = ""

        normal_antis_expS.append(mean(exp_pat_antis_normal))
        cancer_antis_expS.append(mean(exp_pat_antis_cancer))
    

print "***** Data Summary *****"
print "Expression data file  :", opt.get_exp_file()
print "Keys for normal tissue:", opt.get_normal_keys()
print "Keys for cancer tissue:", opt.get_cancer_keys()
print "Onco/TumorSup selector:", opt.get_misc1()
print "Number of sense data  :", len(normal_sense_expS) # , len(cancer_sense_expS)
print "Number of antis data  :", len(normal_antis_expS) # , len(cancer_antis_expS)
print 
print "Normal sense average  :", mean(normal_sense_expS)
print "Cancer sense average  :", mean(cancer_sense_expS)
print "Normal antis average  :", mean(normal_antis_expS)
print "Cancer antis average  :", mean(cancer_antis_expS)


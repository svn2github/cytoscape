#!/usr/bin/env python

import sys

import SAT_Packages.SAT11K.Human_Cancer11k4 as Cancer11k
from SAT_Packages.SAT11K.Human_Cancer11k_Global import *
from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kI
import SAT_Packages.SAT11K.Cancer11k_Gene_info1 as Gene_info

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

normal_sense_exp = []
cancer_sense_exp = []
normal_antis_exp = []
cancer_antis_exp = []

for id in cancer11k.get_exp_sheet().row_labels():
    
    try:
        if id.startswith("ONC-"):
            oncts = cancer_gene_info.get_OncTS_from_onc_id(id)  
            # print id, oncts, cancer11k.get_exp_sheet().get_data_accord_keys(id, opt.get_normal_keys())
            if onco_tumor_selector == "" or onco_tumor_selector == oncts:
                normal_exp = cancer11k.get_exp_sheet().get_data_accord_keys(id, opt.get_normal_keys())
                cancer_exp = cancer11k.get_exp_sheet().get_data_accord_keys(id, opt.get_cancer_keys())
                normal_sense_exp.append(1.0*sum(normal_exp)/len(normal_exp))
                cancer_sense_exp.append(1.0*sum(cancer_exp)/len(cancer_exp))
        
        elif id.startswith("AFAS-Onc-Anti-"):
            onc_id = cancer11k.get_exp_sheet().conv_afas_onc.val_force(id)
            oncts = cancer_gene_info.get_OncTS_from_onc_id(onc_id)
            # print id, oncts, cancer11k.get_exp_sheet().get_data_accord_keys(id, opt.get_normal_keys())
            if onco_tumor_selector == "" or onco_tumor_selector == oncts:
                normal_exp = cancer11k.get_exp_sheet().get_data_accord_keys(id, opt.get_normal_keys())
                cancer_exp = cancer11k.get_exp_sheet().get_data_accord_keys(id, opt.get_cancer_keys())
                normal_antis_exp.append(1.0*sum(normal_exp)/len(normal_exp))
                cancer_antis_exp.append(1.0*sum(cancer_exp)/len(cancer_exp))
            
    except Gene_info.Oncogene_ID_ERROR:
        sys.stderr.write(id + " or " + Gene_info.Oncogene_ID_ERROR.id + " not found.\n")
    

print "***** Data Summary *****"
print "Expression data file  :", opt.get_exp_file()
print "Keys for normal tissue:", opt.get_normal_keys()
print "Keys for cancer tissue:", opt.get_cancer_keys()
print "Onco/TumorSup selector:", opt.get_misc1()

print "Normal sense average  :", 1.0*sum(normal_sense_exp)/len(normal_sense_exp)
print "Cancer sense average  :", 1.0*sum(cancer_sense_exp)/len(cancer_sense_exp)
print "Normal antis average  :", 1.0*sum(normal_antis_exp)/len(normal_antis_exp)
print "Cancer antis average  :", 1.0*sum(cancer_antis_exp)/len(cancer_antis_exp)
#!/usr/bin/env python

import math
import sys
from Calc_Packages.Math.Vector1 import vector_pair
from Calc_Packages.Math.Stats_OrderI import median
from Calc_Packages.Math.StatsI import mean, corr
import SAT_Packages.SAT11K.Human_Cancer11k3 as Cancer11k
import SAT_Packages.SAT11K.Cancer11k_Gene_info1 as Gene_info

from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kI

from Expr_Packages.Clustering.Cluster_Eisen_frmt1 import Cluster_Eisen_frmt

import Usefuls.Table_maker

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

from SAT_Packages.SAT11K.Human_Cancer11k_Global import okay_marker_afas_colon, okay_marker_afas_hepatic

def log_ratio(after, before):
    return math.log(after / before) / math.log(10.0)

sys.stderr.write("Reading command line ...\n")
opt = Option_Cancer11kI()
normal_keys  = opt.get_normal_keys()
cancer_keys  = opt.get_cancer_keys()

sys.stderr.write("Reading Cancer 11K information ...\n")
cancer_gene_info = Gene_info.Cancer11k_Gene_info_OncTS(
    rsc.Human11k_Cancer_gene_info,
    rsc.Human11k_Cancer_category_info_func)

sys.stderr.write("Reading Cancer 11K data ...\n")
cancer_sheet    = Cancer11k.Human_Cancer11k(opt)

# output = Usefuls.Table_maker.Table_row()

output = Cluster_Eisen_frmt(normal_keys + cancer_keys + ["(Flag)"],
                            [ 1.0 ] * len(normal_keys + cancer_keys) + [ 0.0 ])

for afs_id in cancer_sheet.get_exp_sheet().conv_afas_onc.keys():

    onc_id = cancer_sheet.get_exp_sheet().conv_afas_onc.val_force(afs_id)
    annot = cancer_gene_info.get_annotation(onc_id)
    categ = ", ".join(cancer_gene_info.get_major_categ_descr(onc_id))
    oncts = cancer_gene_info.get_OncTS_from_onc_id(onc_id)

    (exp_pat_sense_normal,
     exp_pat_sense_cancer,
     exp_pat_antis_normal,
     exp_pat_antis_cancer) = \
     cancer_sheet.get_four_exp_pat(afs_id)

    sense_antis_ratio_normal = \
        vector_pair(exp_pat_sense_normal,
                    exp_pat_antis_normal,
                    log_ratio)

    sense_antis_ratio_cancer = \
        vector_pair(exp_pat_sense_cancer,
                    exp_pat_antis_cancer,
                    log_ratio)

    s_a_ratio_normal = median(sense_antis_ratio_normal)
    s_a_ratio_cancer = median(sense_antis_ratio_cancer)

    diff_sense_ratio = vector_pair(exp_pat_sense_cancer,
                                   exp_pat_sense_normal,
                                   log_ratio)
    diff_antis_ratio = vector_pair(exp_pat_antis_cancer,
                                   exp_pat_antis_normal,
                                   log_ratio)

    diff_ratio_s = median(diff_sense_ratio)
    diff_ratio_a = median(diff_antis_ratio)

    # print diff_ratio_s, diff_ratio_a, s_a_ratio_normal, s_a_ratio_cancer

    if (diff_ratio_s >=  0.176 and diff_ratio_a <= -0.176 and
        s_a_ratio_normal <= -0.176 and s_a_ratio_cancer >=  0.176):
        flag = +3.0
        
    elif (diff_ratio_s <= -0.176 and diff_ratio_a >=  0.176 and
          s_a_ratio_normal >=  0.176 and s_a_ratio_cancer <= -0.176):
        flag = -3.0
    else:
        flag = ""


    output.set_expression(afs_id,
                          sense_antis_ratio_normal + sense_antis_ratio_cancer + [ flag ],
                          annot)

    # output.append("UNIQID", afs_id)
    # output.append("NAME", annot)

    # for i in range(len(normal_keys)):
    #     output.append(normal_keys[i], "%.3f" % sense_antis_ratio_normal[i])
    # for i in range(len(cancer_keys)):
    #     output.append(cancer_keys[i], "%.3f" % sense_antis_ratio_cancer[i])

    # output.append("log ratio Sense/Antis N", "%.3f" % s_a_ratio_normal)
    # output.append("log ratio Sense/Antis C", "%.3f" % s_a_ratio_cancer)

    # output.output("\t")
    
output.display()


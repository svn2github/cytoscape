#!/usr/bin/env python

import math
import sys
from Calc_Packages.Math.Vector1 import vector_pair
from Calc_Packages.Math.Stats_OrderI import median
from Calc_Packages.Math.StatsI import mean, corr
import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
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
expr_file    = opt.get_exp_file()
expr_file    = opt.get_exp_file()
normal_keys  = opt.get_normal_keys()
cancer_keys  = opt.get_cancer_keys()

sys.stderr.write("Reading Cancer 11K information ...\n")
cancer_gene_info = Gene_info.Cancer11k_Gene_info_OncTS(
    rsc.Human11k_Cancer_gene_info,
    rsc.Human11k_Cancer_category_info_func)

sys.stderr.write("Reading Cancer 11K data ...\n")
cancer_sheet    = Cancer11k.Human_Cancer11k(opt)
okay_expr_sheet = cancer_sheet.get_exp_sheet()

# output = Usefuls.Table_maker.Table_row()

output = Cluster_Eisen_frmt(normal_keys + cancer_keys + ["(Blank)", "NMedianR", "CMedianR"],
                            [ 1.0 ] * len(normal_keys + cancer_keys) + [ 0.0, 0.0, 0.0 ])

for afs_id in okay_expr_sheet.conv_afas_onc.keys():

    onc_id = okay_expr_sheet.conv_afas_onc.val_force(afs_id)
    annot = cancer_gene_info.get_annotation(onc_id)
    categ = ", ".join(cancer_gene_info.get_major_categ_descr(onc_id))
    oncts = cancer_gene_info.get_OncTS_from_onc_id(onc_id)

    (exp_pat_sense_normal,
     exp_pat_sense_cancer,
     exp_pat_antis_normal,
     exp_pat_antis_cancer) = \
     cancer_sheet.get_four_exp_pat(afs_id,
                                   normal_keys,
                                   cancer_keys)

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

    output.set_expression(afs_id,
                          sense_antis_ratio_normal + sense_antis_ratio_cancer + [""] + 
                          [ s_a_ratio_normal, s_a_ratio_cancer ],
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


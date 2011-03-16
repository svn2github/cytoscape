#!/usr/bin/env python

""" This module integrates Multiple normal tissues (11K) and normal/cancer tissues (11K)
and calculates all the necessary information. """

""" Program check required. (min_change_select_patient_noII) """

import math
import sys
# from Calc_Packages.Math.Vector1 import vector_pair
from Calc_Packages.Stats.Stats_OrderI import median, ordering
from Calc_Packages.Stats.StatsI import mean, corr
import SAT_Packages.SAT11K.Read11k1 as Read11k # 11k of multiple normal tissues
import SAT_Packages.SAT11K.Human_Cancer11k3 as Cancer11k
import SAT_Packages.SAT11K.Cancer11k_Gene_info1 as Gene_info

from Expr_Packages.Expr_II.Transcript1 import Transcript_Factory

from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kII

import Usefuls.Table_maker

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

from SAT_Packages.SAT11K.Human_Cancer11k_Global import okay_marker_afas_colon, okay_marker_afas_hepatic


def log_ratio(after, before):
    return math.log(after / before) / math.log(10.0)


def min_change(exp_pat_sense_normal,
               exp_pat_sense_cancer,
               exp_pat_antis_normal,
               exp_pat_antis_cancer,
               mult = (+1.0, -1.0, -1.0, +1.0)):
    ret = []
    for i in range(len(exp_pat_sense_normal)):
        s_n = exp_pat_sense_normal[i]
        s_c = exp_pat_sense_cancer[i]
        a_n = exp_pat_antis_normal[i]
        a_c = exp_pat_antis_cancer[i]
        ret.append(min(log_ratio(s_c, s_n)*mult[0],
                       log_ratio(a_c, a_n)*mult[1],
                       log_ratio(s_n, a_n)*mult[2],
                       log_ratio(s_c, a_c)*mult[3]
                       ))
    return ret

def min_change_select_patient_no(exp_pat_sense_normal,
                                 exp_pat_sense_cancer,
                                 exp_pat_antis_normal,
                                 exp_pat_antis_cancer,
                                 select_patient_nth,
                                 mult = (+1.0, -1.0, -1.0, +1.0)):
    
    min_change_array = min_change(exp_pat_sense_normal,
                                  exp_pat_sense_cancer,
                                  exp_pat_antis_normal,
                                  exp_pat_antis_cancer,
                                  mult)
    min_change_array_order = ordering(min_change_array)
    min_change_array_order_sort = min_change_array_order[:]
    min_change_array_order_sort.sort()
    patient_rank = min_change_array_order_sort[-select_patient_nth]
    patient_no   = min_change_array_order.index(patient_rank) 
    
    return patient_no, min_change_array[patient_no]
                                               

def min_changeII(exp_pat_sense_normal,
                 exp_pat_sense_cancer,
                 exp_pat_antis_normal,
                 exp_pat_antis_cancer,
                 mult = (+1.0, -1.0, -1.0, +1.0)):
    ret_cn = []
    ret_sa = []
    for i in range(len(exp_pat_sense_normal)):
        s_n = exp_pat_sense_normal[i]
        s_c = exp_pat_sense_cancer[i]
        a_n = exp_pat_antis_normal[i]
        a_c = exp_pat_antis_cancer[i]
        ret_cn.append(min(log_ratio(s_c, s_n)*mult[0],
                          log_ratio(a_c, a_n)*mult[1]))
        ret_sa.append(min(log_ratio(s_n, a_n)*mult[2],
                          log_ratio(s_c, a_c)*mult[3]))
    return ret_cn, ret_sa

def min_change_select_patient_noII(exp_pat_sense_normal,
                                exp_pat_sense_cancer,
                                exp_pat_antis_normal,
                                exp_pat_antis_cancer,
                                select_patient_nth,
                                mult = (+1.0, -1.0, -1.0, +1.0)):
    
    (min_change_array_cn, min_change_array_sa) = \
         min_changeII(exp_pat_sense_normal,
                      exp_pat_sense_cancer,
                      exp_pat_antis_normal,
                      exp_pat_antis_cancer,
                      mult)
    min_change_array_order_cn = ordering(min_change_array_cn)
    min_change_array_order_cn_sort = min_change_array_order_cn[:]
    min_change_array_order_cn_sort.sort()
    patient_rank_cn = min_change_array_order_cn_sort[-select_patient_nth]
    patient_no_cn   = min_change_array_order_cn.index(patient_rank_cn) 
    
    min_change_array_order_sa = ordering(min_change_array_sa)
    min_change_array_order_sa_sort = min_change_array_order_sa[:]
    min_change_array_order_sa_sort.sort()
    patient_rank_sa = min_change_array_order_sa_sort[-select_patient_nth]
    patient_no_sa   = min_change_array_order_sa.index(patient_rank_sa) 
    
    return (patient_no_cn, min_change_array_cn[patient_no_cn],
            patient_no_sa, min_change_array_sa[patient_no_sa])

select_patient_nth = 3

sys.stderr.write("Reading command line ...\n")
opt = Option_Cancer11kII()

# 11k of multiple normal tissues
sys.stderr.write("Reading 11K of normal tissues ...\n")
expr_pat_set_dt, sat_set = Read11k.read_human11k_dT()
expr_pat_set_rd, sat_set = Read11k.read_human11k_random()

sys.stderr.write("Reading Cancer 11K information ...\n")
cancer_gene_info = Gene_info.Cancer11k_Gene_info_OncTS(
    rsc.Human11k_Cancer_gene_info,
    rsc.Human11k_Cancer_category_info_func)

sys.stderr.write("Reading Cancer 11K data ...\n")
cancer11k_dt, cancer11k_rd = Cancer11k.create_Human_Cancer11k_dt_rd(opt)

output = Usefuls.Table_maker.Table_row()

for afs_id in cancer11k_dt.get_exp_sheet().conv_afas_onc.keys():

    onc_id = cancer11k_dt.get_exp_sheet().conv_afas_onc.val_force(afs_id)

    """ *** Human 11k multiple normal tissues *** """
    transcr_sense = Transcript_Factory()[onc_id]
    transcr_antis = Transcript_Factory()[afs_id]
    probes_sense = transcr_sense.get_probes()
    probes_antis = transcr_antis.get_probes()

    if len(probes_sense) != 1:
        raise "Number of probes is not 1 for " + onc_id
    if len(probes_antis) != 1:
        raise "Number of probes is not 1 for " + afs_id

    exppat_sense_dt = \
        expr_pat_set_dt.expression_pat(Transcript_Factory()[onc_id].get_probes()[0])
    exppat_antis_dt = \
        expr_pat_set_dt.expression_pat(Transcript_Factory()[afs_id].get_probes()[0])

    exppat_sense_rd = \
        expr_pat_set_rd.expression_pat(Transcript_Factory()[onc_id].get_probes()[0])
    exppat_antis_rd = \
        expr_pat_set_rd.expression_pat(Transcript_Factory()[afs_id].get_probes()[0])

    exppat_cond_dt = expr_pat_set_dt.conditions()
    exppat_cond_rd = expr_pat_set_rd.conditions()

    dt_corr = corr(exppat_sense_dt, exppat_antis_dt)
    rd_corr = corr(exppat_sense_rd, exppat_antis_rd)

    """ *** Human 11k multiple normal tissues (End) *** """
    
    """ *** Human 11k cancer tissues *** """
    
    annot = cancer_gene_info.get_annotation(onc_id)
    categ = ", ".join(cancer_gene_info.get_major_categ_descr(onc_id))
    oncts = cancer_gene_info.get_OncTS_from_onc_id(onc_id)

    (exp_pat_dt_sense_normal,
     exp_pat_dt_sense_cancer,
     exp_pat_dt_antis_normal,
     exp_pat_dt_antis_cancer) = \
     cancer11k_dt.get_four_exp_pat(afs_id)

    (exp_pat_rd_sense_normal,
     exp_pat_rd_sense_cancer,
     exp_pat_rd_antis_normal,
     exp_pat_rd_antis_cancer) = \
     cancer11k_rd.get_four_exp_pat(afs_id)

    """ Calculates dT expressions """
    
    # diff_dt_sense = vector_pair(exp_pat_dt_sense_cancer,
    #                             exp_pat_dt_sense_normal,
    #                             log_ratio)
    # diff_dt_antis = vector_pair(exp_pat_dt_antis_cancer,
    #                             exp_pat_dt_antis_normal,
    #                             log_ratio)
    
    # sense_antis_dt_ratio_normal = \
    #     vector_pair(exp_pat_dt_sense_normal,
    #                 exp_pat_dt_antis_normal,
    #                 log_ratio)

    # sense_antis_dt_ratio_cancer = \
    #     vector_pair(exp_pat_dt_sense_cancer,
    #                 exp_pat_dt_antis_cancer,
    #                 log_ratio)
    
    patient_cn_dt, min_change_cn_dt, patient_sa_dt, min_change_sa_dt = \
        min_change_select_patient_noII(exp_pat_dt_sense_normal,
                                       exp_pat_dt_sense_cancer,
                                       exp_pat_dt_antis_normal,
                                       exp_pat_dt_antis_cancer,
                                       select_patient_nth)
    
    selected_patient_cn_dt_key = ",".join((opt.get_normal_keys()[patient_cn_dt],
                                           opt.get_cancer_keys()[patient_cn_dt]))
    selected_patient_sa_dt_key = ",".join((opt.get_normal_keys()[patient_sa_dt],
                                           opt.get_cancer_keys()[patient_sa_dt]))

    # diff_dt_s = diff_dt_sense[ patient_cn_dt ]
    # diff_dt_a = diff_dt_antis[ patient_cn_dt ]

    # diff_dt_s = median(diff_dt_sense)
    # diff_dt_a = median(diff_dt_antis)

    # s_a_dt_ratio_normal = sense_antis_dt_ratio_normal[ patient_sa_dt ]
    # s_a_dt_ratio_cancer = sense_antis_dt_ratio_cancer[ patient_sa_dt ]

    # s_a_dt_ratio_normal = median(sense_antis_dt_ratio_normal)
    # s_a_dt_ratio_cancer = median(sense_antis_dt_ratio_cancer)


    """ Calculates Rd expressions """

    # diff_rd_sense = vector_pair(exp_pat_rd_sense_cancer,
    #                             exp_pat_rd_sense_normal,
    #                             log_ratio)
    # diff_rd_antis = vector_pair(exp_pat_rd_antis_cancer,
    #                             exp_pat_rd_antis_normal,
    #                             log_ratio)
    
    # sense_antis_rd_ratio_normal = \
    #     vector_pair(exp_pat_rd_sense_normal,
    #                 exp_pat_rd_antis_normal,
    #                 log_ratio)

    # sense_antis_rd_ratio_cancer = \
    #     vector_pair(exp_pat_rd_sense_cancer,
    #                 exp_pat_rd_antis_cancer,
    #                 log_ratio)
    
    patient_cn_rd, min_change_cn_rd, patient_sa_rd, min_change_sa_rd = \
        min_change_select_patient_noII(exp_pat_rd_sense_normal,
                                       exp_pat_rd_sense_cancer,
                                       exp_pat_rd_antis_normal,
                                       exp_pat_rd_antis_cancer,
                                       select_patient_nth)  
    
    selected_patient_cn_rd_key = ",".join((opt.get_normal_keys()[patient_cn_rd],
                                           opt.get_cancer_keys()[patient_cn_rd]))
    selected_patient_sa_rd_key = ",".join((opt.get_normal_keys()[patient_sa_rd],
                                           opt.get_cancer_keys()[patient_sa_rd]))

    # diff_rd_s = diff_rd_sense[ patient_rd ]
    # diff_rd_a = diff_rd_antis[ patient_rd ]
    

    # diff_rd_s = median(diff_rd_sense)
    # diff_rd_a = median(diff_rd_antis)

    # s_a_rd_ratio_normal = sense_antis_rd_ratio_normal[ patient_rd ]
    # s_a_rd_ratio_cancer = sense_antis_rd_ratio_cancer[ patient_rd ]

    # s_a_rd_ratio_normal = median(sense_antis_rd_ratio_normal)
    # s_a_rd_ratio_cancer = median(sense_antis_rd_ratio_cancer)

    
    """ Other information """
    
    mean_exp_pat_dt_sense_normal = mean(exp_pat_dt_sense_normal)
    mean_exp_pat_dt_sense_cancer = mean(exp_pat_dt_sense_cancer)
    mean_exp_pat_dt_antis_normal = mean(exp_pat_dt_antis_normal)
    mean_exp_pat_dt_antis_cancer = mean(exp_pat_dt_antis_cancer)
    mean_exp_pat_rd_sense_normal = mean(exp_pat_rd_sense_normal)
    mean_exp_pat_rd_sense_cancer = mean(exp_pat_rd_sense_cancer)
    mean_exp_pat_rd_antis_normal = mean(exp_pat_rd_antis_normal)
    mean_exp_pat_rd_antis_cancer = mean(exp_pat_rd_antis_cancer)

    median_exp_pat_dt_sense_normal = median(exp_pat_dt_sense_normal)
    median_exp_pat_dt_sense_cancer = median(exp_pat_dt_sense_cancer)
    median_exp_pat_dt_antis_normal = median(exp_pat_dt_antis_normal)
    median_exp_pat_dt_antis_cancer = median(exp_pat_dt_antis_cancer)
    median_exp_pat_rd_sense_normal = median(exp_pat_rd_sense_normal)
    median_exp_pat_rd_sense_cancer = median(exp_pat_rd_sense_cancer)
    median_exp_pat_rd_antis_normal = median(exp_pat_rd_antis_normal)
    median_exp_pat_rd_antis_cancer = median(exp_pat_rd_antis_cancer)
    
    selected_exp_pat_cn_dt_sense_normal = exp_pat_dt_sense_normal[ patient_cn_dt ]
    selected_exp_pat_cn_dt_sense_cancer = exp_pat_dt_sense_cancer[ patient_cn_dt ]
    selected_exp_pat_cn_dt_antis_normal = exp_pat_dt_antis_normal[ patient_cn_dt ]
    selected_exp_pat_cn_dt_antis_cancer = exp_pat_dt_antis_cancer[ patient_cn_dt ]
    selected_exp_pat_cn_rd_sense_normal = exp_pat_rd_sense_normal[ patient_cn_rd ]
    selected_exp_pat_cn_rd_sense_cancer = exp_pat_rd_sense_cancer[ patient_cn_rd ]
    selected_exp_pat_cn_rd_antis_normal = exp_pat_rd_antis_normal[ patient_cn_rd ]
    selected_exp_pat_cn_rd_antis_cancer = exp_pat_rd_antis_cancer[ patient_cn_rd ]

    selected_exp_pat_sa_dt_sense_normal = exp_pat_dt_sense_normal[ patient_sa_dt ]
    selected_exp_pat_sa_dt_sense_cancer = exp_pat_dt_sense_cancer[ patient_sa_dt ]
    selected_exp_pat_sa_dt_antis_normal = exp_pat_dt_antis_normal[ patient_sa_dt ]
    selected_exp_pat_sa_dt_antis_cancer = exp_pat_dt_antis_cancer[ patient_sa_dt ]
    selected_exp_pat_sa_rd_sense_normal = exp_pat_rd_sense_normal[ patient_sa_rd ]
    selected_exp_pat_sa_rd_sense_cancer = exp_pat_rd_sense_cancer[ patient_sa_rd ]
    selected_exp_pat_sa_rd_antis_normal = exp_pat_rd_antis_normal[ patient_sa_rd ]
    selected_exp_pat_sa_rd_antis_cancer = exp_pat_rd_antis_cancer[ patient_sa_rd ]    



    if afs_id in okay_marker_afas_colon:
        okay_mark_colon = "*"
    else:
        okay_mark_colon = ""

    if afs_id in okay_marker_afas_hepatic:
        okay_mark_hepatic = "*"
    else:
        okay_mark_hepatic = ""

    output.append("ONC ID",  onc_id)
    output.append("AFAS ID", afs_id)
    output.append("Annotation", annot)
    output.append("Categories", categ)
    output.append("Oncogenes / Tumor suppressors", oncts)

    output.append("Mean dT sense normal",
                  "%.3f" % mean_exp_pat_dt_sense_normal)
    output.append("Mean dT antis normal",
                  "%.3f" % mean_exp_pat_dt_antis_normal)
    output.append("Mean dT sense cancer",
                  "%.3f" % mean_exp_pat_dt_sense_cancer)
    output.append("Mean dT antis cancer",
                  "%.3f" % mean_exp_pat_dt_antis_cancer)
    output.append("Mean rd sense normal",
                  "%.3f" % mean_exp_pat_rd_sense_normal)
    output.append("Mean rd antis normal",
                  "%.3f" % mean_exp_pat_rd_antis_normal)
    output.append("Mean rd sense cancer",
                  "%.3f" % mean_exp_pat_rd_sense_cancer)
    output.append("Mean rd antis cancer",
                  "%.3f" % mean_exp_pat_rd_antis_cancer)

    output.append("Median dT sense normal",
                  "%.3f" % median_exp_pat_dt_sense_normal)
    output.append("Median dT antis normal",
                  "%.3f" % median_exp_pat_dt_antis_normal)
    output.append("Median dT sense cancer",
                  "%.3f" % median_exp_pat_dt_sense_cancer)
    output.append("Median dT antis cancer",
                  "%.3f" % median_exp_pat_dt_antis_cancer)
    output.append("Median rd sense normal",
                  "%.3f" % median_exp_pat_rd_sense_normal)
    output.append("Median rd antis normal",
                  "%.3f" % median_exp_pat_rd_antis_normal)
    output.append("Median rd sense cancer",
                  "%.3f" % median_exp_pat_rd_sense_cancer)
    output.append("Median rd antis cancer",
                  "%.3f" % median_exp_pat_rd_antis_cancer)

    output.append("Selected dT sense normal (C/N)",
                  "%.3f" % selected_exp_pat_cn_dt_sense_normal)
    output.append("Selected dT antis normal (C/N)",
                  "%.3f" % selected_exp_pat_cn_dt_antis_normal)
    output.append("Selected dT sense cancer (C/N)",
                  "%.3f" % selected_exp_pat_cn_dt_sense_cancer)
    output.append("Selected dT antis cancer (C/N)",
                  "%.3f" % selected_exp_pat_cn_dt_antis_cancer)
    output.append("Selected rd sense normal (C/N)",
                  "%.3f" % selected_exp_pat_cn_rd_sense_normal)
    output.append("Selected rd antis normal (C/N)",
                  "%.3f" % selected_exp_pat_cn_rd_antis_normal)
    output.append("Selected rd sense cancer (C/N)",
                  "%.3f" % selected_exp_pat_cn_rd_sense_cancer)
    output.append("Selected rd antis cancer (C/N)",
                  "%.3f" % selected_exp_pat_cn_rd_antis_cancer)
    
    output.append("Selected dT sense normal (S/A)",
                  "%.3f" % selected_exp_pat_sa_dt_sense_normal)
    output.append("Selected dT antis normal (S/A)",
                  "%.3f" % selected_exp_pat_sa_dt_antis_normal)
    output.append("Selected dT sense cancer (S/A)",
                  "%.3f" % selected_exp_pat_sa_dt_sense_cancer)
    output.append("Selected dT antis cancer (S/A)",
                  "%.3f" % selected_exp_pat_sa_dt_antis_cancer)
    output.append("Selected rd sense normal (S/A)",
                  "%.3f" % selected_exp_pat_sa_rd_sense_normal)
    output.append("Selected rd antis normal (S/A)",
                  "%.3f" % selected_exp_pat_sa_rd_antis_normal)
    output.append("Selected rd sense cancer (S/A)",
                  "%.3f" % selected_exp_pat_sa_rd_sense_cancer)
    output.append("Selected rd antis cancer (S/A)",
                  "%.3f" % selected_exp_pat_sa_rd_antis_cancer)
       
    output.append("Selected patient dt key (C/N)", selected_patient_cn_dt_key) 
    output.append("Selected patient rd key (C/N)", selected_patient_cn_rd_key)

    output.append("Selected patient dt key (S/A)", selected_patient_sa_dt_key) 
    output.append("Selected patient rd key (S/A)", selected_patient_sa_rd_key)

    # output.append("dt log ratio Cancer/Normal S", "%.3f" % diff_dt_s)
    # output.append("dt log ratio Cancer/Normal A", "%.3f" % diff_dt_a)
    # output.append("rd log ratio Cancer/Normal S", "%.3f" % diff_rd_s)
    # output.append("rd log ratio Cancer/Normal A", "%.3f" % diff_rd_a)
    # output.append("dt log ratio Sense/Antis N", "%.3f" % s_a_dt_ratio_normal)
    # output.append("dt log ratio Sense/Antis C", "%.3f" % s_a_dt_ratio_cancer)
    # output.append("rd log ratio Sense/Antis N", "%.3f" % s_a_rd_ratio_normal)
    # output.append("rd log ratio Sense/Antis C", "%.3f" % s_a_rd_ratio_cancer)
    
    output.append("Selected change dT (C/N)", "%.3f" % min_change_cn_dt)
    output.append("Selected change rd (C/N)", "%.3f" % min_change_cn_rd)
    output.append("Selected change dT (S/A)", "%.3f" % min_change_sa_dt)
    output.append("Selected change rd (S/A)", "%.3f" % min_change_sa_rd)

    output.append("Okay Mark Colon",   okay_mark_colon)
    output.append("Okay Mark Hepatic", okay_mark_hepatic)

    output.append("Average dT-S", "%.3lf" % mean(exppat_sense_dt))
    output.append("Average dT-A", "%.3lf" % mean(exppat_antis_dt))
    output.append("Average Rd-S", "%.3lf" % mean(exppat_sense_rd))
    output.append("Average Rd-A", "%.3lf" % mean(exppat_antis_rd))

    output.append("Multiple normal corr dT", "%.3lf" % dt_corr)
    output.append("Multiple normal corr rd", "%.3lf" % rd_corr)    


    for i in range(len(opt.get_normal_keys())):
        output.append("dT-S:" + opt.get_normal_keys()[i],
                      "%.1f" % exp_pat_dt_sense_normal[i])

    for i in range(len(opt.get_normal_keys())):
        output.append("dT-A:" + opt.get_normal_keys()[i],
                      "%.1f" % exp_pat_dt_antis_normal[i])

    for i in range(len(opt.get_cancer_keys())):
        output.append("dT-S:" + opt.get_cancer_keys()[i],
                      "%.1f" % exp_pat_dt_sense_cancer[i])

    for i in range(len(opt.get_cancer_keys())):
        output.append("dT-A:" + opt.get_cancer_keys()[i],
                      "%.1f" % exp_pat_dt_antis_cancer[i])

    for i in range(len(opt.get_normal_keys())):
        output.append("Rd-S:" + opt.get_normal_keys()[i],
                      "%.1f" % exp_pat_rd_sense_normal[i])

    for i in range(len(opt.get_normal_keys())):
        output.append("Rd-A:" + opt.get_normal_keys()[i],
                      "%.1f" % exp_pat_rd_antis_normal[i])

    for i in range(len(opt.get_cancer_keys())):
        output.append("Rd-S:" + opt.get_cancer_keys()[i],
                      "%.1f" % exp_pat_rd_sense_cancer[i])

    for i in range(len(opt.get_cancer_keys())):
        output.append("Rd-A:" + opt.get_cancer_keys()[i],
                      "%.1f" % exp_pat_rd_antis_cancer[i])

    for i in range(len(exppat_cond_dt)):
        output.append("dT-S:" + exppat_cond_dt[i],
                      "%.1f" % exppat_sense_dt[i])
    for i in range(len(exppat_cond_dt)):
        output.append("dT-A:" + exppat_cond_dt[i],
                      "%.1f" % exppat_antis_dt[i])
    for i in range(len(exppat_cond_dt)):
        output.append("Rd-S:" + exppat_cond_rd[i],
                      "%.1f" % exppat_sense_rd[i])
    for i in range(len(exppat_cond_dt)):
        output.append("Rd-A:" + exppat_cond_rd[i],
                      "%.1f" % exppat_antis_rd[i])

    output.output("\t")

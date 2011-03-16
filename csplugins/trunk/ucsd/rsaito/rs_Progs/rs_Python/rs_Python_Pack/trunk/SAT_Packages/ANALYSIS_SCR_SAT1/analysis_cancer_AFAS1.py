#!/usr/bin/env python

import math
from Calc_Packages.Math.Vector1 import *
from Calc_Packages.Math.StatsI import *
import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
import SAT_Packages.SAT11K.GNUplot_Cancer11k1 as GNUplot
import SAT_Packages.SAT.SAT_gnuplot1 as SAT_gnuplot
import SAT_Packages.SAT11K.Cancer11k_Gene_info1 as Gene_info

from SAT_Packages.SAT11K.OptParse_Cancer11k1 import OptParse_celltype_prim_II

import Usefuls.Table_maker

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

from SAT_Packages.SAT11K.Human_Cancer11k_Global import *

expr_file_dt, expr_file_rd, normal_keys, cancer_keys, args_dummy = \
    OptParse_celltype_prim_II()

cancer_gene_info = Gene_info.Cancer11k_Gene_info(
    rsc.Human11k_Cancer_gene_info,
    rsc.Human11k_Cancer_category_info_func)

def log_ratio(after, before):
    return math.log(after / before) / math.log(10.0)

okay_expr_sheet_dt = Cancer11k.Okay_Sheet(expr_file_dt)
okay_expr_sheet_rd = Cancer11k.Okay_Sheet(expr_file_rd)

output = Usefuls.Table_maker.Table_row()

for afs_id in okay_expr_sheet_dt.conv_afas_onc.keys():

    onc_id = okay_expr_sheet_dt.conv_afas_onc.val_force(afs_id)

    annot = cancer_gene_info.get_annotation(onc_id)
    categ = ", ".join(cancer_gene_info.get_major_categ_descr(onc_id))

    (exp_pat_dt_sense_normal,
     exp_pat_dt_sense_cancer,
     exp_pat_dt_antis_normal,
     exp_pat_dt_antis_cancer) = \
     okay_expr_sheet_dt.get_four_exp_pat(afs_id,
                                         normal_keys,
                                         cancer_keys)

    (exp_pat_rd_sense_normal,
     exp_pat_rd_sense_cancer,
     exp_pat_rd_antis_normal,
     exp_pat_rd_antis_cancer) = \
     okay_expr_sheet_rd.get_four_exp_pat(afs_id,
                                         normal_keys,
                                         cancer_keys)

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

    diff_dt_sense = vector_pair(exp_pat_dt_sense_cancer,
                                exp_pat_dt_sense_normal,
                                log_ratio)
    diff_dt_antis = vector_pair(exp_pat_dt_antis_cancer,
                                exp_pat_dt_antis_normal,
                                log_ratio)

    diff_dt_s = median(diff_dt_sense)
    diff_dt_a = median(diff_dt_antis)

    sense_antis_dt_ratio_normal = \
        vector_pair(exp_pat_dt_sense_normal,
                    exp_pat_dt_antis_normal,
                    log_ratio)

    sense_antis_dt_ratio_cancer = \
        vector_pair(exp_pat_dt_sense_cancer,
                    exp_pat_dt_antis_cancer,
                    log_ratio)

    s_a_dt_ratio_normal = median(sense_antis_dt_ratio_normal)
    s_a_dt_ratio_cancer = median(sense_antis_dt_ratio_cancer)

    diff_rd_sense = vector_pair(exp_pat_rd_sense_cancer,
                                exp_pat_rd_sense_normal,
                                log_ratio)
    diff_rd_antis = vector_pair(exp_pat_rd_antis_cancer,
                                exp_pat_rd_antis_normal,
                                log_ratio)

    diff_rd_s = median(diff_rd_sense)
    diff_rd_a = median(diff_rd_antis)

    sense_antis_rd_ratio_normal = \
        vector_pair(exp_pat_rd_sense_normal,
                    exp_pat_rd_antis_normal,
                    log_ratio)

    sense_antis_rd_ratio_cancer = \
        vector_pair(exp_pat_rd_sense_cancer,
                    exp_pat_rd_antis_cancer,
                    log_ratio)

    s_a_rd_ratio_normal = median(sense_antis_rd_ratio_normal)
    s_a_rd_ratio_cancer = median(sense_antis_rd_ratio_cancer)

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

    for i in range(len(normal_keys)):
        output.append("dT-S:" + normal_keys[i],
                      "%.1f" % exp_pat_dt_sense_normal[i])

    for i in range(len(normal_keys)):
        output.append("dT-A:" + normal_keys[i],
                      "%.1f" % exp_pat_dt_antis_normal[i])

    for i in range(len(cancer_keys)):
        output.append("dT-S:" + cancer_keys[i],
                      "%.1f" % exp_pat_dt_sense_cancer[i])

    for i in range(len(cancer_keys)):
        output.append("dT-A:" + cancer_keys[i],
                      "%.1f" % exp_pat_dt_antis_cancer[i])

    for i in range(len(normal_keys)):
        output.append("Rd-S:" + normal_keys[i],
                      "%.1f" % exp_pat_rd_sense_normal[i])

    for i in range(len(normal_keys)):
        output.append("Rd-A:" + normal_keys[i],
                      "%.1f" % exp_pat_rd_antis_normal[i])

    for i in range(len(cancer_keys)):
        output.append("Rd-S:" + cancer_keys[i],
                      "%.1f" % exp_pat_rd_sense_cancer[i])

    for i in range(len(cancer_keys)):
        output.append("Rd-A:" + cancer_keys[i],
                      "%.1f" % exp_pat_rd_antis_cancer[i])

    output.append("dt log ratio Cancer/Normal S", "%.3f" % diff_dt_s)
    output.append("dt log ratio Cancer/Normal A", "%.3f" % diff_dt_a)
    output.append("rd log ratio Cancer/Normal S", "%.3f" % diff_rd_s)
    output.append("rd log ratio Cancer/Normal A", "%.3f" % diff_rd_a)
    output.append("dt log ratio Sense/Antis N", "%.3f" % s_a_dt_ratio_normal)
    output.append("dt log ratio Sense/Antis C", "%.3f" % s_a_dt_ratio_cancer)
    output.append("rd log ratio Sense/Antis N", "%.3f" % s_a_rd_ratio_normal)
    output.append("rd log ratio Sense/Antis C", "%.3f" % s_a_rd_ratio_cancer)

    output.append("Okay Mark Colon",   okay_mark_colon)
    output.append("Okay Mark Hepatic", okay_mark_hepatic)

    output.output("\t")

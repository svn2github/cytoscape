#!/usr/bin/env python

import math
import Data_Struct.Plot2
import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
import GNUplot.GNUplot_points2 as GNUplot
from Calc_Packages.Math.Vector1 import *
from Calc_Packages.Math.StatsI import *
from Calc_Packages.Math.Stats_OrderI import *

from Usefuls.ListProc1 import common
from Usefuls.Adjust_to_thres import adjust_to_lower_thres_list
from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

def Plot_Cancer11k_OncTS(expr_sheet_cancer11k,
                         col_keys_normal,
                         col_keys_cancer,
                         cancer_gene_info,
                         miscs):

    def change_calc(after, before):
        return math.log(after / before) / math.log(10.0)

    flag, expr_thres, count_thres = miscs[:3]

    if expr_thres:
        expr_thres = float(expr_thres)
    else:
        expr_thres = 5.0
    if count_thres:
        count_thres = float(count_thres)
    else:
        count_thres = 0.0

    pset = Data_Struct.Plot2.Plot(("General cancer related genes",
                 "Oncogenes", "Tumor suppressors"))

    for afs_id in expr_sheet_cancer11k.conv_afas_onc.keys():
        onc_id = expr_sheet_cancer11k.conv_afas_onc.val_force(afs_id)
        categ  = cancer_gene_info.get_OncTS_from_onc_id(onc_id)
        descr = { "O": "Oncogenes", "S": "Tumor suppressors" }.\
            get(categ, "General cancer related genes")

        exp_pat_sense_normal = expr_sheet_cancer11k.get_data_accord_keys(
            onc_id, col_keys_normal)
        exp_pat_sense_cancer = expr_sheet_cancer11k.get_data_accord_keys(
            onc_id, col_keys_cancer)
        exp_pat_antis_normal = expr_sheet_cancer11k.get_data_accord_keys(
            afs_id, col_keys_normal)
        exp_pat_antis_cancer = expr_sheet_cancer11k.get_data_accord_keys(
            afs_id, col_keys_cancer)

        exp_pat_sense_normal_adjusted = \
            adjust_to_lower_thres_list(exp_pat_sense_normal, expr_thres)
        exp_pat_sense_cancer_adjusted = \
            adjust_to_lower_thres_list(exp_pat_sense_cancer, expr_thres)
        exp_pat_antis_normal_adjusted = \
            adjust_to_lower_thres_list(exp_pat_antis_normal, expr_thres)
        exp_pat_antis_cancer_adjusted = \
            adjust_to_lower_thres_list(exp_pat_antis_cancer, expr_thres)

        diff_sense = vector_pair(exp_pat_sense_cancer_adjusted,
                                 exp_pat_sense_normal_adjusted,
                                 change_calc)
        diff_antis = vector_pair(exp_pat_antis_cancer_adjusted,
                                 exp_pat_antis_normal_adjusted,
                                 change_calc)

        x = median(diff_sense)
        y = median(diff_antis)

        pset.add_point(descr, [ x, y ])

    if not flag:
        pset.output()
    else:
        print "Sense under threshold (Expression decreased in cancer cell)"
        pset.conditional_count_x(lambda x: x < count_thres).output(reverse = True)
        print
        print "Sense over threshold (Expression increased in cancer cell)"
        pset.conditional_count_x(lambda x: x > count_thres).output(reverse = True)
        print
        print "Antisense under threshold (Expression decreased in cancer cell)"
        pset.conditional_count_y(lambda y: y < count_thres).output(reverse = True)
        print
        print "Antisense over threshold (Expression increased in cancer cell)"
        pset.conditional_count_y(lambda y: y > count_thres).output(reverse = True)    


if __name__ == "__main__":

    from SAT_Packages.SAT11K.Human_Cancer11k_Global import *
    from SAT_Packages.SAT11K.Cancer11k_Gene_info1 \
        import Cancer11k_Gene_info_OncTS

    from SAT_Packages.SAT11K.OptParse_Cancer11k1 import OptParse_celltype_prim_misc

    expr_file, normal_keys, cancer_keys, miscs, args_dummy = \
        OptParse_celltype_prim_misc()
    # print expr_file, normal_keys, cancer_keys

    okay_expr_sheet = Cancer11k.Okay_Sheet(expr_file)

    cancer_gene_info = Cancer11k_Gene_info_OncTS(
        rsc.Human11k_Cancer_gene_info,
        rsc.Human11k_Cancer_category_info_func)

    Plot_Cancer11k_OncTS(okay_expr_sheet,
                         normal_keys,
                         cancer_keys,
                         cancer_gene_info,
                         miscs)

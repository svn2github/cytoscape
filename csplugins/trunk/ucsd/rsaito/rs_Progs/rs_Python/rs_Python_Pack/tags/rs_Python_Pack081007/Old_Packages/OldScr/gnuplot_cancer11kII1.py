#!/usr/bin/env python

import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
import GNUplot.GNUplot_points2 as GNUplot
from Calc_Packages.Math.Vector1 import *
from Calc_Packages.Math.StatsI import *

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsAntis_Config")

def GNUplot_Cancer11kII(expr_sheet_cancer11k,
                        col_keys_normal,
                        col_keys_cancer,
                        classification):
    """ classification is dictionary where
    classification[ onc_id ] = class_name """

    pset = GNUplot.Points_Set()

    for afs_id in expr_sheet_cancer11k.conv_afas_onc.keys():
        onc_id = expr_sheet_cancer11k.conv_afas_onc.val_force(afs_id)
        if onc_id in classification:
            class_name = classification[ onc_id ]
        else:
            class_name = "Others"

        exp_pat_sense_normal = expr_sheet_cancer11k.get_data_accord_keys(
            onc_id, col_keys_normal)
        exp_pat_sense_cancer = expr_sheet_cancer11k.get_data_accord_keys(
            onc_id, col_keys_cancer)
        exp_pat_antis_normal = expr_sheet_cancer11k.get_data_accord_keys(
            afs_id, col_keys_normal)
        exp_pat_antis_cancer = expr_sheet_cancer11k.get_data_accord_keys(
            afs_id, col_keys_cancer)

        diff_sense = vector_diff(exp_pat_sense_cancer,
                                 exp_pat_sense_normal)
        diff_antis = vector_diff(exp_pat_antis_cancer,
                                 exp_pat_antis_normal)
        x = median(diff_sense)
        y = median(diff_antis)
        pset.add_point(class_name, [ x, y ])

    GNUplot.GNUplot_points(pset).gnuplot()


if __name__ == "__main__":

    from SAT_Packages.SAT11K.Human_Cancer11k_Global import *

    """ Setting expression data type """

    # cancer_keys = colon_cancer_keys
    # normal_keys = colon_normal_keys
    # expr_file   = rsc.Human11k_Cancer_Colon_random

    cancer_keys = hepatic_cancer_keys
    normal_keys = hepatic_normal_keys
    expr_file   = rsc.Human11k_Cancer_Hepatic_random

    """ Setting expression data type (End) """

    okay_expr_sheet = Cancer11k.Okay_Sheet(expr_file)

    GNUplot_Cancer11kII(okay_expr_sheet,
                        normal_keys,
                        cancer_keys,
                        {})

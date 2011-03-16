#!/usr/bin/env python

import math
import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
import GNUplot.GNUplot_points2 as GNUplot
from Calc_Packages.Math.Vector1 import *
from Calc_Packages.Math.StatsI import *

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

def GNUplot_Cancer11kII(expr_sheet_cancer11k,
                        col_keys_normal,
                        col_keys_cancer,
                        classification):
    """ classification is dictionary where
    classification[ onc_id ] = class_name """

    def change_calc(after, before):
        return math.log(after / before) / math.log(10.0)

    pset = GNUplot.Points_Set()

    for afs_id in expr_sheet_cancer11k.conv_afas_onc.keys():
        onc_id = expr_sheet_cancer11k.conv_afas_onc.val_force(afs_id)
        if afs_id in classification:
            class_name = classification[ afs_id ]
        else:
            class_name = "Others"

        (exp_pat_sense_normal,
         exp_pat_sense_cancer,
         exp_pat_antis_normal,
         exp_pat_antis_cancer) = \
         okay_expr_sheet.get_four_exp_pat(afs_id,
                                          normal_keys,
                                          cancer_keys)

        diff_sense = vector_pair(exp_pat_sense_cancer,
                                 exp_pat_sense_normal,
                                 change_calc)
        diff_antis = vector_pair(exp_pat_antis_cancer,
                                 exp_pat_antis_normal,
                                 change_calc)
        x = median(diff_sense)
        y = median(diff_antis)

        if class_name != "Others":
            print "%.3f %.3f %s %s %s" % (x, y,
                                          class_name, onc_id, afs_id)

        pset.add_point(class_name, [ x, y ])

    GNUplot.GNUplot_points(pset).gnuplot()


if __name__ == "__main__":

    from SAT_Packages.SAT11K.Human_Cancer11k_Global import *
    from SAT_Packages.SAT11K.Cancer11k_Gene_info1 \
        import Cancer11k_Gene_info
    from SAT_Packages.SAT11K.OptParse_Cancer11k1 import OptParse_celltype_prim

    expr_file, normal_keys, cancer_keys, args_dummy = \
        OptParse_celltype_prim()
    print expr_file, normal_keys, cancer_keys

    okay_expr_sheet = Cancer11k.Okay_Sheet(expr_file)

    okay_marker = {}
    for afas in okay_expr_sheet.conv_afas_onc.keys():
        if afas in okay_marker_afas_colon \
                and afas in okay_marker_afas_hepatic:
            okay_marker[ afas ] = "Okay Marker"
        elif afas in okay_marker_afas_colon:
            okay_marker[ afas ] = "Okay Colon Marker"
        elif afas in okay_marker_afas_hepatic:
            okay_marker[ afas ] = "Okay Hepatic Marker"

    GNUplot_Cancer11kII(okay_expr_sheet,
                        normal_keys,
                        cancer_keys,
                        okay_marker)

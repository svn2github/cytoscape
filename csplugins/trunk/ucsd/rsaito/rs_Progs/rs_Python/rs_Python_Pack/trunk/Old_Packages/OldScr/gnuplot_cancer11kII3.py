#!/usr/bin/env python

import math
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

    def change_calc(after, before):
        return math.log(after / before) / math.log(10.0)

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

        diff_sense = vector_pair(exp_pat_sense_cancer,
                                 exp_pat_sense_normal,
                                 change_calc)
        diff_antis = vector_pair(exp_pat_antis_cancer,
                                 exp_pat_antis_normal,
                                 change_calc)
        x = median(diff_sense)
        y = median(diff_antis)
        pset.add_point(class_name, [ x, y ])

    GNUplot.GNUplot_points(pset).gnuplot()


if __name__ == "__main__":

    from SAT_Packages.SAT11K.Human_Cancer11k_Global import *
    from SAT_Packages.SAT11K.Cancer11k_Gene_info1 \
        import Cancer11k_Gene_info

    """ Setting expression data type """

    # cancer_keys = colon_cancer_keys
    # normal_keys = colon_normal_keys
    # expr_file   = rsc.Human11k_Cancer_Colon_random

    cancer_keys = hepatic_cancer_keys
    normal_keys = hepatic_normal_keys
    expr_file   = rsc.Human11k_Cancer_Hepatic_random

    """ Setting expression data type (End) """

    okay_expr_sheet = Cancer11k.Okay_Sheet(expr_file)

    okay_marker_colon = [
        "ONC-L31951",
        "ONC-L34058",
        "ONC-M14505",
        "ONC-M25753",
        "ONC-U01038",
        "ONC-U29343",
        "ONC-U37139",
        "ONC-U43746",
        "ONC-U58334",
        "ONC-X52022",
        "ONC-X57766",
        "ONC-X63629"
        ]

    okay_marker_hepatic = [
        "ONC-L03840",
        "ONC-L34058",
        "ONC-M15796",
        "ONC-M21616",
        "ONC-M25753",
        "ONC-M31899",
        "ONC-M81104",
        "ONC-U25278",
        "ONC-X53586"
        ]


    okay_marker = {}
    for onc_gene in okay_expr_sheet.conv_onc_afas.keys():
        if onc_gene in okay_marker_colon:
            okay_marker[ onc_gene ] = "Okay Colon Marker"
        elif onc_gene in okay_marker_hepatic:
            okay_marker[ onc_gene ] = "Okay Hepatic Marker"

    cancer_gene_info = Cancer11k_Gene_info(
        rsc.Human11k_Cancer_gene_info,
        rsc.Human11k_Cancer_category_info_func)

    for category in cancer_gene_info.get_categories():
    # for category in [ "FG00" ]:

        category_descr = cancer_gene_info.get_category_descr(category)

        onc_genes_in_category = cancer_gene_info. \
            get_ONC_accession_from_categ(category)

        classification = {}
        afs_ids_used = []

        for onc_gene in onc_genes_in_category:
            classification[ onc_gene ] = category_descr
            afs_ids = okay_expr_sheet.conv_onc_afas.val_force(onc_gene)
            if afs_ids:
                afs_ids_used += afs_ids

        for onc_gene in okay_marker:
            classification[ onc_gene ] = okay_marker[ onc_gene ]

        print category, category_descr, onc_genes_in_category, afs_ids_used
        print

        if category_descr and len(afs_ids_used) > 0:
            GNUplot_Cancer11kII(okay_expr_sheet,
                                normal_keys,
                                cancer_keys,
                                classification)

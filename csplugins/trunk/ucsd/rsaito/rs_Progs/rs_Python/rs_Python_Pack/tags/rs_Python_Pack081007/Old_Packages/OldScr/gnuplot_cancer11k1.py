#!/usr/bin/env python

import SAT_Packages.SAT_Read11K.Human_Cancer11k2 as Cancer11k
import SAT_Packages.SAT.SAT_gnuplot1 as SAT_gnuplot

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsAntis_Config")

def GNUplot_Cancer11k(expr_sheet_cancer11k, 
                      col_keys_normal,
                      col_keys_cancer,
                      afs_id):

    onc_id = expr_sheet_cancer11k.conv_afas_onc.val_force(afs_id)
    if not onc_id:
        return

    exp_pat_sense_normal = expr_sheet_cancer11k.get_data_accord_keys(
        onc_id, col_keys_normal)
    exp_pat_sense_cancer = expr_sheet_cancer11k.get_data_accord_keys(
        onc_id, col_keys_cancer)
    exp_pat_antis_normal = expr_sheet_cancer11k.get_data_accord_keys(
        afs_id, col_keys_normal)
    exp_pat_antis_cancer = expr_sheet_cancer11k.get_data_accord_keys(
        afs_id, col_keys_cancer)

    sat_plot = SAT_gnuplot.SAT_gnuplot()
    sat_plot.set_conditions(
        col_keys_normal + ["|"] + col_keys_cancer)
    sat_plot.set_sensedata(
        exp_pat_sense_normal + [0] + exp_pat_sense_cancer)
    sat_plot.set_antisensedata(
        exp_pat_antis_normal + [0] + exp_pat_antis_cancer)
    sat_plot.set_dataname1(onc_id)
    sat_plot.set_dataname2(afs_id)

    sat_plot.gnuplot()


if __name__ == "__main__":

    from SAT_Packages.SAT_Read11K.Human_Cancer11k_Global import *
    
    """ Setting expression data type """

    # cancer_keys = colon_cancer_keys
    # normal_keys = colon_normal_keys
    # expr_file   = rsc.Human11k_Cancer_Colon_random

    cancer_keys = hepatic_cancer_keys
    normal_keys = hepatic_normal_keys
    expr_file   = rsc.Human11k_Cancer_Hepatic_random

    """ Setting expression data type (End) """

    okay_expr_sheet = Cancer11k.Okay_Sheet(expr_file)
    
    for afas_id in okay_expr_sheet.conv_afas_onc.keys():
        rsrev_scores = okay_expr_sheet.get_reverse_level_afs(
            afas_id, 
            normal_keys,
            cancer_keys)
        # if 1.0*sum(rsrev_scores) / len(rsrev_scores) < -1000:
        if (max(rsrev_scores) < 10 and
            1.0*sum(rsrev_scores) / len(rsrev_scores) < -50):
            print afas_id, rsrev_scores
            GNUplot_Cancer11k(okay_expr_sheet,
                              normal_keys,
                              cancer_keys,
                              afas_id)

    """
    GNUplot_Cancer11k(okay_expr_sheet,
                      normal_keys,
                      cancer_keys,
                      "AFAS-Onc-Anti-M55172-01")
                      """

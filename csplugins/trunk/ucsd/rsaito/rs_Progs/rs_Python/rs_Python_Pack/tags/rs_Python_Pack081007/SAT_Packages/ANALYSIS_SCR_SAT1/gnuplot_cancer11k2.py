#!/usr/bin/env python

import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
import SAT_Packages.SAT11K.GNUplot_Cancer11k1 as GNUplot
import SAT_Packages.SAT.SAT_gnuplot1 as SAT_gnuplot

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

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

for afas_id in okay_expr_sheet.conv_afas_onc.keys():
    rsrev_scores = okay_expr_sheet.get_reverse_level_afs(
        afas_id,
        normal_keys,
        cancer_keys)
    # if 1.0*sum(rsrev_scores) / len(rsrev_scores) < -1000:
    if (max(rsrev_scores) < 10 and
        1.0*sum(rsrev_scores) / len(rsrev_scores) < -50):
        print afas_id, rsrev_scores
        GNUplot.GNUplot_Cancer11k(okay_expr_sheet,
                                  normal_keys,
                                  cancer_keys,
                                  afas_id)

"""
GNUplot_Cancer11k(okay_expr_sheet,
                  normal_keys,
                  cancer_keys,
                  "AFAS-Onc-Anti-M55172-01")
                  """

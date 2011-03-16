#!/usr/bin/env python

import math
from Calc_Packages.Math.Vector1 import *
from Calc_Packages.Math.StatsI import *
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

def change_calc(after, before):
    return math.log(after / before) / math.log(10.0)

okay_expr_sheet = Cancer11k.Okay_Sheet(expr_file)

for afs_id in okay_expr_sheet.conv_afas_onc.keys():
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

    if x > 0.2 and y < -0.2:
        print diff_sense
        print diff_antis
        print x, y
        GNUplot.GNUplot_Cancer11k(okay_expr_sheet,
                                  normal_keys,
                                  cancer_keys,
                                  afs_id)

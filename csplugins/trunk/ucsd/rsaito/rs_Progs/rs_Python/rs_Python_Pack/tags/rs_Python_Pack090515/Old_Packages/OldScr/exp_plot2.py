#!/usr/bin/env python

import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
import GNUplot.GNUplot_points2 as GNUplot
from Calc_Packages.Math.Vector1 import *
from Calc_Packages.Math.StatsI import *

from SAT_Packages.SAT11K.Human_Cancer11k_Global import *

from SAT_Packages.SAT11K.OptParse_Cancer11k1 import OptParse_celltype_prim

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

""" Setting expression data type """

expr_file, normal_keys, cancer_keys, args_dummy = \
    OptParse_celltype_prim()

""" Setting expression data type (End) """

expr_sheet_cancer11k = Cancer11k.Okay_Sheet(expr_file)

x = []
y = []

for afs_id in expr_sheet_cancer11k.conv_afas_onc.keys():
    onc_id = expr_sheet_cancer11k.conv_afas_onc.val_force(afs_id)

    exp_pat_sense_normal = expr_sheet_cancer11k.get_data_accord_keys(
        onc_id, normal_keys)
    exp_pat_antis_normal = expr_sheet_cancer11k.get_data_accord_keys(
        afs_id, normal_keys)

    x.append(median(exp_pat_sense_normal))
    y.append(median(exp_pat_antis_normal))

print ",".join(normal_keys), "Correlation =", corr(x, y)
for i in range(len(x)):
    print "\t".join(("%.3lf" % x[i], "%.3lf" % y[i]))
print

x = []
y = []

for afs_id in expr_sheet_cancer11k.conv_afas_onc.keys():
    onc_id = expr_sheet_cancer11k.conv_afas_onc.val_force(afs_id)

    exp_pat_sense_cancer = expr_sheet_cancer11k.get_data_accord_keys(
        onc_id, cancer_keys)
    exp_pat_antis_cancer = expr_sheet_cancer11k.get_data_accord_keys(
        afs_id, cancer_keys)

    x.append(median(exp_pat_sense_cancer))
    y.append(median(exp_pat_antis_cancer))

print ",".join(normal_keys), "Correlation =", corr(x, y)
for i in range(len(x)):
    print "\t".join(("%.3lf" % x[i], "%.3lf" % y[i]))
print

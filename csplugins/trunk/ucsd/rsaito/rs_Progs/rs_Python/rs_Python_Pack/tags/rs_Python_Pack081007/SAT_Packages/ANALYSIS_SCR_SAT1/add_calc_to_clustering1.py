#!/usr/bin/env python

import math
from Usefuls.rsConfig import RSC_II
from Usefuls.Sheet_Analysis import Sheet_tab
import SAT_Packages.SAT11K.Human_Cancer11k2 as Cancer11k
from SAT_Packages.SAT11K.Human_Cancer11k_Global import *

rsc = RSC_II("rsSAT_Config")

colon_revers_keys = [ "Colon_R1",
                      "Colon_R12",
                      "Colon_R13",
                      "Colon_R15",
                      "Colon_R2",
                      "Colon_R7" ]

hepatic_revers_keys = [ "Hepatic_R12",
                        "Hepatic_R16",
                        "Hepatic_R20",
                        "Hepatic_R5",
                        "Hepatic_R6" ]

""" Cell Type Settings """

# normal_keys = colon_normal_keys
# cancer_keys = colon_cancer_keys
# oscore_keys = colon_revers_keys
# expr_file   = rsc.Human11k_Cancer_Colon_dT
# cl_res_file = rsc.Human11k_Cluster_Result_Colon_dT

normal_keys = hepatic_normal_keys
cancer_keys = hepatic_cancer_keys
oscore_keys = hepatic_revers_keys
expr_file   = rsc.Human11k_Cancer_Hepatic_dT
cl_res_file = rsc.Human11k_Cluster_Result_Hepatic_dT

""" Cell Type Settings (End) """

okay_expr_sheet = Cancer11k.Okay_Sheet(expr_file)
okay_clustering_result = Sheet_tab(cl_res_file)

while True:
    r = okay_clustering_result.read_line()
    if not r: break
    if okay_clustering_result.get_line_counter() == 1:
        out =  r + oscore_keys
    elif okay_clustering_result.get_line_counter() == 2:
        out = r + ["1.0"] * len(oscore_keys)
    else:
        afs_id = r[1]
        rev_level = okay_expr_sheet.get_reverse_level_afs(afs_id,
                                                          normal_keys,
                                                          cancer_keys)
        rev_level_str = map(lambda x: `x`, rev_level)
        out = r + rev_level_str
    print "\t".join(out)

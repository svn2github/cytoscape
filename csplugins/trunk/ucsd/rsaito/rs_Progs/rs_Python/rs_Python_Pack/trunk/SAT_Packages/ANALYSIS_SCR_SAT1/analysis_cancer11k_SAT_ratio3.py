#!/usr/bin/env python

import math
import sys
from Calc_Packages.Math.Vector1 import vector_pair
from Calc_Packages.Stats.Stats_OrderI import ordering
# import SAT_Packages.SAT11K.Human_Cancer11k4 as Cancer11k
import SAT_Packages.SAT11K.Okay_Sheet_Simple1 as SAT_Cancer11k
import SAT_Packages.SAT11K.Cancer11k_Gene_info1 as Gene_info

from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kI
from SAT_Packages.SAT11K.Read11kII1 import Read_SAT11KII

from Expr_Packages.Clustering.Cluster_Eisen_frmt1 import Cluster_Eisen_frmt

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")


def log_ratio(after, before):
    return math.log(after / before) / math.log(10.0)

def min_change(exp_pat_sense_normal,
               exp_pat_sense_cancer,
               exp_pat_antis_normal,
               exp_pat_antis_cancer,
               mult = (+1.0, -1.0, -1.0, +1.0)):
     
    ret = []
    for i in range(len(exp_pat_sense_normal)):
        s_n = exp_pat_sense_normal[i]
        s_c = exp_pat_sense_cancer[i]
        a_n = exp_pat_antis_normal[i]
        a_c = exp_pat_antis_cancer[i]
        ret.append(min(log_ratio(s_c, s_n)*mult[0],
                       log_ratio(a_c, a_n)*mult[1],
                       log_ratio(s_n, a_n)*mult[2],
                       log_ratio(s_c, a_c)*mult[3]
                       ))
        
    return ret

def min_change_select_patient_no(exp_pat_sense_normal,
                                 exp_pat_sense_cancer,
                                 exp_pat_antis_normal,
                                 exp_pat_antis_cancer,
                                 select_patient_nth,
                                 mult = (+1.0, -1.0, -1.0, +1.0)):

    min_change_array = min_change(exp_pat_sense_normal,
                                  exp_pat_sense_cancer,
                                  exp_pat_antis_normal,
                                  exp_pat_antis_cancer,
                                  mult)
    min_change_array_order = ordering(min_change_array)
    min_change_array_order_sort = min_change_array_order[:]
    min_change_array_order_sort.sort()
    patient_rank = min_change_array_order_sort[-select_patient_nth]
    patient_no   = min_change_array_order.index(patient_rank)     
    
    return patient_no, min_change_array[patient_no]


select_patient_nth = 3

sys.stderr.write("Reading command line ...\n")
opt = Option_Cancer11kI()
normal_keys  = opt.get_normal_keys()
cancer_keys  = opt.get_cancer_keys()

if opt.get_misc1() != "":
    thres_st = float(opt.get_misc1())
else:
    thres_st = 0.0414 # (1.1 fold)
    
if opt.get_misc2() != "":
    thres_op = float(opt.get_misc2())
else:
    thres_op = 0.0414 # (1.1 fold)


sys.stderr.write("Reading SAT 11K pairs ...\n")
sats_strand, sats_pc_pc, sats_pc_nc, sats_nc_nc = \
    Read_SAT11KII(rsc.human11k_okay)

# print "Option 3:", opt.get_misc3(), type(opt.get_misc3())
if opt.get_misc3() == "1":
    sats = sats_pc_pc
elif opt.get_misc3() == "2":
    sats = sats_pc_nc
elif opt.get_misc3() == "3":
    sats = sats_nc_nc
else:
    sats = sats_strand

sys.stderr.write("Reading Cancer 11K information ...\n")
cancer_gene_info = Gene_info.Cancer11k_Gene_info_OncTS(
    rsc.Human11k_Cancer_gene_info,
    rsc.Human11k_Cancer_category_info_func)

sys.stderr.write("Reading Cancer 11K data ...\n")
cancer_sheet = SAT_Cancer11k.Human_Cancer11k(opt)

output = Cluster_Eisen_frmt(normal_keys + cancer_keys + ["(Flag)"],
                            [ 1.0 ] * len(normal_keys + cancer_keys) + [ 0.0 ])

for sat in sats.get_sats():

    t1, t2  = sat.get_transcripts()
    s_id, a_id = t1.get_transcriptID(), t2.get_transcriptID()

    (exp_pat_sense_normal,
     exp_pat_sense_cancer,
     exp_pat_antis_normal,
     exp_pat_antis_cancer) = \
     cancer_sheet.get_four_exp_pat(sat)

    sense_antis_ratio_normal = \
        vector_pair(exp_pat_sense_normal,
                    exp_pat_antis_normal,
                    log_ratio)

    sense_antis_ratio_cancer = \
        vector_pair(exp_pat_sense_cancer,
                    exp_pat_antis_cancer,
                    log_ratio)

    diff_sense_ratio = vector_pair(exp_pat_sense_cancer,
                                   exp_pat_sense_normal,
                                   log_ratio)
    diff_antis_ratio = vector_pair(exp_pat_antis_cancer,
                                   exp_pat_antis_normal,
                                   log_ratio)

    patient_st, min_change_st = \
        min_change_select_patient_no(exp_pat_sense_normal,
                                     exp_pat_sense_cancer,
                                     exp_pat_antis_normal,
                                     exp_pat_antis_cancer,
                                     select_patient_nth)  

    patient_op, min_change_op = \
        min_change_select_patient_no(exp_pat_sense_normal,
                                     exp_pat_sense_cancer,
                                     exp_pat_antis_normal,
                                     exp_pat_antis_cancer,
                                     select_patient_nth,
                                     mult = (-1.0, +1.0, +1.0, -1.0)
                                     )

    if min_change_st >= thres_st: 
        flag = +3.0
        
    elif min_change_op >= thres_op:
        flag = -3.0
    else:
        flag = ""


    output.set_expression(sat.get_satid(),
                          sense_antis_ratio_normal + sense_antis_ratio_cancer + [ flag ],
                          s_id + "-" + a_id)
    
output.display()


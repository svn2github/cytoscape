#!/usr/bin/env python

import sys
import math

import SAT_Packages.SAT11K.Human_Cancer11k5 as Cancer11k
import SAT_Packages.SAT11K.Cancer11k_Gene_info1 as Gene_info
from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kII
from Calc_Packages.Math.Stats_OrderI import median, ordering
from Usefuls.Counter import Count2
import Usefuls.Table_maker
from Usefuls.rsConfig import RSC_II
from Calc_Packages.Math.StatsI import mean, sd_infer, sem

select_patient_nth = 3

def log_ratio(after, before):
    return math.log(after / before) / math.log(10.0)


def min_change(exp_pat_sense_normal,
               exp_pat_sense_cancer,
               exp_pat_antis_normal,
               exp_pat_antis_cancer,
               mult = (+1.0, -1.0, -1.0, +1.0)):
    
    global logfile
    
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


rsc = RSC_II("rsSAT_Config")

sys.stderr.write("Reading command line ...\n")
opt = Option_Cancer11kII()

sys.stderr.write("Reading Cancer 11K data ...\n")
cancer11k_dt, cancer11k_rd = Cancer11k.create_Human_Cancer11k_dt_rd(opt, rsc.Human11k_Cancer_AFAS_ID_Conv)

sys.stderr.write("Reading Well above background ...\n")
wab_dt_sheet = cancer11k_dt.get_wab_sheet()
wab_rd_sheet = cancer11k_rd.get_wab_sheet()

sys.stderr.write("Reading Cancer 11K information ...\n")
cancer_gene_info = Gene_info.Cancer11k_Gene_info_OncTS(
    rsc.Human11k_Cancer_gene_info,
    rsc.Human11k_Cancer_category_info_func)

counter_all_sense = 0
counter_sub_sense = 0
counter_all_AFAS = 0
counter_sub_AFAS = 0
counter_GO_all = Count2()
counter_GO_sub = Count2()
onc_id_checked_all = {}
onc_id_checked_sub = {}
  
for afs_id in cancer11k_rd.get_exp_sheet().conv_afas_onc.keys():
    counter_all_AFAS += 1

    onc_id = cancer11k_rd.get_exp_sheet().conv_afas_onc.val_force(afs_id)

    (exp_pat_dt_sense_normal,
     exp_pat_dt_sense_cancer,
     exp_pat_dt_antis_normal,
     exp_pat_dt_antis_cancer) = \
     cancer11k_dt.get_four_exp_pat(afs_id)

    (exp_pat_rd_sense_normal,
     exp_pat_rd_sense_cancer,
     exp_pat_rd_antis_normal,
     exp_pat_rd_antis_cancer) = \
     cancer11k_rd.get_four_exp_pat(afs_id)
     
    (wab_dt_sense_normal,
     wab_dt_sense_cancer,
     wab_dt_antis_normal,
     wab_dt_antis_cancer) = \
     wab_dt_sheet.get_four_exp_pat(afs_id, 
                                   opt.get_normal_keys(), 
                                   opt.get_cancer_keys())
    
    (wab_rd_sense_normal,
     wab_rd_sense_cancer,
     wab_rd_antis_normal,
     wab_rd_antis_cancer) = \
     wab_rd_sheet.get_four_exp_pat(afs_id, 
                                   opt.get_normal_keys(),
                                   opt.get_cancer_keys())

    median_exp_pat_dt_antis_normal = median(exp_pat_dt_antis_normal)
    median_exp_pat_dt_antis_cancer = median(exp_pat_dt_antis_cancer)     
    median_exp_pat_rd_antis_normal = median(exp_pat_rd_antis_normal)
    median_exp_pat_rd_antis_cancer = median(exp_pat_rd_antis_cancer)
    
    patient, min_change_level = \
        min_change_select_patient_no(exp_pat_rd_sense_normal,
                                     exp_pat_rd_sense_cancer,
                                     exp_pat_rd_antis_normal,
                                     exp_pat_rd_antis_cancer,
                                     select_patient_nth)
                                     # mult = (-1.0, +1.0, +1.0, -1.0))  
 
    categ = cancer_gene_info.get_major_categ_descr(onc_id)

    if onc_id not in onc_id_checked_all:
        counter_all_sense += 1
        counter_GO_all.count_up_list(categ)
    onc_id_checked_all[ onc_id ] = ""
           
    if min_change_level >= 0.0414:  # <--- Write condition here
    # if sum(wab_rd_antis_normal) >= 3:
    # if median_exp_pat_dt_antis_normal >= 100:  
    # if median_exp_pat_rd_antis_normal * 2 <= median_exp_pat_rd_antis_cancer:
        counter_sub_AFAS += 1         

        if onc_id not in onc_id_checked_sub:
            counter_sub_sense += 1
            counter_GO_sub.count_up_list(categ)
        onc_id_checked_sub[ onc_id ] = ""


print "All", counter_all_AFAS, counter_all_sense
print "Sub", counter_sub_AFAS, counter_sub_sense
print

output = Usefuls.Table_maker.Table_row()

all_go = counter_GO_all.get_elems()
all_go.sort()

for go in all_go:
    go_all = counter_GO_all[go]
    if go in counter_GO_sub:
        go_sub = counter_GO_sub[go]
    else:
        go_sub = 0
    output.append("GO", go)
    output.append("All", `go_all`)
    output.append("Sub", `go_sub`)
    output.append("All ratio", "%.3f" % (1.0*go_all/counter_all_sense))
    output.append("Sub ratio", "%.3f" % (1.0*go_sub/counter_sub_sense))
    output.output("\t")




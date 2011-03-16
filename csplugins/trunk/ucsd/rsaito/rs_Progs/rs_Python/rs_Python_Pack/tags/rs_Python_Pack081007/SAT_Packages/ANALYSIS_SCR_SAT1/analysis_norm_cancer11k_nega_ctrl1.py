#!/usr/bin/env python

import sys
import math

import SAT_Packages.SAT11K.Human_Cancer11k5 as Cancer11k
from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kI
from Calc_Packages.Math.Stats_OrderI import median, ordering

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
opt = Option_Cancer11kI()

sys.stderr.write("Reading Cancer 11K data ...\n")
cancer11k = Cancer11k.Human_Cancer11k(opt, rsc.Human11k_Cancer_AFAS_ID_Conv)
cancer11k_sf = Cancer11k.Human_Cancer11k(opt, rsc.Human11k_Cancer_AFAS_ID_Conv)

counter_p  = 0
n_iteration = 100
shuffle_samples = []
for iteration in range(n_iteration):
    # cancer11k_sf.shuffle_norm_cancer("") # ("AFAS-Onc-Anti-")
    cancer11k_sf.shuffle_sense_AFAS()
    
    counter    = 0
    counter_nc = 0
    
    for afs_id in cancer11k.get_exp_sheet().conv_afas_onc.keys():
    
        onc_id = cancer11k.get_exp_sheet().conv_afas_onc.val_force(afs_id)
           
        (exp_pat_sense_normal,
         exp_pat_sense_cancer,
         exp_pat_antis_normal,
         exp_pat_antis_cancer) = \
         cancer11k.get_four_exp_pat(afs_id)
         
        (exp_pat_sense_normal_sf,
         exp_pat_sense_cancer_sf,
         exp_pat_antis_normal_sf,
         exp_pat_antis_cancer_sf) = \
         cancer11k_sf.get_four_exp_pat(afs_id)
      
        median_exp_pat_antis_normal = median(exp_pat_antis_normal)
        median_exp_pat_antis_cancer = median(exp_pat_antis_cancer)

        median_exp_pat_antis_normal_sf = median(exp_pat_antis_normal_sf)
        median_exp_pat_antis_cancer_sf = median(exp_pat_antis_cancer_sf)

        """
        print "*** %s - %s ***" % (onc_id, afs_id)
        print "Sense Normal", "\t".join(map(lambda x:"%.2f" % (x,), exp_pat_sense_normal))
        print "Sense Cancer", "\t".join(map(lambda x:"%.2f" % (x,), exp_pat_sense_cancer))
        print "Antis Normal", "\t".join(map(lambda x:"%.2f" % (x,), exp_pat_antis_normal)), median_exp_pat_antis_normal
        print "Antis Cancer", "\t".join(map(lambda x:"%.2f" % (x,), exp_pat_antis_cancer)), median_exp_pat_antis_cancer
        print "Shuffled."
        print "Sense Normal", "\t".join(map(lambda x:"%.2f" % (x,), exp_pat_sense_normal_sf))
        print "Sense Cancer", "\t".join(map(lambda x:"%.2f" % (x,), exp_pat_sense_cancer_sf))
        print "Antis Normal", "\t".join(map(lambda x:"%.2f" % (x,), exp_pat_antis_normal_sf)), median_exp_pat_antis_normal_sf
        print "Antis Cancer", "\t".join(map(lambda x:"%.2f" % (x,), exp_pat_antis_cancer_sf)), median_exp_pat_antis_cancer_sf
        print
        
  
        if median_exp_pat_antis_cancer >= median_exp_pat_antis_normal*2:
            counter    += 1
        if median_exp_pat_antis_cancer_sf >= median_exp_pat_antis_normal_sf*2:
            counter_nc += 1
       
        if median_exp_pat_antis_cancer*3 < median_exp_pat_antis_normal:
            counter    += 1
            print onc_id, afs_id
        if median_exp_pat_antis_cancer_sf*3 < median_exp_pat_antis_normal_sf:
            counter_nc += 1
 
        """
        
        patient, min_change_rl = \
            min_change_select_patient_no(exp_pat_sense_normal,
                                         exp_pat_sense_cancer,
                                         exp_pat_antis_normal,
                                         exp_pat_antis_cancer,
                                         select_patient_nth)
                                         # (-1.0, +1.0, +1.0, -1.0))  

        patient_sf, min_change_sf = \
            min_change_select_patient_no(exp_pat_sense_normal_sf,
                                         exp_pat_sense_cancer_sf,
                                         exp_pat_antis_normal_sf,
                                         exp_pat_antis_cancer_sf,
                                         select_patient_nth)
                                         # (-1.0, +1.0, +1.0, -1.0))  


        if min_change_rl >= 0.0414:
            counter += 1
            # print onc_id, afs_id
        if min_change_sf >= 0.0414:
            counter_nc += 1

            
    shuffle_samples.append(counter_nc)

    if counter_nc >= counter:
        counter_p += 1
    print counter, counter_nc

print
print "Final result:"
print "Over count:", counter_p
print "Iteration :", n_iteration
print "NC average:", mean(shuffle_samples)
print "NC SD     :", sd_infer(shuffle_samples)
print "NC SEM    :", sem(shuffle_samples)


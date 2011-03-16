#!/usr/bin/env python

import sys
import math
from optparse import OptionParser

import SAT_Packages.SAT11K.Human_Cancer11k5 as Cancer11k
from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kI
from Calc_Packages.Stats.Stats_OrderI import median, ordering

from Usefuls.rsConfig import RSC_II
from Calc_Packages.Stats.StatsI import mean, sd_infer, sem

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


sys.stderr.write("Reading command line ...\n")

usage = "Usage: %prog [ options ] arguments"
parser = OptionParser(usage)
            
parser.add_option(
    "--iter", dest = "iteration",
    help = "number of iterations for shuffling",
    default = "100") 

parser.add_option(
    "--shuf", dest = "shuf_method",
    help = "shuffling method. 0: No shuffling, 1: Normal-Cancer shuffling, 2: Normal-Cancer shuffling (AFAS only), 3: Sense-AFAS shuffling",
    default = "1")

parser.add_option(
    "--irate", dest = "irate",
    help = "Minimum increase rate (Ex. 1.1 indicates 110% increase)",
    default = "1.1")

parser.add_option(
    "--select", dest = "select",
    help = "Target selection. SA: sense-antisense, Aup: antisense up-regulated in cancer, Adn: antisense down-regulated",
    default = "SA"
    )

opt = Option_Cancer11kI(parser)

rsc = RSC_II("rsSAT_Config")

sys.stderr.write("Reading Cancer 11K data ...\n")
cancer11k = Cancer11k.Human_Cancer11k(opt, rsc.Human11k_Cancer_AFAS_ID_Conv)
cancer11k_sf = Cancer11k.Human_Cancer11k(opt, rsc.Human11k_Cancer_AFAS_ID_Conv)

counter_p  = 0
shuffle_samples = []
for iteration in range(int(opt.get_options().iteration)):
    if opt.get_options().shuf_method == "1":
        cancer11k_sf.shuffle_norm_cancer() 
    elif opt.get_options().shuf_method == "2":
        cancer11k_sf.shuffle_norm_cancer("AFAS-Onc-Anti-") # For AFAS-only shuffling.
    elif opt.get_options().shuf_method == "3":
        cancer11k_sf.shuffle_sense_AFAS() # Shuffling of sense-antisense pairngs
    
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

        increase_rate = float(opt.get_options().irate)

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

        """
 
        if opt.get_options().select == "Aup":
            if median_exp_pat_antis_cancer >= median_exp_pat_antis_normal * increase_rate:
                counter    += 1
            if median_exp_pat_antis_cancer_sf >= median_exp_pat_antis_normal_sf * increase_rate:
                counter_nc += 1
                
        elif opt.get_options().select == "Adn":
            if median_exp_pat_antis_cancer * increase_rate <= median_exp_pat_antis_normal:
                counter    += 1
            if median_exp_pat_antis_cancer_sf * increase_rate <= median_exp_pat_antis_normal_sf:
                counter_nc += 1
 
        elif opt.get_options().select == "SA":
            if min_change_rl >= math.log10(increase_rate): # 0.0414 = log(1.1)/log(10.0)
                counter += 1
                # print onc_id, afs_id
            if min_change_sf >= math.log10(increase_rate):
                counter_nc += 1

            
    shuffle_samples.append(counter_nc)

    if counter_nc >= counter:
        counter_p += 1
    print "[%5d] Actual / Shuffled: %d / %d" % (iteration, counter, counter_nc)

print
print "Final result:"
print "Selection  :", { "SA" : "Sense-AFAS",
                        "Aup"  : "AFAS up-regulated in cancer",
                        "Adn"  : "AFAS down-regulated in cancer"}[opt.get_options().select]
print "Shuffle    :", { "0": "No shuffling", 
                        "1": "Normal-Cancer shuffling",
                        "2": "Normal-Cancer shuffling (AFAS only)",
                        "3": "Sense-AFAS shuffling" }[opt.get_options().shuf_method]
print "Inc rate   :", increase_rate
print "Iteration  :", opt.get_options().iteration
print "Actual     :", counter
print "Over count :", counter_p
print "NC average :", mean(shuffle_samples)
print "NC SD      :", sd_infer(shuffle_samples)
print "NC SEM     :", sem(shuffle_samples)


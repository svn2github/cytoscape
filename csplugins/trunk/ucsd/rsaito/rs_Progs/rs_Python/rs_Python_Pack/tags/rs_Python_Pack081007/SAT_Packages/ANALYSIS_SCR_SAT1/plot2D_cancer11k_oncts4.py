#!/usr/bin/env python

import math
import Data_Struct.Plot2
import SAT_Packages.SAT11K.Human_Cancer11k3 as Cancer11k
import GNUplot.GNUplot_points2 as GNUplot
from Calc_Packages.Math.Vector1 import *
from Calc_Packages.Math.StatsI import *
from Calc_Packages.Math.Stats_OrderI import *

from Usefuls.ListProc1 import common
from Usefuls.Adjust_to_thres import adjust_to_lower_thres_list
from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

def Plot_Cancer11k_OncTS(cancer11k, cancer_gene_info, flag, count_thres):

    def change_calc(after, before):
        return math.log(after / before) / math.log(10.0)

    pset = Data_Struct.Plot2.Plot(("General cancer related genes",
                 "Oncogenes", "Tumor suppressors"))

    for afs_id in cancer11k.get_exp_sheet().conv_afas_onc.keys():
        onc_id = cancer11k.get_exp_sheet().conv_afas_onc.val_force(afs_id)
        categ  = cancer_gene_info.get_OncTS_from_onc_id(onc_id)
        descr = { "O": "Oncogenes", "S": "Tumor suppressors" }.\
            get(categ, "General cancer related genes")

        (exp_pat_sense_normal,
         exp_pat_sense_cancer,
         exp_pat_antis_normal,
         exp_pat_antis_cancer) = cancer11k.get_four_exp_pat(afs_id)

        diff_sense = vector_pair(exp_pat_sense_cancer,
                                 exp_pat_sense_normal,
                                 change_calc)
        diff_antis = vector_pair(exp_pat_antis_cancer,
                                 exp_pat_antis_normal,
                                 change_calc)

        patient = get_centre_closer_to_median((diff_sense, diff_antis))[0]
        x = diff_sense[patient]
        y = diff_antis[patient]

        pset.add_point(descr, [ x, y ])

        if flag == "isp":
            u_sense_normal = mean(exp_pat_sense_normal)
            u_sense_cancer = mean(exp_pat_sense_cancer)
            u_antis_normal = mean(exp_pat_antis_normal)
            u_antis_cancer = mean(exp_pat_antis_cancer)            
            out = map(lambda n: "% 10.2lf" % n, 
                      (x, y, u_sense_normal, u_sense_cancer, u_antis_normal, u_antis_cancer))
            print "\t".join(out)

    if flag.startswith("isp"):
        return

    if count_thres:
        count_thres = float(count_thres)
        print "Count threshold is:", count_thres
    else:
        count_thres = 0.0

    if not flag:
        pset.output()
    elif flag == "count1":
        print "Sense under threshold (Expression decreased in cancer cell)"
        pset.conditional_count_x(lambda x: x < -count_thres).output(reverse = True)
        print
        print "Sense over threshold (Expression increased in cancer cell)"
        pset.conditional_count_x(lambda x: x >  count_thres).output(reverse = True)
        print
        print "Antisense under threshold (Expression decreased in cancer cell)"
        pset.conditional_count_y(lambda y: y < -count_thres).output(reverse = True)
        print
        print "Antisense over threshold (Expression increased in cancer cell)"
        pset.conditional_count_y(lambda y: y >  count_thres).output(reverse = True)    

    elif flag == "count2":
        print "Sense under and Antisense under threshold (Both expression decreased in cancer cell)"
        pset.conditional_count(lambda p: p[0] < -count_thres and p[1] < -count_thres).output(reverse = True)
        print
        
        print "Sense under and Antisense over  threshold (Sense expression decreased whereas antisense expression incleased in cancer cell)"
        pset.conditional_count(lambda p: p[0] < -count_thres and p[1] >  count_thres).output(reverse = True)
        print

        print "Sense over  and Antisense under threshold (Sense expression increased whereas antisense expression decreased in cancer cell)"
        pset.conditional_count(lambda p: p[0] >  count_thres and p[1] < -count_thres).output(reverse = True)
        print
        
        print "Sense over  and Antisense over  threshold (Both expression increased in cancer cell)"
        pset.conditional_count(lambda p: p[0] >  count_thres and p[1] >  count_thres).output(reverse = True)
        print
        
        

if __name__ == "__main__":

    from SAT_Packages.SAT11K.Human_Cancer11k_Global import *
    from SAT_Packages.SAT11K.Cancer11k_Gene_info1 \
        import Cancer11k_Gene_info_OncTS

    from SAT_Packages.SAT11K.OptParse_Cancer11k3 import Option_Cancer11kI
    opt = Option_Cancer11kI()
    human_cancer11k = Cancer11k.Human_Cancer11k(opt)

    cancer_gene_info = Cancer11k_Gene_info_OncTS(
        rsc.Human11k_Cancer_gene_info,
        rsc.Human11k_Cancer_category_info_func)
    
    flag        = opt.get_misc1()
    count_thres = opt.get_misc2()

    Plot_Cancer11k_OncTS(human_cancer11k,
                         cancer_gene_info,
                         flag,
                         count_thres)
    
    

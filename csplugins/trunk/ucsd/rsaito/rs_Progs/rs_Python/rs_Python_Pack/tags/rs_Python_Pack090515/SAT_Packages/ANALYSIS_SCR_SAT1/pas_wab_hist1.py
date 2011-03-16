#!/usr/bin/env python

import SAT_Packages.SAT11K.Human_Cancer11k2 as Human_Cancer11k
import SAT_Packages.SAT11K.OptParse_Cancer11k3 as OptParse_Cancer
from Usefuls.Histogram import Hists

opt_cancer = OptParse_Cancer.Option_Cancer11kI()
cancer11k  = Human_Cancer11k.Human_Cancer11k(opt_cancer)

exp_sheet = cancer11k.get_exp_sheet()
pas_sheet = cancer11k.get_pas_sheet()
wab_sheet = cancer11k.get_wab_sheet()

hists = Hists(0, 200, 40)
hists.add_hist("NoExpresion")
hists.add_hist("PosAndSignf")
hists.add_hist("WellAboveBG")


for id in exp_sheet.row_labels():
    exp_pat = \
        exp_sheet.get_data_accord_keys(id,
                                       opt_cancer.get_normal_keys() +
                                       opt_cancer.get_cancer_keys())
    pas_pat = \
        pas_sheet.get_data_accord_keys(id,
                                       opt_cancer.get_normal_keys() +
                                       opt_cancer.get_cancer_keys()) 
    wab_pat = \
        wab_sheet.get_data_accord_keys(id,
                                       opt_cancer.get_normal_keys() +
                                       opt_cancer.get_cancer_keys()) 
    
    """
    print "***", id, "***"
    print exp_pat
    print pas_pat
    print wab_pat
    print
    """
    


    for i in range(len(exp_pat)):
        if wab_pat[i]:
            hists.add_data("WellAboveBG", exp_pat[i])
        elif pas_pat[i]:
            hists.add_data("PosAndSignf", exp_pat[i])
        else:
            hists.add_data("NoExpresion", exp_pat[i])
hists.display()

#!/usr/bin/env python

from optparse import OptionParser

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

from SAT_Packages.SAT11K.Human_Cancer11k_Global import *

class Option_Cancer11kI:
    def __init__(self):
        options, args = self.__parsing()
    
        if options.CancerType == "colon":
            cancer_keys = colon_cancer_keys
            normal_keys = colon_normal_keys
            if options.Priming == "dt":
                exp_file = rsc.Human11k_Cancer_Colon_dT
                pas_file = rsc.Human11k_Cancer_Colon_gIsPosAndSignif_dT
                wab_file = rsc.Human11k_Cancer_Colon_gIsWellAboveBG_dT
            elif options.Priming == "rd":
                exp_file = rsc.Human11k_Cancer_Colon_random
                pas_file = rsc.Human11k_Cancer_Colon_gIsPosAndSignif_random
                wab_file = rsc.Human11k_Cancer_Colon_gIsWellAboveBG_random
    
        elif options.CancerType == "hepatic":
            cancer_keys = hepatic_cancer_keys
            normal_keys = hepatic_normal_keys
            if options.Priming == "dt":
                exp_file = rsc.Human11k_Cancer_Hepatic_dT
                pas_file = rsc.Human11k_Cancer_Hepatic_gIsPosAndSignif_dT
                wab_file = rsc.Human11k_Cancer_Hepatic_gIsWellAboveBG_dT
            elif options.Priming == "rd":
                exp_file = rsc.Human11k_Cancer_Hepatic_random
                pas_file = rsc.Human11k_Cancer_Hepatic_gIsPosAndSignif_random
                wab_file = rsc.Human11k_Cancer_Hepatic_gIsWellAboveBG_random
    
        self.exp_file = exp_file
        self.pas_file = pas_file
        self.wab_file = wab_file
        self.normal_keys = normal_keys
        self.cancer_keys = cancer_keys
        
        self.min_thres       = float(options.minexp)
        self.filtering_level = float(options.filter)
        
        self.options = options
        self.args    = args
        
    def __parsing(self):
        usage = "Usage: %prog [ options ] arguments"
        parser = OptionParser(usage)
        parser.add_option(
            "-c", "--CancerType", dest = "CancerType",
            help = "determines cancer cell type (colon or hepatic)",
            default = "colon") 
    
        parser.add_option(
            "-p", "--Priming", dest = "Priming",
            help = "determines priming type (dt or rd)",
            default = "rd")
    
        parser.add_option(
            "--minexp", dest = "minexp",
            help = "Minimum expression level (Filtering level > 0 required)",
            default = 0.0)    
    
        parser.add_option(
            "--filter", dest = "filter",
            help = "Filtering level of expression data",
            default = 0)
    
        parser.add_option(
            "--misc1", dest = "misc1",
            help = "miscellaneous option 1 (depends on program)",
            default = "")
    
        parser.add_option(
            "--misc2", dest = "misc2",
            help = "miscellaneous option 2 (depends on program)",
            default = "")
    
        parser.add_option(
            "--misc3", dest = "misc3",
            help = "miscellaneous option 3 (depends on program)",
            default = "")
    
        parser.add_option(
            "--misc4", dest = "misc4",
            help = "miscellaneous option 4 (depends on program)",
            default = "")
    
        parser.add_option(
            "--misc5", dest = "misc5",
            help = "miscellaneous option 5 (depends on program)",
            default = "")
    
        options, args = parser.parse_args()
    
        # print "Options:", options
        # print "Args   :", args
        # print dir(options)
        return options, args
        
    def get_exp_file(self):
        return self.exp_file
    
    def get_pas_file(self):
        return self.pas_file
    
    def get_wab_file(self):
        return self.wab_file
    
    def get_normal_keys(self):
        return self.normal_keys
    
    def get_cancer_keys(self):
        return self.cancer_keys

    def get_min_thres(self):
        return self.min_thres
    
    def get_filtering_level(self):
        return self.filtering_level

    def get_misc1(self):
        return self.options.misc1

    def get_misc2(self):
        return self.options.misc2

    def get_misc3(self):
        return self.options.misc3
       
    def get_args(self):
        return self.args
        
        

class Option_Cancer11kII:
    def __init__(self):
        options, args = self.__parsing()
        
        if options.CancerType == "colon":
            self.cancer_keys = colon_cancer_keys
            self.normal_keys = colon_normal_keys
            self.exp_file_dt = rsc.Human11k_Cancer_Colon_dT
            self.exp_file_rd = rsc.Human11k_Cancer_Colon_random
            self.pas_file_dt = rsc.Human11k_Cancer_Colon_gIsPosAndSignif_dT
            self.pas_file_rd = rsc.Human11k_Cancer_Colon_gIsPosAndSignif_random
            self.wab_file_dt = rsc.Human11k_Cancer_Colon_gIsWellAboveBG_dT
            self.wab_file_rd = rsc.Human11k_Cancer_Colon_gIsWellAboveBG_random
    
        elif options.CancerType == "hepatic":
            self.cancer_keys = hepatic_cancer_keys
            self.normal_keys = hepatic_normal_keys
            self.exp_file_dt = rsc.Human11k_Cancer_Hepatic_dT
            self.exp_file_rd = rsc.Human11k_Cancer_Hepatic_random
            self.pas_file_dt = rsc.Human11k_Cancer_Hepatic_gIsPosAndSignif_dT
            self.pas_file_rd = rsc.Human11k_Cancer_Hepatic_gIsPosAndSignif_random
            self.wab_file_dt = rsc.Human11k_Cancer_Hepatic_gIsWellAboveBG_dT
            self.wab_file_rd = rsc.Human11k_Cancer_Hepatic_gIsWellAboveBG_random
    
        self.min_thres       = float(options.minexp)
        self.filtering_level = float(options.filter)
       
        self.options = options
        self.args    = args
       
    def __parsing(self):

        usage = "Usage: %prog [ options ] arguments"
        parser = OptionParser(usage)
        parser.add_option(
            "-c", "--CancerType", dest = "CancerType",
            help = "determines cancer cell type (colon or hepatic)",
            default = "colon")
    
        parser.add_option(
            "--minexp", dest = "minexp",
            help = "Minimum expression level (Filtering level > 0 required)",
            default = 0.0)    
    
        parser.add_option(
            "--filter", dest = "filter",
            help = "Filtering level of expression data",
            default = 0)
    
        parser.add_option(
            "--misc1", dest = "misc1",
            help = "miscellaneous option 1 (depends on program)",
            default = "")
    
        parser.add_option(
            "--misc2", dest = "misc2",
            help = "miscellaneous option 2 (depends on program)",
            default = "")
    
        parser.add_option(
            "--misc3", dest = "misc3",
            help = "miscellaneous option 3 (depends on program)",
            default = "")
    
        parser.add_option(
            "--misc4", dest = "misc4",
            help = "miscellaneous option 4 (depends on program)",
            default = "")
    
        parser.add_option(
            "--misc5", dest = "misc5",
            help = "miscellaneous option 5 (depends on program)",
            default = "")
    
        (options, args) = parser.parse_args()
    
        # print "Options:", options
        # print "Args   :", args
        # print dir(options)
        
        return options, args  

    def get_exp_file_dt(self):
        return self.exp_file_dt

    def get_exp_file_rd(self):
        return self.exp_file_rd
    
    def get_pas_file_dt(self):
        return self.pas_file_dt

    def get_pas_file_rd(self):
        return self.pas_file_rd
    
    def get_wab_file_dt(self):
        return self.wab_file_dt
 
    def get_wab_file_rd(self):
        return self.wab_file_rd
    
    def get_normal_keys(self):
        return self.normal_keys
    
    def get_cancer_keys(self):
        return self.cancer_keys
    
    def get_min_thres(self):
        return self.min_thres
    
    def get_filtering_level(self):
        return self.filtering_level
    
    def get_misc1(self):
        return self.options.misc1

    def get_misc2(self):
        return self.options.misc2
    
    def get_args(self):
        return self.args


if __name__ == "__main__":
    print "*** Option #1 ***"
    print "Exp. file:", Option_Cancer11kI().get_exp_file()
    print "PAS. file:", Option_Cancer11kI().get_pas_file()
    print "WAB. file:", Option_Cancer11kI().get_wab_file()
    print "Nrm. keys:", Option_Cancer11kI().get_normal_keys()
    print "Can. keys:", Option_Cancer11kI().get_cancer_keys()
    print "Min thres:", Option_Cancer11kI().get_min_thres()
    print "Flt level:", Option_Cancer11kI().get_filtering_level()
    print
    print "*** Option #2 ***"
    print "Exp. file (dT):", Option_Cancer11kII().get_exp_file_dt()
    print "Exp. file (Rd):", Option_Cancer11kII().get_exp_file_rd()
    print "PAS. file (dT):", Option_Cancer11kII().get_pas_file_dt()
    print "PAS. file (Rd):", Option_Cancer11kII().get_pas_file_rd()
    print "WAB. file (dT):", Option_Cancer11kII().get_wab_file_dt()
    print "WAB. file (Rd):", Option_Cancer11kII().get_wab_file_rd()
    print "Nrm. keys:", Option_Cancer11kII().get_normal_keys()
    print "Can. keys:", Option_Cancer11kII().get_cancer_keys()
    print "Min thres:", Option_Cancer11kII().get_min_thres()
    print "Flt level:", Option_Cancer11kII().get_filtering_level()
    
#!/usr/bin/env python

from optparse import OptionParser

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")

from SAT_Packages.SAT11K.Human_Cancer11k_Global import *


def OptParse_celltype_prim():

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

    (options, args) = parser.parse_args()

    # print "Options:", options
    # print "Args   :", args
    # print dir(options)

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

    return exp_file, pas_file, wab_file, normal_keys, cancer_keys, args

def OptParse_celltype_prim_misc():

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

    return exp_file, pas_file, wab_file, normal_keys, cancer_keys, \
        (options.misc1, 
         options.misc2, 
         options.misc3, 
         options.misc4, 
         options.misc5), args


def OptParse_celltype_prim_II():

    usage = "Usage: %prog [ options ] arguments"
    parser = OptionParser(usage)
    parser.add_option(
        "-c", "--CancerType", dest = "CancerType",
        help = "determines cancer cell type (colon or hepatic)",
        default = "colon")

    (options, args) = parser.parse_args()

    # print "Options:", options
    # print "Args   :", args
    # print dir(options)

    if options.CancerType == "colon":
        cancer_keys = colon_cancer_keys
        normal_keys = colon_normal_keys
        exp_file_dt = rsc.Human11k_Cancer_Colon_dT
        exp_file_rd = rsc.Human11k_Cancer_Colon_random
        pas_file_dt = rsc.Human11k_Cancer_Colon_gIsPosAndSignif_dT
        pas_file_rd = rsc.Human11k_Cancer_Colon_gIsPosAndSignif_random
        wab_file_dt = rsc.Human11k_Cancer_Colon_gIsWellAboveBG_dT
        wab_file_rd = rsc.Human11k_Cancer_Colon_gIsWellAboveBG_random

    elif options.CancerType == "hepatic":
        cancer_keys = hepatic_cancer_keys
        normal_keys = hepatic_normal_keys
        exp_file_dt = rsc.Human11k_Cancer_Hepatic_dT
        exp_file_rd = rsc.Human11k_Cancer_Hepatic_random
        pas_file_dt = rsc.Human11k_Cancer_Hepatic_gIsPosAndSignif_dT
        pas_file_rd = rsc.Human11k_Cancer_Hepatic_gIsPosAndSignif_random
        wab_file_dt = rsc.Human11k_Cancer_Hepatic_gIsWellAboveBG_dT
        wab_file_rd = rsc.Human11k_Cancer_Hepatic_gIsWellAboveBG_random

    return (exp_file_dt, exp_file_rd,
            pas_file_dt, pas_file_rd,
            wab_file_dt, wab_file_rd,
            normal_keys, cancer_keys, args)

if __name__ == "__main__":

    # print OptParse_celltype_prim()
    print OptParse_celltype_prim_misc()
    # print OptParse_celltype_prim_II()


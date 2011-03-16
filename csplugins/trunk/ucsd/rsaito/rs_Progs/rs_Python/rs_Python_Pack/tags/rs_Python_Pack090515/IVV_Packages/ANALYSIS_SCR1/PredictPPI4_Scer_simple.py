#!/usr/bin/env python

import sys
sys.path.append("../")

import string

from PPI_Pred.PPIPred3 import PPIPred2
import IVV_info.IVV_info
import Homology.Homology3_descr
import Data_Struct.Hash_A

from Usefuls.rsConfig import RSC

config_file = "../../../rsIVV_Config"
rsc = RSC(config_file)

bait_thres = 1.0e-30
prey_thres = 1.0e-3

rep_thres = 1

sys.stderr.write("Reading IVV information...\n")
filter = IVV_info.IVV_filter.IVV_filter1()
filter.set_Prey_filter_file(rsc.PreyFilter)
ivv_info = IVV_info.IVV_info.IVV_info(rsc.IVVInfo) #, filter)

sys.stderr.write("Reading homology information...\n")
homol_ivv_to_refseq = Homology.Homology3_descr.HomologyDescr3(
    rsc.HomolIVVScer)

ivv_pred = PPIPred2(ivv_info, mode = "S")

sys.stderr.write("Reading ID Conversion files...\n")
ivv_pred.set_mapping(homol_ivv_to_refseq,
                     rsc.Gene2RefSeq_Scer,
                     bait_thres, prey_thres,
                     taxonid = "4932")

sys.stderr.write("IVV -> Gene Calculation...\n")
ivv_pred.set_reprod_thres(rep_thres)
ivv_pred.ivv_to_convid()

spoke = ivv_pred.get_spoke()
for p1 in spoke:
    for p2 in spoke[p1]:
        print p1, p2, spoke[p1][p2]



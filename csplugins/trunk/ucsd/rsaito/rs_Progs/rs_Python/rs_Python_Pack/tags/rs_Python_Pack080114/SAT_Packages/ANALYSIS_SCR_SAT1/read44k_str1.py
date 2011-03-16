#!/usr/bin/env python

import Usefuls.rsConfig
import SAT_Packages.SAT44K_Info.sas
import cPickle

rsc_antis = Usefuls.rsConfig.RSC_II("rsAntis_Config")

all_info = cPickle.load(open(rsc_antis.human44k_str, "r"))

count = 0
for elem in all_info.get_all_SAT():
    print elem
    count += 1
    if count > 10: break
    

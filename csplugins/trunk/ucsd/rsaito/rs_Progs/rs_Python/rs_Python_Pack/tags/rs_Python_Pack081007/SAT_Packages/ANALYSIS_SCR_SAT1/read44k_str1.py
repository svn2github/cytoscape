#!/usr/bin/env python

import Usefuls.rsConfig
import SAT_Packages.SAT44K_Info.sas
import cPickle

rsc_antis = Usefuls.rsConfig.RSC_II("rsSAT_Config")

all_info = cPickle.load(open(rsc_antis.human44k_str, "r"))

count = 0
for sas in all_info.get_all_SAT():
    # if count == 0:
    #    print dir(sas)
    sas_id         = sas.get_ID()
    plus_isoforms  = sas.get_cluster('plus').get_all_isoforms()
    minus_isoforms = sas.get_cluster('minus').get_all_isoforms()

    plus_ids = map(lambda x: x.get_ID(), plus_isoforms)
    minus_ids = map(lambda x: x.get_ID(), minus_isoforms)
    
    print sas_id, plus_ids, minus_ids
    
    count += 1
    if count >=5 : break


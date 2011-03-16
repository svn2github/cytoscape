#!/usr/bin/env python

from Usefuls.rsConfig import RSC_II
import cPickle

rsc_antis = RSC_II("rsSAT_Config")

exp_info = cPickle.load(open(rsc_antis.human44k_dT, "r"))
for cond in exp_info.get_conditions():
    print cond
pids = exp_info.get_probes()
print pids[0], exp_info.get_expression_of_probe(pids[0])


#!/usr/bin/env python

from Usefuls.rsConfig import RSC_II
import cPickle

import Usefuls.Table_maker

rsc_antis = RSC_II("rsSAT_Config")

exp_info = cPickle.load(open(rsc_antis.human44k_random, "r")) # rsc_antis.human44k_random
# print exp_info.get_conditions()
pids = exp_info.get_probes()
# print pids[0], exp_info.get_expression_of_probe(pids[0])

output = Usefuls.Table_maker.Table_row()

for pid in pids:
    for cond in exp_info.get_conditions():
        idx_cond  = exp_info.condition2idx[ cond ]
        idx_probe = exp_info.probeID2idx[ pid ]
        expr = exp_info.values[idx_cond, idx_probe]
        output.append("Probe ID", pid)
        output.append(cond.__str__().replace('\t', " "), "%.6f" % expr)
    output.output("\t")
    


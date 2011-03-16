#!/usr/bin/env python

import sys
sys.path.append("./rsIVV_Python3")

import PPI.PPi2
import Data_Struct.NonRedSet
import string

ppifile = sys.argv[1]

ppi = PPI.PPi2.PPi2()
ppi.read_from_file2(ppifile, 0, 1, "PPI")
ppi.both_dir()

# ppi.ppi_display()

n_partner_distr = Data_Struct.NonRedSet.NonRedSetDict()

for protein in ppi.get_proteins():
    interactor = ppi.interactor(protein)
    n_interact = len(interactor)
    n_partner_distr.append_Dict(`n_interact`, protein)
#    print protein, ppi.interactor(protein)

for n_partner in n_partner_distr.keys():
    print n_partner + "\t" + `len(n_partner_distr.ret_set_Dict(n_partner))` + \
	"\t" + string.join(n_partner_distr.ret_set_Dict(n_partner),
		    ",")

#!/usr/bin/env python

import sys
sys.path.append("./rsIVV_Python3")

import string
import IVV_info
import IVV_filter
import IVV_Conv
import PPI.PPi2
import Expr.Express1
import Usefuls.Histogram

ivv_info_file = "./IVV/ivv_human7.3_info"
ivv_prey_filter = "./basic_filter_list1"
expfile = "Exp_data/SymAtlas_human_CD"

iteration = 1

sys.stderr.write("Reading IVV information...\n")
filter = IVV_filter.IVV_filter1()
filter.set_Prey_filter_file(ivv_prey_filter)
ivv_info = IVV_info.IVV_info(ivv_info_file) # , filter)

sys.stderr.write("Reading Expression information...\n")
exp = Expr.Express1.SymAtlas(expfile)
hist = Usefuls.Histogram.Hist(-1, 1, 20)

for i in range(iteration):
    sys.stderr.write("Iteration #" + `i` + " \n")
#    ivv_info.Prey_info().shuffle_prey_bait_relations(10000)

    ivv_gene = IVV_Conv.IVV_Conv(ivv_info, mode = "S")
    ivv_gene.set_reprod_thres(1)
    ivv_gene.ivv_to_convid()
    spoke = ivv_gene.get_spoke()

    ppi = PPI.PPi2.PPi2()
    ppi.read_dict(spoke)
    ppi.both_dir()

    for p1, p2, val in ppi.get_non_redu_ppi():
	corr = exp.corr(p1, p2)
	if corr != False: hist.add(corr)
	#    print p1, p2, corr

hist.display_rate()


#!/usr/bin/env python

import sys
sys.path.append("../")

import string
import IVV_info.IVV_info
import IVV_info.IVV_filter
import IVV_info.IVV_Conv
import PPI.PPi2
import Expr.GEO_exp1
import Expr.GEO_probe1
import Usefuls.Histogram

ivv_info_file = "../../IVV/ivv_human8.0_info"
ivv_prey_filter = "../../IVV/basic_filter_list2"

probe_file= "../../Exp_data/GEO/GDS1414/GPL96-14367.txt"
exp_file0 = "../../Exp_data/GEO/GDS1414/exp_data/GSM28995"
exp_file1 = "../../Exp_data/GEO/GDS1414/exp_data/GSM28998"
exp_file2 = "../../Exp_data/GEO/GDS1414/exp_data/GSM29001"
exp_file3 = "../../Exp_data/GEO/GDS1414/exp_data/GSM29004"

iteration = 1

sys.stderr.write("Reading IVV information...\n")
filter = IVV_info.IVV_filter.IVV_filter1()
filter.set_Prey_filter_file(ivv_prey_filter)
ivv_info = IVV_info.IVV_info.IVV_info(ivv_info_file, filter)

sys.stderr.write("Reading Expression information...\n")

probe_info = Expr.GEO_probe1.Probe_Info(probe_file)

geo_exp_set = Expr.GEO_exp1.GEOexp_set()
geo_exp_set.set_exp_file(exp_file0, "EXP0")
geo_exp_set.set_exp_file(exp_file1, "EXP1")
geo_exp_set.set_exp_file(exp_file2, "EXP2")
geo_exp_set.set_exp_file(exp_file3, "EXP3")

geo_exp_set_geneid = Expr.GEO_exp1.GEOexp_set_geneid(probe_info, geo_exp_set)

hist = Usefuls.Histogram.Hist(-1, 1, 20)

for i in range(iteration):
    sys.stderr.write("Iteration #" + `i` + " \n")
#    ivv_info.Prey_info().shuffle_prey_bait_relations(10000)

    ivv_gene = IVV_info.IVV_Conv.IVV_Conv(ivv_info, mode = "S")
    ivv_gene.set_reprod_thres(1)
    ivv_gene.ivv_to_convid()
    spoke = ivv_gene.get_spoke()

    ppi = PPI.PPi2.PPi2()
    ppi.read_dict(spoke)
    ppi.both_dir()

    for p1, p2, val in ppi.get_non_redu_ppi():
        if p1 != p2:
            corr = geo_exp_set_geneid.corr(p1, p2)
            if corr != False: hist.add(corr)
            # print p1, p2, corr

# hist.display()
hist.display_rate()


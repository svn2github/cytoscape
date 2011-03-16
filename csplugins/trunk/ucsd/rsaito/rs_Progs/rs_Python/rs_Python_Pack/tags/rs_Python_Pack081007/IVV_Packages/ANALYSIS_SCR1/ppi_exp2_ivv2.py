#!/usr/bin/env python

import sys
sys.path.append("../")

import string
import random

from IVV_info.IVV_info import IVV_info
from IVV_info.IVV_filter import IVV_filter1
from IVV_info.IVV_Conv import IVV_Conv
import PPI.PPi2
from Expr.SymAtlas_WEB import SymAtlas
import Usefuls.Histogram

ivv_info_file = "../../IVV/ivv_human8.0_info"
ivv_prey_filter = "../../IVV/basic_filter_list2"

gpl96_file = "../../Exp_data/SymAtlas/WEB/GPL96-14367.txt"
gnf1b_anntable_file = "../../Exp_data/SymAtlas/WEB/gnf1b-anntable.txt"
symatlas_file = "../../Exp_data/SymAtlas/WEB/GNF1Hdata.txt"

sys.stderr.write("Reading Expression information...\n")
exp = SymAtlas(symatlas_file, gpl96_file, gnf1b_anntable_file)

hist = Usefuls.Histogram.Hist(-1, 1, 20)

random_mode = True

if random_mode is False:
    sys.stderr.write("Reading IVV information...\n")
    filter = IVV_filter1()
    filter.set_Prey_filter_file(ivv_prey_filter)
    ivv_info = IVV_info(ivv_info_file, filter)

    iteration = 1

    for i in range(iteration):
        sys.stderr.write("Iteration #" + `i` + " \n")
        #  ivv_info.Prey_info().shuffle_prey_bait_relations(10000)

        ivv_gene = IVV_Conv(ivv_info, mode = "S")
        ivv_gene.set_reprod_thres(1)
        ivv_gene.ivv_to_convid()
        spoke = ivv_gene.get_spoke()

        ppi = PPI.PPi2.PPi2()
        ppi.read_dict(spoke)
        ppi.both_dir()

        for p1, p2, val in ppi.get_non_redu_ppi():
            corr = exp.corr(p1, p2)
            if p1 != p2 and corr != False: hist.add(corr)
            # print p1, p2, corr

else:
    genes = exp.genes()

    for i in range(10000):
    
        rand1 = random.randrange(0,len(genes))
        rand2 = random.randrange(0,len(genes))

        gene1 = genes[ rand1 ]
        gene2 = genes[ rand2 ]

        if (gene1 != gene2):
            hist.add(exp.corr(gene1, gene2))

hist.display_rate()


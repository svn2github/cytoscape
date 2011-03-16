#!/usr/bin/env python

import sys
sys.path.append("../")

import random
import Usefuls.Histogram

from Expr.GEO_probe1 import Probe_Info
from Expr.GEO_exp1 import *

probe_info_file = "../../Exp_data/GEO/GDS1414/GPL96-14367.txt"
exp_file0 = "../../Exp_data/GEO/GDS1414/exp_data/GSM28995"
exp_file1 = "../../Exp_data/GEO/GDS1414/exp_data/GSM28998"
exp_file2 = "../../Exp_data/GEO/GDS1414/exp_data/GSM29001"
exp_file3 = "../../Exp_data/GEO/GDS1414/exp_data/GSM29004"

probe_info = Probe_Info(probe_info_file)

geo_exp_set = GEOexp_set()
geo_exp_set.set_exp_file(exp_file0, "EXP0")
geo_exp_set.set_exp_file(exp_file1, "EXP1")
geo_exp_set.set_exp_file(exp_file2, "EXP2")
geo_exp_set.set_exp_file(exp_file3, "EXP3")

geoexp_set_geneid = GEOexp_set_geneid(probe_info, geo_exp_set)

hist = Usefuls.Histogram.Hist(-1, 1, 20)

genes = geoexp_set_geneid.genes()

for i in range(10000):
    
    rand1 = random.randrange(0,geoexp_set_geneid.row_num())
    rand2 = random.randrange(0,geoexp_set_geneid.row_num())

    gene1 = genes[ rand1 ]
    gene2 = genes[ rand2 ]

    if gene1 != gene2:
        # print gene1, gene2, geoexp_set_geneid.corr(gene1, gene2)
        hist.add(geoexp_set_geneid.corr(gene1, gene2))

hist.display_rate()

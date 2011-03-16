#!/usr/bin/env python

import sys
sys.path.append("./rsIVV_Python2")

import string
import PPI.PPi2
import Expr.Express1

ppifile = sys.argv[1]
expfile = "Exp_data/SymAtlas_human_CD"

ppi = PPI.PPi2.PPi2()
ppi.read_from_file(ppifile, 0, 1, 2)
ppi.both_dir()

exp = Expr.Express1.SymAtlas(expfile)

for p1, p2, val in ppi.get_non_redu_ppi():
    corr = exp.corr(p1, p2)
    if corr != False:
        print string.join((p1, p2, string.join(val, ","), `corr`), "\t")


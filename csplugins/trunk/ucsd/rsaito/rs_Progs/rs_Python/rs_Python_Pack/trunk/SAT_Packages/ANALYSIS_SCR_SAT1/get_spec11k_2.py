#!/usr/bin/env python

import SAT_Packages.SAT11K.Read11k1 as Read11k
import Calc_Packages.Math.Math_SAT1 as Math_SAT
import SAT_Packages.SAT.SAT_gnuplot1 as SAT_gnuplot

expr_pat_set, sat_set = Read11k.read_mouse11k_nerve_random()

for sat in sat_set.get_sats():
    tr1 = sat.get_transcript1()
    tr2 = sat.get_transcript2()

    expr1 = tr1.expression_pat_single_probe()
    expr2 = tr2.expression_pat_single_probe()

    spec = Math_SAT.select_z_abs_min_sgn(expr1, expr2)

    if max(expr1) >= 300 and max(expr2) >= 300 and min(spec) <= -1.5:
        print spec, min(spec), max(spec)
        sat_plot = SAT_gnuplot.SAT_gnuplot()
        sat_plot.import_sat(sat)
        sat_plot.gnuplot_line()




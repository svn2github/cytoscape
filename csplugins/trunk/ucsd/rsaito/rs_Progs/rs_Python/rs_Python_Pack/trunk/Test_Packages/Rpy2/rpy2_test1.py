#!/usr/bin/env python

""" 
You must install R (probably with ./configure --enable-R-shlib)
and rpy2.

wilcox.test do not function appropriately if decimal is used.
Check http://aoki2.si.gunma-u.ac.jp/R/wilcox-paired.html.
"""

import rpy2.robjects as robjects
r = robjects.r

d1 = (0.8, 0.7, 0.8, 1.1, 0.8, 1.0, 0.4, 0.6, 1.0, 0.9, 0.7, 0.7, 0.7, 0.6, 0.9, 1.0, 0.9, 0.4, 1.1, 1.0)
d2 = (0.8, 0.6, 0.9, 1.0, 1.0, 1.1, 0.5, 0.5, 0.9, 1.0, 0.5, 0.6, 0.8, 0.6, 0.8, 0.9, 0.8, 0.7, 1.1, 1.1)

v1 = robjects.FloatVector(d1)
v2 = robjects.FloatVector(d2)

print r['wilcox.test'](v1, v2, paired = True)
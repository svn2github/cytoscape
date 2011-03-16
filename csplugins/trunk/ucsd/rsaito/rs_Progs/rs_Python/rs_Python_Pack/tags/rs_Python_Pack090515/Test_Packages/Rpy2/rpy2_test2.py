#!/usr/bin/env python

""" 
You must install R (probably with ./configure --enable-R-shlib)
and rpy2.

wilcox.test do not function appropriately if decimal is used.
Check http://aoki2.si.gunma-u.ac.jp/R/wilcox-paired.html.
"""

import rpy2.robjects as robjects
r = robjects.r

d1 = (56,68,55,79,80,61,79,90,83,62)
d2 = (70,68,60,80,75,69,83,87,77,67)

d1 = (3,16,4,3,4,7,1,2,3,7,10,4)
d2 = (5,8,7,3,5,2,7,11,10,2,3,14)

v1 = robjects.FloatVector(d1) # Not IntVector
v2 = robjects.FloatVector(d2) # Not IntVector

print r['wilcox.test'](v1, v2, paired = True)
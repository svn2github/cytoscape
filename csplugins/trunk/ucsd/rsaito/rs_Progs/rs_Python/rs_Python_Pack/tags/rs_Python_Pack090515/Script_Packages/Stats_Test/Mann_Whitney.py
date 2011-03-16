#!/usr/bin/env python

""" 
You must install R (probably with ./configure --enable-R-shlib)
and rpy2.

wilcox.test do not function appropriately if decimal is used.
Check http://aoki2.si.gunma-u.ac.jp/R/wilcox-paired.html.
"""

import sys
import rpy2.robjects as robjects
r = robjects.r

if len(sys.argv) < 3:
    print "* Sample Data *"
    sample1 = (4.0, 2.7, 2.2, 1.9, 1.9, 1.8, 1.7, 1.7, 1.7, 1.6, 1.3, 1.1, 0.8)
    sample2 = (4.7, 3.8, 3.6, 2.9, 2.8, 2.2, 1.7)

else:
    print "* Real Data *"
    sample1 = map(lambda x: float(x), open(sys.argv[1]).readlines())
    sample2 = map(lambda x: float(x), open(sys.argv[2]).readlines()) 

# print "Sample 1", sample1
# print "Sample 2", sample2

v1 = robjects.FloatVector(sample1) # Not IntVector
v2 = robjects.FloatVector(sample2) # Not IntVector

print "Sample 1", v1
print "Sample 2", v2
result = r['wilcox.test'](v1, v2)
print result
print result.r['p.value']
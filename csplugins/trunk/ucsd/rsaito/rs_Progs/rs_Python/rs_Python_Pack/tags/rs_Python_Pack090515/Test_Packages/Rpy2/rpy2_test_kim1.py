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

def read_two_column_file(filename):
    d1 = []
    d2 = []
    fh = open(filename, "r")
    for line in fh:
        n1_str, n2_str = line.split("\t")
        n1, n2 = int(n1_str), int(n2_str)
        d1.append(n1)
        d2.append(n2)
    return tuple(d1), tuple(d2)

d1, d2 = read_two_column_file(sys.argv[1])

v1 = robjects.FloatVector(d1) # Not IntVector
v2 = robjects.FloatVector(d2) # Not IntVector

print r['wilcox.test'](v1, v2, paired = True)

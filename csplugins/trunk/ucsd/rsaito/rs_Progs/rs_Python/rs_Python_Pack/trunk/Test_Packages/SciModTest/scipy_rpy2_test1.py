#!/usr/bin/env python

import rpy2.robjects as robjects
r = robjects.r

# print r
# print r.pnorm(0.0)
# print r('pnorm')(0.0)
# print r.pnorm(0.0)[0]

print r.pnorm(1.96)
print r('pnorm')(1.96)
print r.pnorm(1.96)[0]

print r.pchisq(16.27, 3)

from scipy.stats import *

print norm.cdf(1.96)
print chi2.cdf(16.27, 3)
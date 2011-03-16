#!/usr/bin/env python

import rpy2.robjects as robjects
r = robjects.r

# print r
print r.pnorm(0.0)
print r('pnorm')(0.0)
print r.pnorm(0.0)[0]

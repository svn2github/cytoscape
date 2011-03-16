#!/usr/bin/env python

import sys
import string

from scipy.stats import *

df = "3" # sys.argv[1]

print df
for i in range(100 + 1):
    print i * 0.1, chi2.pdf(i * 0.1, string.atoi(df))
    
print

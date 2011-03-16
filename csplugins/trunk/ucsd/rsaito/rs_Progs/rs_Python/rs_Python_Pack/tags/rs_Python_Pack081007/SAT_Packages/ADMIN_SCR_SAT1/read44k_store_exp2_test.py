#!/usr/bin/env python

import cPickle

from SAT_Packages.SAT44K_Expr import expmat

mat = expmat.read(['Human', 'dT', 'colon', '1', '2'])

# Example of how to get expression data for a probe.
pids = mat.get_probes()
print mat.get_expression_of_probe(pids[0])

# Example of how to get expression data for a condition.
f = mat.get_conditions()
# files = f.get(fname) # fname instance
files = f.kwget(['Human']) # fname instance
print files
# files are identical(?) to conditions.
# cond = files[0]

idx = mat.condition2idx[files[0]]
print mat.values[idx]
# Above two can be replaced by
mat.get_expression_of_condition(files[0])


# Example of how to normalize the data.
# Float is permitted, i.e. mat.normalize_by(1.0)
# average = mat.normalize_by(files[0])

cPickle.dump(mat, open("pickledump", "w"))



#!/usr/bin/env python

import cPickle

from SAT_Packages.SAT44K_Expr import expmat

mat = expmat.read(['Human', 'dT'])
cPickle.dump(mat, open("Human44k_dT.pkl", "w"))
mat = expmat.read(['Human', 'random'])
cPickle.dump(mat, open("Human44k_random.pkl", "w"))

mat = expmat.read(['Mouse', 'dT'])
cPickle.dump(mat, open("Mouse44k_dT.pkl", "w"))
mat = expmat.read(['Mouse', 'random'])
cPickle.dump(mat, open("Mouse44k_random.pkl", "w"))





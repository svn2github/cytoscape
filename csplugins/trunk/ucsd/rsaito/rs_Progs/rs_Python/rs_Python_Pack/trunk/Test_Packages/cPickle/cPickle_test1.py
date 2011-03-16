#!/usr/bin/env python
'''
Created on Mar 14, 2011

@author: rsaito
'''

import cPickle

test_obj = { "Apple"  : "Ringo",
             "Banana" : "Banana",
             "Candy"  : "Ame" }

print test_obj

fh = open("/tmp/cPickle_test1", "wb")
cPickle.dump(test_obj, fh, True) # True : binary mode?
fh.close()

fh_r = open("/tmp/cPickle_test1", "rb")
test_obj_r = cPickle.load(fh_r)

print test_obj_r

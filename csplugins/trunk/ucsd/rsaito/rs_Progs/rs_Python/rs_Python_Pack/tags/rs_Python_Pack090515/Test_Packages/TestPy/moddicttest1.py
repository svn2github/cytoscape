#!/usr/bin/env python

import sys

"""

M.name
M.__dict__['name']
sys.modules['M'].name
getattr(M, 'name')

"""

gvar = 100

def testfunc1():

    print "Hello"

def testfunc2():

    print "Hi!"

print sys.modules[ __name__ ].__dict__

sys.modules[ __name__ ].__dict__[ "Rintaro" ] = 35

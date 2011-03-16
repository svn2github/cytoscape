#!/usr/bin/env python

# a = {}
# print a[b]

class A:
    pass

try:
    print A().b
except AttributeError, msg:
    print AttributeError.__dict__
    print dir(msg)
    print msg.message
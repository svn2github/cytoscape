#!/usr/bin/env python

class Exc1:
    def __init__(self, str):
        self.str = str

    def mesg(self):
        return self.str

class Exc_sub1(Exc1):
    pass


def testfunc():
    raise Exc_sub1, Exc_sub1("Hello")

def testfunc2():
    raise Exc_sub1("Hi!!!")

"""
try:
    testfunc()
except Exc1, descr:
    print "Exception", descr.mesg()
"""

try:
    testfunc2()
except Exc1, descr:
    print "Exception", descr.mesg()
    
try:
    raise Exception("This is a test.")
except Exception, descr:
    print descr
    
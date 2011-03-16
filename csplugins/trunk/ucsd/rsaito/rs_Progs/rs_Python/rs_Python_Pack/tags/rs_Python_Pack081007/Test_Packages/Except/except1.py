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

try:
    testfunc()
except Exc1, descr:
    print "Exception", descr.mesg()

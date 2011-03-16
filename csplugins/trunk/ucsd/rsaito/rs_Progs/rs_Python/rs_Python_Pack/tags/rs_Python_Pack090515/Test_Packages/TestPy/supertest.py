#!/usr/bin/env python

class B(object):
    def meth(self, param):
        print "B:", param

class C(B):  
    def meth(self, param):
        super(C, self).meth(param)
        # B.meth(self, param)
        print "C:", param

if __name__ == "__main__":
    c = C()
    c.meth("Rintaro")

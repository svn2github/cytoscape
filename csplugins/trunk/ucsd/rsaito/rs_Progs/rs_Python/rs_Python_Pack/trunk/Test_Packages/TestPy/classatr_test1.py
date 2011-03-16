#!/usr/bin/env python

########
### This is a test.

class A:
    var = "Rintaro"

    def test1(self):
        print self.var

class A_sub(A):
    var = "Saito"

if __name__ == "__main__":
    a1 = A()
    a2 = A()
    a1.test1()
    b = A_sub()
    b.test1()
    a1.test1()

    print "#####"
    A.var = "RRR"
    # Also try a1.var = "RRR"
    a1.test1()
    a2.test1()
    b.test1()

    print "#####"
    a1.var = "XXX"
    print A.var

    


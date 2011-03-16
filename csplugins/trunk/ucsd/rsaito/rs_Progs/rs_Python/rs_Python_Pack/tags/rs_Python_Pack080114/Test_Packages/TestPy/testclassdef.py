#!/usr/bin/env python

def testdef():
    print "Hello!"

class TestClass:
    def testmethod(self):
        testdef()

if __name__ == "__main__":
    testinst = TestClass()
    testinst.testmethod()

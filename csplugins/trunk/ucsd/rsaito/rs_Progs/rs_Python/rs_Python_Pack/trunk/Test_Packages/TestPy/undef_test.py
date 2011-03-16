#!/usr/bin/env python

class TestClass:
    def __init__(self, variable):
        self.variable = variable

    def testmethod(self):
        if variable == "Hi!":
            print "Hello!"

if __name__ == "__main__":
    testclass = TestClass(variable = "Hi!")
    testclass.testmethod()
    

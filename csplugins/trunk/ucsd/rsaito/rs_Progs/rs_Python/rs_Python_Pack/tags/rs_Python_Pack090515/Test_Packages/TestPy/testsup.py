#!/usr/bin/env python

class TestSup:
    def testfunc(self):
        self.display()
    
    def display(self):
        print "Super Class!!"

class TestSub(TestSup):
    def display(self):
        print "Sub Class!"

if __name__ == "__main__":
    inst = TestSub()
    inst.testfunc()
    

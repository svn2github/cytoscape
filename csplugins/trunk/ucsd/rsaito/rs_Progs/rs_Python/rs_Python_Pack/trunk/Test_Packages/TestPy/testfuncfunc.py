#!/usr/bin/env python

def testfunc():
    x = 10
    
    def testfuncfunc():
        # x = 5
        print x
        
    testfuncfunc()
    print x

testfunc()

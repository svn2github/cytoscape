#!/usr/bin/env python

class Test_Init_Class:
    def __init__(self, init_value = ""):
        self.test_attr = init_value
        print "Initialization finished.", init_value

    def testfunc(self, param):
        pass
    
if __name__ == "__main__":
    ll = Test_Init_Class()
    ll.testfunc("Monday")
    ll.testfunc("Tuesday")
    ll.testfunc("Thursday")

    ll = Test_Init_Class()
    print ll.test_attr
    

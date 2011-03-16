#!/usr/bin/env python

def def_test(param = 0):
    print param
    param += 1

def def_test2(param = []):
    print param
    param += ["X"]
    param = None # Does not work...

def def_test3(param = []):
    print param
    param2 = param
    param2 += ["X"]

def def_test3(param = []):
    print param
    param2 = param[:]
    param2 += ["X"]


if __name__ == "__main__":

    def_test()
    def_test2()
    def_test3()

    def_test()
    def_test2()
    def_test3()

    def_test()
    def_test2()
    def_test3()

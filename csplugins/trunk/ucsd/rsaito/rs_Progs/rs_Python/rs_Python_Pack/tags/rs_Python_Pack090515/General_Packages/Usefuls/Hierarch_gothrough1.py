#!/usr/bin/env python

def squash(array):

    if type(array) == dict:
        work = array.keys()
    else:
        work = array
    
    if type(work) == tuple or type(work) == list:
        ret = []
        for a in work:
            ret = ret + squash(a)
        return ret
    else:
        return [ work ]

def recur_apply(func, array, *param):
    
    if type(array) == dict:
        work = array.keys()
    else:
        work = array
    
    if type(work) == tuple or type(work) == list:
        ret = []
        for a in work:
            ret.append(recur_apply(func, a, *param))
        return ret
    else:
        return func(work, *param)


if __name__ == "__main__":
    
    e = ([ "A", "B", ("C", "D")], ("E", ("F", ("G","H"), "I")))
    print squash(e)

    def mult(x, y): return x * y; 
    def double(x): return x * 2

    f = (1, 2, 3, [ 4, 5, 6 ], [ 7, (8,9), 10 ])
    g = ("1", "2", ("3", "4"), "5")
    print recur_apply(mult, f, 3)
    print recur_apply(double, f)
    print recur_apply(int, g)



#!/usr/bin/env python

def sgn(x):
    if x == 0:
        return 0
    else:
        return 1.0 * x / abs(x)

def sgn_a(ilist):
    return [ sgn(x) for x in ilist ]

def cumulat(ilist):
    ret = []
    total = 0
    for elem in ilist:
        total += elem
        ret.append(total)
    return ret

if __name__ == "__main__":

    print sgn_a([-2, 0, 9, 7.3])
    print cumulat([3, 2, 5, 1])

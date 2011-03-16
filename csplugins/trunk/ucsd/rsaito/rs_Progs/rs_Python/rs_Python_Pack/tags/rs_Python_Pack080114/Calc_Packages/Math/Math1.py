#!/usr/bin/env python

import math

def mean(array):
    return 1.0*sum(array)/len(array)

def var(array):
    u = mean(array)

    total = 0
    for x in array:
        total += (x - u) ** 2
    return total / len(array)

def filter_floats(a1, a2):
    # String -> float conversion

    len_a = min(len(a1), len(a2))

    a1_filt = []
    a2_filt = []

    i = 0
    while i < len_a:
        try:
            e1 = float(a1[i])
            e2 = float(a2[i])
            a1_filt.append(e1)
            a2_filt.append(e2)
            
        except ValueError:
            pass

        i += 1

    return (a1_filt, a2_filt)

def filter_floats2(a1, a2):
    # Checking the object type

    len_a = min(len(a1), len(a2))

    a1_filt = []
    a2_filt = []

    i = 0
    while i < len_a:
	if ((type(a1[i]) == float or type(a1[i]) == int) and
	    (type(a2[i]) == float or type(a2[i]) == int)):
            a1_filt.append(a1[i])
            a2_filt.append(a2[i])
        i += 1

    return (a1_filt, a2_filt)
               
def norm(array):

    u = mean(array)
    s = math.sqrt(var(array))

    norm = []
    for x in array:
        norm.append((x - u) / s)
    
    return norm


def inner_prod(a1, a2):

    if len(a1) != len(a2):
        raise "List size error ..."

    i = 0
    total = 0
    while i < len(a1):
        total += a1[i] * a2[i]
        i += 1
    return total

def prod_abt(a1, a2):

    if len(a1) != len(a2):
        raise "List size error ..."

    i = 0
    res = []
    while i < len(a1):
        res.append(a1[i] * a2[i])
        i += 1
    return res

def corr(a1, a2):

    s_a1 = norm(a1)
    s_a2 = norm(a2)

    return inner_prod(s_a1, s_a2) / len(a1)

def sgn(x):
    if x == 0:
        return 0
    else:
        return 1.0 * x / abs(x)

def sgn_a(ilist):
    return [ sgn(x) for x in ilist ]

if __name__ == "__main__":

    a = (1,3,6,8,9,10)
    b = (1,2,3)
    print "Average:", mean(a)
    print "Variance:", var(a)
    

    a1 = (1,2,9,4,"-",6,7,8)
    a2 = (2,3,4,"-",6,7,0,9)

    a1_f, a2_f = filter_floats(a1, a2)

    print mean(a1_f)
    print mean(a2_f)
    print var(a1_f)
    print var(a2_f)
    print corr(a1_f, a2_f)

    a1 = [2,3,6]
    a2 = [4,2,5]
    print prod_abt(a1, a2)
    print sgn_a([-2, 0, 9, 7.3])


#!/usr/bin/env python

import math
import Vector1

def mean(array):
    return 1.0*sum(array)/len(array)

def var(array):
    u = mean(array)

    total = 0
    for x in array:
        total += (x - u) ** 2
    return total / len(array)

def var_infer(array):
    u = mean(array)

    total = 0
    for x in array:
        total += (x - u) ** 2
    return total / (len(array) - 1)

def sd(array):
    
    return math.sqrt(var(array))

def sd_infer(array):
    
    return math.sqrt(var_infer(array))

def sem(array):
    return sd_infer(array) / math.sqrt(len(array))

def norm(array):

    u = mean(array)
    s = math.sqrt(var(array))

    norm = []
    for x in array:
        norm.append((x - u) / s)
    
    return norm

def corr(a1, a2):
    """ Identical to corr_sd """

    s_a1 = norm(a1)
    s_a2 = norm(a2)

    return Vector1.inner_prod(s_a1, s_a2) / len(a1)


if __name__ == "__main__":

    from Usefuls.Num_filters import filter_floats

    a = (1,3,6,8,9,10,12)
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

    a3 = (75, 68, 83, 90, 65, 77, 62, 80, 72, 78)
    print mean(a3)
    print var(a3)
    print var_infer(a3)
    print sd(a3)
    print sd_infer(a3)
    print sem(a3)


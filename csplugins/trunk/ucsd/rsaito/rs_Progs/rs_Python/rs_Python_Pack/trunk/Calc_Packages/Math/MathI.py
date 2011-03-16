#!/usr/bin/env python

def sgn(x):
    if x == 0:
        return 0
    elif x > 0:
        return 1
    else:
        return -1

def sgn_a(ilist):
    return [ sgn(x) for x in ilist ]

def cumulat(ilist):
    ret = []
    total = 0
    for elem in ilist:
        total += elem
        ret.append(total)
    return ret

def divide_int_near_equal(numerator, denominator):
    quotient    = int(numerator / denominator)
    remainder   = numerator % denominator
    divided_list = [quotient] * denominator
    for i in range(remainder):
        divided_list[i] += 1
    return divided_list

if __name__ == "__main__":

    print sgn_a([-2, 0, 9, 7.3])
    print cumulat([3, 2, 5, 1])
    print divide_int_near_equal(16, 5)
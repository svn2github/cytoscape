#!/usr/bin/env python

from math import log10

def big_diff(ilist):
    inc_rate = -100000
    inc_diff = -100000
    dec_rate =  100000
    dec_diff =  100000
    
    for i in range(len(ilist) - 1):
        rate = log10(1.0*ilist[i+1]/ilist[i])
        if rate > inc_rate:
            inc_rate = rate
            inc_diff = ilist[i+1] - ilist[i]
        if rate < dec_rate:
            dec_rate = rate
            dec_diff = ilist[i+1] - ilist[i]
    
    return inc_rate, inc_diff, dec_rate, dec_diff


if __name__ == "__main__":
    print big_diff((10, 15, 5, 500, 0.5))
    
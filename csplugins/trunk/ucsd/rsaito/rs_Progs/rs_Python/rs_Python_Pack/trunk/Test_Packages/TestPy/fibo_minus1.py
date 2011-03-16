#!/usr/bin/env python

rec = [None] * 2000

def fibo_minus(n):
    if rec[n] is not None:
        return rec[n]
    
    if n == 1:
        ret = 0
    elif n == 2:
        ret = 1
    else:
        ret =  fibo_minus(n-1) - fibo_minus(n-2)

    rec[n] = ret
    return ret
    
if __name__ == "__main__":
    for i in range(30):
        print i+1, fibo_minus(i+1)
    print fibo_minus(1000)
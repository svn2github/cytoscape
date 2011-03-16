#!/usr/bin/env python

def rsInteg_I(func, lower, upper, step, *args, **kwargs):
    x = lower
    sum = 0
    while x < upper:
        y = func(x, *args, **kwargs)
        sum += y*step
        x += step
    return sum

if __name__ == "__main__":
    def f1(x, a, b, c):
        # print x, a, b, c
        return a*x**2 + b*x + c
    print rsInteg_I(f1, 0, 2, 0.0001, 3, 2, 1)
    
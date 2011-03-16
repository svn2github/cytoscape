#!/usr/bin/env python

def factorial(x):

    ret = 1
    if x >= 1:
        for i in range(x):
            ret *= i + 1
    return ret

def combination(n, n1):
    f = factorial
    return f(n) / (f(n1)*f(n-n1))

def binomi(n, n1, p):
    f = factorial
    c = combination
    return p ** n1 * (1-p) ** (n-n1) * c(n, n1)

if __name__ == "__main__":
    # print factorial(4)
    # print combination(6, 3)
    # print binomi(5, 2, .25)

    for i in range(100+1):
        print i, binomi(100, i, 1.0/6)
    print binomi(10, 9, 1.0/6) + binomi(10, 10, 1.0/6)


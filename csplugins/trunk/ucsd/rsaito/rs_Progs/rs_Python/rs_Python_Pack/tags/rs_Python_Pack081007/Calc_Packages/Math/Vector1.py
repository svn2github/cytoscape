#!/usr/bin/env python

def vector_sum(a1, a2):
    if len(a1) != len(a2):
        raise "List size not equal."

    ret = []
    for i in range(len(a1)):
        ret.append(a1[i] + a2[i])

    return ret

def vector_diff(a1, a2):
    if len(a1) != len(a2):
        raise "List size not equal."

    ret = []
    for i in range(len(a1)):
        ret.append(a1[i] - a2[i])

    return ret

def vector_pair(a1, a2, func):
    if len(a1) != len(a2):
        raise "List size not equal."

    ret = []
    for i in range(len(a1)):
        ret.append(apply(func, (a1[i], a2[i])))

    return ret    

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

if __name__ == "__main__":

    print vector_sum([1,2,3], [7, 9, 11])
    print vector_diff([1,2,3], [7, 9, 11])

    a1 = [2,3,6]
    a2 = [4,2,5]
    print prod_abt(a1, a2)
    print inner_prod(a1, a2)

    def mult(x, y):return x * y

    print vector_pair(a1, a2, mult)
    print vector_pair(a1, a2, lambda x, y: x * y)

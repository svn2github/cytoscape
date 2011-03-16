#!/usr/bin/python

def common(list1, list2):
    "Calculates intersection (AND). """
    
    hash1 = {}
    hashc = {}
    
    for elem in list1:
        hash1[ elem ] = ""

    for elem in list2:
        if elem in hash1:
            hashc[ elem ] = ""

    return hashc.keys()

def list_to_dict(list):
    dict = {}
    for elem in list:
        dict[ elem ] = ""
    return dict

def count_key_list(key, list):
    count = 0
    for elem in list:
        if elem == key:
            count += 1
    return count

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

def array_string(ilist):
    return map((lambda x: `x`), ilist)


if __name__ == "__main__":

    a = ["Rin", "Gen", "Warren", "Scott"]
    b = ["Warren", "Warren", "Gen"]
    c = common(a,b)
    print c
    
    print list_to_dict(a)

    d = [ "A", "B", "A", "C", "B", "C", "D", "C" ]

    print count_key_list("C", d)

    e = ([ "A", "B", ("C", "D")], ("E", ("F", ("G","H"), "I")))
    print squash(e)


    def mult(x, y): return x * y; 
    def double(x): return x * 2

    f = (1, 2, 3, [ 4, 5, 6 ], [ 7, (8,9), 10 ])
    g = ("1", "2", ("3", "4"), "5")
    print recur_apply(mult, f, 3)
    print recur_apply(double, f)
    print recur_apply(int, g)

    h = (1.0, 2.0, 3.2)
    print array_string(h)
    

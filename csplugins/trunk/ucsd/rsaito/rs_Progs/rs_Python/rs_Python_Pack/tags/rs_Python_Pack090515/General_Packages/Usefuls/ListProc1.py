#!/usr/bin/env python

def NonRedList(ilist):
    """ In the lateset version of Python, this can be done with
    list(set([a,a,b,b,c,c,c,...])) """

    h = {}
    for elem in ilist:
        h[elem] = True
    return h.keys()

def list_to_dict(ilist):
    dict = {}
    for elem in ilist:
        dict[ elem ] = ""
    return dict

def count_key_list(key, ilist):
    count = 0
    for elem in ilist:
        if elem == key:
            count += 1
    return count

def array_string(ilist):
    return map((lambda x: `x`), ilist)

def array_string_neat(ilist, digits, sep = "\t"):
 
    conv_command = "%%.%df" % digits
    
    ret = []
    for elem in ilist:
        if type(elem) is int or type(elem) is float:
            ret.append(conv_command % elem)
        else:
            ret.append(elem)
    return sep.join(ret)


def map_extI(func, ilist, *args, **kwargs):
    ret = []
    for elem in ilist:
        ret.append(apply(func, (elem, )+ args, kwargs))
    return ret

def reduce_extI(func, ilist, *args, **kwargs):
    ret = {}
    for elem in ilist:
        apply(func, (ret, elem)+ args, kwargs)
    return ret

def list_identical_size_check(arrays):
    for i in range(len(arrays) - 1):
        if len(arrays[i]) != len(arrays[i+1]):
            raise "List size not identical."
    return True

def common(list1, list2, param = 0):
    """ Calculates intersection (AND). 
        It also considers item order. """
    
    hash1 = {}
    hashc = {}
    
    for elem in list1:
        hash1[ elem ] = ""

    for elem in list2:
        if elem in hash1:
            hashc[ elem ] = ""

    if param == 1:
        order_std = list1
    elif param == 2:
        order_std = list2
    else:
        return hashc.keys()

    ret = []
    for item in order_std:
        if item in hashc:
            ret.append(item)
    return ret

def multi_list_intersection(lists):
    if len(lists) == 0:
        return []
    cinter = set(lists[0])
    for alist in lists[1:]:
        cinter = cinter.intersection(set(alist))
    return list(cinter) 

def list_elem_count(lists):
    # Multiple elements in the same list is counted as 1.
    
    elem_c = {}
    for alist in lists:
        for elem in set(alist):
            elem_c[elem] = elem_c.get(elem, 0) + 1

    return elem_c
    

if __name__ == "__main__":

    print multi_list_intersection([["A", "B", "C"],
                                   ["C", "D", "B"],
                                   ["A", "A", "B", "C"],
                                   ["A", "B", "C", "X"]])

    print list_elem_count([["A", "B", "C"],
                           ["C", "D", "B"],
                           ["A", "A", "B", "C"],
                           ["A", "B", "C", "X"],
                           ["A", "A", "B", "C", "D", "E"]])

    print NonRedList(("a", "b", "a", "a", "c"))

    a = ["Rin", "Gen", "Warren", "Scott", "Michael", "John" ]
    b = ["Warren", "Warren", "John", "Kim", "Gen"]
    c = common(a,b)
    print c
    c = common(a,b,1)
    print c
    c = common(a,b,2)
    print c
        
    print list_to_dict(a)

    d = [ "A", "B", "A", "C", "B", "C", "D", "C" ]

    print count_key_list("C", d)

    h = (1.0, 2.0, 3.2)
    print array_string(h)
    
    def functest(x, mult, p):
        return x * mult + p
    def functest2(ret, x, mult, p):
        ret["ans"] = ret.get("ans", 0) + x * mult + p
    
    print map_extI(functest, h, 3, p=1)
    print reduce_extI(functest2, h, 3, p=1)
    
    print array_string_neat([1.3243353, 0.43434, 0.242455, "Hello", "", 0.10000988], 4)
    
    print list_identical_size_check(((1,2,3),(4,5,6),(7,8,9)))
    print list_identical_size_check(((1,2,3),(4,5,6),(7,8,9,10)))
    
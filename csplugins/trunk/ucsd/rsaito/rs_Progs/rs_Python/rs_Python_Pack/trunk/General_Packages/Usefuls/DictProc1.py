#!/usr/bin/env python

import string

def list_count_dict(ilist):
    ret_dict = {}
    for elem in ilist:
        ret_dict[elem] = ret_dict.get(elem, 0) + 1
    return ret_dict

def dict_product(d1, d2, sep = "-"):
    ret_dict = {}
    for k1 in d1:
        for k2 in d2:
            k = k1 + sep + k2
            v = d1[k1] * d2[k2]
            ret_dict[k] = v

    return ret_dict

def list_product(l1, l2, sep = "-"):
    d1 = list_count_dict(l1)
    d2 = list_count_dict(l2)
    return dict_product(d1, d2, sep)

def count_dict_to_str(d, colon, sep):
        tlist = []
        for k in d:
                tlist.append(k + colon + `d[k]`)
        return string.join(tlist, sep)

def count_dict_add(d1, d2):

    ret_d = {}

    for k1 in d1:
        ret_d[k1] = ret_d.get(k1, 0) + d1[k1]
    for k2 in d2:
        ret_d[k2] = ret_d.get(k2, 0) + d2[k2]

    return ret_d

def file_to_dict_simple(filename):
    fh = open(filename, "r")
    ret_dict = {}
    for line in fh:
        if not line.isspace():
            sline = line.rstrip()
            ret_dict[ sline ] = ""
    return ret_dict

def rev_key_val(idict):
    odict = {}
    for k in idict:
        v = idict[k]
        odict[v] = k
    return odict

def rev_key_val_redund(idict):
    odict = {}
    for k in idict:
        v = idict[k]
        if v in odict:
            odict[v].append(k)
        else:
            odict[v] = [k]
    return odict
    

if __name__ == "__main__":

    import TmpFile

    l1 = ["A", "B", "A", "C", "D", "C", "C" ]
    h = list_count_dict(l1)
    print h

    h1 = {"A":3, "B":2, "C":5 }
    h2 = {"D":2, "E":9, "A":7 }
    print dict_product(h1, h2)

    l2 = ["C", "C", "X", "X", "X"]

    print list_count_dict(l1)
    print list_count_dict(l2)
    print list_product(l1, l2)

    print count_dict_to_str({"A": 5, "B":3, "C":2 }, ":", ",")

    h1 = count_dict_add(h1, h2)
    print h1

    tmp_obj = TmpFile.TmpFile_II("""
Monday
Tuesday
Wednesday
""")
    print file_to_dict_simple(tmp_obj.filename())
    
    idict = { "A": 1, "B":2, "C":3, "D":2, "E": 1 }
    print rev_key_val(idict)
    print rev_key_val_redund(idict)

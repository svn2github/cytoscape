#!/usr/bin/env python

def replace_by_dict(s, d):
    ret = s
    ks = d.keys()
    ks.sort()
    for k in ks:
        ret = ret.replace(k, d[k])
    return ret

if __name__ == "__main__":

    print replace("ABCDEF", { "A": "1", "C":"3", "E": "5", "G": "7" }) 
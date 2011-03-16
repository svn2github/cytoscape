#!/usr/bin/env python

def msort(s):
    if len(s) == 2:
        return [min(s), max(s)]
    elif len(s) == 1:
        return s
    else:
        s1 = msort(s[:len(s)/2])
        s2 = msort(s[len(s)/2:])
        return merge(s1, s2)

def merge(s1, s2):
    sr = []
    s1p = 0
    s2p = 0
    while s1p < len(s1) or s2p < len(s2):
        if s2p == len(s2) or (s1p < len(s1) and s1[s1p] < s2[s2p]):
            sr.append(s1[s1p])
            s1p += 1
        else:
            sr.append(s2[s2p])
            s2p += 1
    return sr
    
if __name__ == "__main__":
    print msort((5,2,3,6,9,1))
    print merge([2,5,7], [1,8,10])
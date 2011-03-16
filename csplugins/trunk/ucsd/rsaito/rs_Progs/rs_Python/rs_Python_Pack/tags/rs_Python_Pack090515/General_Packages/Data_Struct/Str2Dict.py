#!/usr/bin/env python

def Str2Dict(str1):
    h = {}
    for c in str1:
        if c in h:
            h[c] +=1
        else:
            h[c] = 1
    return h

if __name__ == "__main__":
    print Str2Dict("abcdefgaabbbcccc")
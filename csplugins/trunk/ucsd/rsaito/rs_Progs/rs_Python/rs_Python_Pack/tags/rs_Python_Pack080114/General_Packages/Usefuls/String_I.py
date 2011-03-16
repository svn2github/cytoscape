#!/usr/bin/env python

def matcher(substr, str):

    ret = []
    for p in range(len(str) - len(substr) + 1):
        # print p, substr, str[p:p+len(substr)]
        if substr == str[p:p+len(substr)]:
            ret.append(p)

    return ret

if __name__ == "__main__":
    print matcher("xxx", "xxxxxx")

    

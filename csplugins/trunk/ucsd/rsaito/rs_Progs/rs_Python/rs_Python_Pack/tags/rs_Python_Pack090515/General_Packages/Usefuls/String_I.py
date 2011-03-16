#!/usr/bin/env python

def matcher(substr, str):

    ret = []
    for p in range(len(str) - len(substr) + 1):
        # print p, substr, str[p:p+len(substr)]
        if substr == str[p:p+len(substr)]:
            ret.append(p)

    return ret

def joiner(ilist, sep = "\t"):
    ret = []
    for elem in ilist:
        if type(elem) is str:
            ret.append(elem)
        elif elem is None:
            ret.append("")
        # elif elem == []:
        #     ret.append("")
        # elif elem == ():
        #     ret.append("") 
        else:
            ret.append(str(elem))
    return sep.join(ret)

if __name__ == "__main__":
    print matcher("xxx", "xxxxxx")
    print joiner(("A", 3, None, 5, False, True, "Hello", [], "X"))
    

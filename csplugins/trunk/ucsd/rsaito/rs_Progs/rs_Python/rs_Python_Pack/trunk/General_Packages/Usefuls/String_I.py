#!/usr/bin/env python

def matcher(substr, istr):

    ret = []
    for p in range(len(istr) - len(substr) + 1):
        # print p, substr, str[p:p+len(substr)]
        if substr == istr[p:p+len(substr)]:
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

def deleter(istr, dchars):
    ostr = istr
    for dchar in dchars:
        ostr = ostr.replace(dchar, "")
    return ostr

def str_num_match_simple(str1, str2, length_check = False):
    if length_check and len(str1) != len(str2):
        raise 'String length of "%s" and "%s" not identical.' % (str1, str2)
    else:
        count = 0
        if len(str1) < len(str2):
            str_len = len(str1)
        else:
            str_len = len(str2)
        for i in range(str_len):
            if str1[i] == str2[i]:
                count += 1
    return count

if __name__ == "__main__":
    print matcher("xxx", "xxxxxx")
    print joiner(("A", 3, None, 5, False, True, "Hello", [], "X"))
    print deleter("abcdefghij", "cdg")
    print str_num_match_simple("abcdexx", "decdgxxxxx")
    
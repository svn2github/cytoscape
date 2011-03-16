#!/usr/bin/env python

def split_alpha_num(istr):
    """ '.' is dealt as string """
    pctype = None
    buf = []
    buf_s = ""
    for c in istr:
        if '0' <= c and c <= '9':
            ctype = "Num"
        else:
            ctype = "Char"
            
        if pctype is not None and pctype != ctype:
            if pctype == "Num":
                buf.append(int(buf_s))
            else:
                buf.append(buf_s)
            buf_s = ""
                        
        buf_s += c
        pctype = ctype

    if pctype == "Num":
        buf.append(int(buf_s))
    else:
        buf.append(buf_s)
            
    return buf

def add_zero_str(istr, zeronum = 5):
    strnumlist = split_alpha_num(istr)
    ret = ""
    for elem in strnumlist:
        if type(elem) is int:
            if len('elem') > zeronum:
                raise "Digits exceeded threshold"
            ret += '0' * (zeronum - len(`elem`)) + `elem`
        else:
            ret += elem
    return ret

def alphanum_sort_cmp(a, b):
    a_ = add_zero_str(a)
    b_ = add_zero_str(b)
    
    if a_ < b_:
        return -1
    elif a_ == b_:
        return 0
    else:
        return 1
    
if __name__ == "__main__":
    
    print split_alpha_num("ABC10DEF05GH123CCC")
    print add_zero_str("ABC10DEF05GH123CCC")
    
    a = ["AB9", "AB8", "AB12", "AB11"]
    a.sort(alphanum_sort_cmp)
    print a
    
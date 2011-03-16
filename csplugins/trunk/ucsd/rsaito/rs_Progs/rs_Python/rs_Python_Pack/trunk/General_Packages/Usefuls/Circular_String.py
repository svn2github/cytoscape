#!/usr/bin/env python

def get_csubstr(cstr, pos1, pos2):
    """ Obtain substring where pos1 <= pos < pos2 from circular string (cstr) """
    
    ret = ""
    while pos1 < 0:
        pos1 += len(cstr)
        pos2 += len(cstr)

    while pos1 >= len(cstr):
        pos1 -= len(cstr)
        pos2 -= len(cstr)        
        
    if pos2 <= len(cstr):
        ret = cstr[pos1:pos2]
    else:
        ret = cstr[pos1:]
        pos2 -= len(cstr)
        while pos2 > len(cstr):
            ret += cstr
            pos2 -= len(cstr)
        ret += cstr[:pos2]
        
    return ret

if __name__ == "__main__":
    print get_csubstr("0123456789", 0, 19)
    
        
    
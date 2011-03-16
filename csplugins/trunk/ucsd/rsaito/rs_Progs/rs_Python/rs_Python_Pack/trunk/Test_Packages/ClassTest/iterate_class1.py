#!/usr/bin/env python

class TestIter:
    def __init__(self, h):
        self.h = h
        
    def keys(self):
        print "--- keys called."
        return self.h.keys()
    
    def __iter__(self):
        print "--- iter called."
        return self.h.__iter__()
    
if __name__ == "__main__":
    ti = TestIter({"A":"a", "B":"b", "C":"c"})
    
    print "* In test *"
    if "A" in ti:
        print "OK"
    else:
        print "NG"
    
    for k in ti:
        print k, ti.h[k]
                   
    
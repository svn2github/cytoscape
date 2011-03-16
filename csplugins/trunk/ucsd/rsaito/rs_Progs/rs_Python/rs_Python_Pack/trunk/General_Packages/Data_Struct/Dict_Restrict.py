#!/usr/bin/env python

class Dict_Restrict:
    def __init__(self, h):
        self.h = h

    def set_h(self, p):
        for key in p:
            self[key] = p[key]

    def __setitem__(self, key, val):
        if key not in self.h:
            raise Exception, "undefined key: " + key

        self.h[key] = val
    
    def __getattr__(self, attrname):

        return getattr(self.h, attrname)
     
     

if __name__ == "__main__":
    h = Dict_Restrict({"A": 1,
                       "B": 2,
                       "C": 3 })
    
    h["A"] = 4
    h["B"] = 10
    # h["D"] = "Hello!"
    
    h.set_h({"A": 10, "B": 15 })
    print h
    
    h.set_h({"A": 10, "B": 15, "D": 36 })
    print h
    
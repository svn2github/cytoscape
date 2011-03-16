#!/usr/bin/env python

class Dict_Ordered:
    def __init__(self, keys_ordered = None):
        if keys_ordered:
            self.keys_ordered = keys_ordered
        else:
            self.keys_ordered = []
            
        self.h = {}

    def keys(self):
        ret = []
        for k in self.keys_ordered:
            if self.h.has_key(k):
                ret.append(k)

        return ret
        
    def sort_keys(self):
        self.keys_ordered.sort()
        
    def __setitem__(self, key, val):
        if key not in self.keys_ordered:
            self.keys_ordered.append(key)
        self.h[key] = val
        
    def __getattr__(self, attrname):
        return getattr(self.h, attrname)

    def output(self, reverse = False):
        for key in self.keys():
            if type(key) <> str:
                col1 = `key`
            else:
                col1 = key
            if type(self.h[key]) <> str:
                col2 = `self.h[key]`
            else:
                col2 = self.h[key]
            if reverse:
                print "\t".join((col2, col1))
            else:
                print "\t".join((col1, col2))

if __name__ == "__main__":
    test1 = Dict_Ordered([2,3,1, "X"])
    test1[1] = "A"
    test1[2] = "B" 
    test1[3] = "C"
    test1[4] = 4
    
    print test1.keys()
    test1.sort_keys()
    print test1.keys()
    test1.output()
    test1.output(reverse=True)
    

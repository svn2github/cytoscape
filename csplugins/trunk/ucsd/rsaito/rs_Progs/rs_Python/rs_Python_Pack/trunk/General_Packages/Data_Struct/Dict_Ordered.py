#!/usr/bin/env python

from General_Packages.Usefuls.ListProc1 import list_shuffle

class Dict_Ordered:
    def __init__(self, keys_ordered = None):

        self.h = {}
        self.keys_ordered = []
        self.keys_ordered_h = {}
        
        self.keys_ordered_v = [] # Valid ones
        self.valid_keys_calc = False

        if keys_ordered:
            self.keys_ordered = keys_ordered
            for key in keys_ordered:
                self.keys_ordered_h[key] = ""

    def keys(self):
        
        if self.valid_keys_calc:
            return self.keys_ordered_v
        
        self.keys_ordered_v = []
        for key in self.keys_all():
            if key in self.h:
                self.keys_ordered_v.append(key)
        
        # print "Recalculated valid keys:", self.keys_ordered_v[0:10]
        
        self.valid_keys_calc = True
        return self.keys_ordered_v

    def keys_all(self):
        
        return self.keys_ordered
        
    def sort_keys(self):
        self.keys_ordered.sort()
        self.valid_keys_calc = False
        
    def shuffle_keys(self):
        self.keys_ordered = list_shuffle(self.keys_ordered)
        self.valid_keys_calc = False
        
    def __setitem__(self, key, val):
        if key not in self.keys_ordered_h:
            self.keys_ordered.append(key)
            self.keys_ordered_h[key] = ""
            self.valid_keys_calc = False

        self.h[key] = val
                
    def __getattr__(self, attrname):
        return getattr(self.h, attrname)

    def __iter__(self):
        return self.keys().__iter__()

    def vals(self, None_Mark = None):
        ret = []
        for k in self.keys_all():
            if k in self.h:
                ret.append(self[k])
            else:
                ret.append(None_Mark)
        return ret

    def get_outputI(self, reverse = False, sep = "\t", None_Mark = ""):
        ret = []
        for key in self.keys_all():
            if type(key) <> str:
                col1 = `key`
            else:
                col1 = key
                
            if key not in self:
                col2 = None_Mark
            elif type(self.h[key]) <> str:
                col2 = `self[key]`
            else:
                col2 = self[key]
            if reverse:
                ret.append(sep.join((col2, col1)))
            else:
                ret.append(sep.join((col1, col2)))
        return ret

    def output(self, reverse = False, sep = "\t"):
        for key in self:
            if type(key) <> str:
                col1 = `key`
            else:
                col1 = key
            if type(self.h[key]) <> str:
                col2 = `self.h[key]`
            else:
                col2 = self.h[key]
            if reverse:
                print sep.join((col2, col1))
            else:
                print sep.join((col1, col2))

if __name__ == "__main__":
    test1 = Dict_Ordered([2,3,1, "X"])
    test1[1] = "A"
    test1[2] = "B" 
    test1[3] = "C"
    test1[4] = 4
    
    print test1.keys()
    
    print "----"
    print test1.h
    print test1.keys_ordered
    print test1.keys_ordered_h
    print test1.keys_ordered_v
    print " - - "
    
    for k in test1:
        print "Trying for", k
        if k in test1:
            print k, test1[k]
    print test1.vals("NA")
    
    test1[5] = "Five"
    
    print test1.keys()
    print "Again", test1.keys()
    test1.sort_keys()
    print test1.keys()
    print test1.get_outputI(False, ":")
    test1.output(reverse=True)
    
    test2 = Dict_Ordered()
    test2["A"] = 1
    test2["B"] = 2
    test2["C"] = 3
    test2["D"] = 4
    test2["E"] = 5
    print test2.keys()
    print test2.shuffle_keys()
    test2["F"] = 6
    print test2.keys()
    
    

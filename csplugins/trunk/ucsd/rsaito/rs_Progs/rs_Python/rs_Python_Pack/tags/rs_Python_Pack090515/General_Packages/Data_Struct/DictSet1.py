#!/usr/bin/env python

class DictSet:
    def __init__(self):
        self.h = {}

    def append(self, key, val):
        if key in self.h:
            self.h[key].append(val)
        else:
            self.h[key] = [ val ]

    def ret_set(self, key):
        return list(set(self.h[ key ]))

    def __getitem__(self, key):
        return self.h[ key ]

    def keys(self):
        return self.h.keys()
    
    def values(self):
        return self.h.values()

    def __iter__(self):
        return self.keys().__iter__()
    
    def __repr__(self):
        return self.h.__repr__()

    def has_key(self, key):
        return self.h.has_key(key)   
    

class DictSet_Order(DictSet):
    def __init__(self):
        DictSet.__init__(self)
        self.order = []

    def append(self, key, val):
        if not self.has_key(key):
            self.order.append(key)
        DictSet.append(self, key, val)

    def ret_order(self):
        return self.order

if __name__ == "__main__":

    ds = DictSet_Order()
    ds.append("A", "Rin")
    ds.append("A", "Rin")
    ds.append("B", "Tin")
    ds.append("C", "Tinny")

    print "Dictionary structure:"
    print ds
    print "Dictionary keys:"
    print ds.keys()
    print ds.h
    print ds.has_key("A")
    print ds.has_key("X")
    print ds.ret_order()

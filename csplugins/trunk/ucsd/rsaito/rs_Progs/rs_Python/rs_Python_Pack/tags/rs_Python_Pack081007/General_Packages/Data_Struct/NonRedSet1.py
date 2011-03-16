#!/usr/bin/env python

class NonRedSet:
    def __init__(self):
        self.h = {}

    def append(self, elem):
        self.h[ elem ] = ""

    def append_list(self, elems):
        for elem in elems:
            self.append(elem)

    def ret_set(self):
        return self.h.keys()

class NonRedSetDict:
    def __init__(self):
        self.h = {}

    def append_Dict(self, key, val):
        if not key in self.h:
            self.h[key] = NonRedSet()
        self.h[key].append(val)

    def ret_set_Dict(self, key):
        return self.h[key].ret_set()

    def keys(self):
        return self.h.keys()

    def has_key(self, key):
        return self.h.has_key(key)
    

if __name__ == "__main__":
    l = NonRedSet()
    l.append("Rin")
    l.append("Gen")
    l.append("Rin")
    l.append("Koji")
    l.append("Yuri")
    l.append("Aki")
    l.append("Koji")
    print l.ret_set()

    l.append_list(["A", "B", "C", "A"])
    print l.ret_set()

    l_dict = NonRedSetDict()
    l_dict.append_Dict("SFC", "Rintaro")
    l_dict.append_Dict("TTCK", "Koji")
    l_dict.append_Dict("SFC", "Rintaro")
    l_dict.append_Dict("SFC", "Rintaro")
    l_dict.append_Dict("SFC", "Yo")
    l_dict.append_Dict("SFC", "Yo")
    print l_dict.ret_set_Dict("SFC")
    print l_dict.has_key("SFC")
    print l_dict.has_key("Medical")


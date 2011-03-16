#!/usr/bin/env python

class Pair:
    def __init__(self, p1, p2):
        self.p1 = p1
        self.p2 = p2
        self.val = None

    def get_pair(self):
        return self.p1, self.p2

    def set_val(self, val):
        self.val = val

    def get_val(self):
        return self.val

class Pair2:
    def __init__(self, p1, p2):
        self.p1 = p1
        self.p2 = p2
        self.val = {}

    def get_pair(self):
        return self.p1, self.p2

    def set_val(self, val, itype):
        self.val[ itype ] = val

    def get_val(self, itype):
        if itype in self.val:
            return self.val[itype]
        else:
            return False

class Pair3:
    def __init__(self, p1, p2):
        self.p1 = p1
        self.p2 = p2
        self.val = {}

    def get_pair(self):
        return self.p1, self.p2

    def set_val(self, func, itype):
        self.val[ itype ] = func

    def get_val(self, itype):
        if itype in self.val:
            return self.val[itype](self.p1, self.p2)
        else:
            return False


if __name__ == "__main__":

    def simple_func(p1, p2):
        return p1 + " " + p2

    def simple_func2(p1, p2):
        return p1 + " : " + p2

    pair = Pair("Rin", "Gen")
    print pair.get_pair()
    pair.set_val("XXX")
    print pair.get_val()
    pair2 = Pair2("RIN", "GEN")
    pair2.set_val("XYZ", "type1")
    print pair2.get_val("type2")
    pair3 = Pair3("Rintalk", "Gesulin")
    pair3.set_val(simple_func, 1)
    pair3.set_val(simple_func2, 2)
    print pair3.get_val(1)
    print pair3.get_val(2)

#!/usr/bin/env python

class Single_KeyVals_I:
    def __init__(self, key, vals):
        self.key = key
        self.vals = vals

    def get_key(self):
        return self.key

    def get_vals(self):
        return self.vals


class Neighb_Red:
    def __init__(self):
        """ Structure of self.l
        [ [key1, [val1, val2]],
          [key2, [val3, val4]],
          [key3, [val5, val6]]]
          """
        
        self.l = []
        self.prev_key = False

    def append(self, key, val):
        if self.prev_key == key:
            self.l[-1][1].append(val)
        else:
            self.l.append([key, [val]])
        self.prev_key = key

    def get_list(self):
        return self.l

    def get_Single_KeyVals_set(self):
        ret = []
        for elem in self.get_list():
            key, vals = elem
            ret.append(Single_KeyVals_I(key, vals))
        return ret

if __name__ == "__main__":
    nr = Neighb_Red()
    nr.append("A", "Rin")
    nr.append("A", "Gen")
    nr.append("A", "Rin")
    nr.append("B", "Rin")
    nr.append("C", "Rin")
    nr.append("C", "Rin")

    print nr.get_list()
    for keyval in nr.get_Single_KeyVals_set():
        print keyval.get_key(), keyval.get_vals()

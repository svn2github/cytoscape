#!/usr/bin/env python

class MultiDimDict:
    def __init__(self, dim, init_val):
        self.dim = dim
        self.init_val = init_val # Set 0 if this is used as counter.
        self.dict = {}

    def get_dim(self):
        return self.dim

    def set_val(self, loc, val):
        if len(loc) != self.dim:
            raise "Dimensional error ... " + `len(loc)` + " <> " + `self.dim`

        cur_ref = self.dict 
        for p in loc[:-1]:
            if not p in cur_ref:
                cur_ref[p] = {}
            cur_ref = cur_ref[p] 
        cur_ref[ loc[-1] ] = val

    def get_val(self, loc):
        if len(loc) != self.dim:
            raise "Dimensional error ... " + `len(loc)` + " <> " + `self.dim`

        cur_ref = self.dict 
        for p in loc[:-1]:
            if not p in cur_ref:
                cur_ref[p] = {}
            cur_ref = cur_ref[p]

        if loc[-1] in cur_ref:
            return cur_ref[ loc[-1] ]
        else:
            return self.init_val

    def plus_val(self, loc, val):
        if len(loc) != self.dim:
            raise "Dimensional error ... " + `len(loc)` + " <> " + `self.dim`

        cur_ref = self.dict 
        for p in loc[:-1]:
            if not p in cur_ref:
                cur_ref[p] = {}
            cur_ref = cur_ref[p] 

        if not loc[-1] in cur_ref:
            cur_ref[ loc[-1] ] = self.init_val
            
        cur_ref[ loc[-1] ] += val


    def get_all_data(self):
        return self.dict

if __name__ == "__main__":
    md = MultiDimDict(3, "Initial")
    md.set_val(("A", "B", "C"), "Hello")
    md.set_val(("A", "B", "D"), "Hi")
    print md.get_val(("A", "B", "C"))
    print md.get_val(("A", "B", "D"))
    print md.get_val(("A", "B", "E"))
        
    md_num = MultiDimDict(3, 0)
    md_num.set_val(("A", "B", "C"), md_num.get_val(("A", "B", "C")) + 1)
    md_num.set_val(("A", "B", "C"), md_num.get_val(("A", "B", "C")) + 2)
    print md_num.get_val(("A", "B", "C"))
    md_num.plus_val(("A", "B", "C"), 10)
    print md_num.get_val(("A", "B", "C"))

    md2 = MultiDimDict(2, "Init")
    md2.set_val((3, 6), "X")
    print md2.get_val((3, 6))

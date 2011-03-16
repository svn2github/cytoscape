#!/usr/bin/env python
from _xmlplus.xpath.XPathParser import AND

import string
import TmpFile

class Column_Sheet_Count:
    def __init__(self, filename):
        self.filename = filename
        self.reset()

    def reset(self):
        self.funcs = []
        self.descr = []
        self.parms = []
        self.fh = open(self.filename, "r")
        self.header = self.fh.readline().rstrip().split("\t")
        self.header_num2header_name = {}
        self.header_name2header_num = {}
        for i in range(len(self.header)):
            header_name = self.header[i]
            self.header_num2header_name[i] = header_name
            self.header_name2header_num[ header_name ] = i

    def reg_func(self, descr, func, **param):
        self.descr.append(descr)
        self.funcs.append(func)
        self.parms.append(param)

    def sheet_calculation(self):

        data = {}
        self.counter = [0] * len(self.funcs)
        for line in self.fh:
            line_tab = line.rstrip().split("\t")
            for i in range(len(line_tab)):
                header_name = self.header_num2header_name[i]
                data[ header_name ] = line_tab[i]
            for i in range(len(self.funcs)):
                if self.funcs[i](data, self.parms[i]) is True:
                    self.counter[i] += 1

    def ret_result(self, descr):
        idx = self.descr.index(descr)
        return self.counter[ idx ]

if __name__ == "__main__":
    tmp_obj = TmpFile.TmpFile_III("""

H      W
166    62
175    80
180    75
155    55

""")

    def func1(data, param):
        if string.atof(data["W"]) >= param["w"]:
            return True
        else:
            return False

    def func2(data, param):
        if (string.atof(data["H"]) >= param["h"] and
            string.atof(data["W"]) <= param["w"]):
            return True
        else:
            return False

    csc = Column_Sheet_Count(tmp_obj.filename())
    csc.reg_func("Height70", func1, w=60)
    csc.reg_func("Ikebody", func2, h=170, w=79)
    csc.sheet_calculation()
    print csc.ret_result("Height70")
    print csc.ret_result("Ikebody")

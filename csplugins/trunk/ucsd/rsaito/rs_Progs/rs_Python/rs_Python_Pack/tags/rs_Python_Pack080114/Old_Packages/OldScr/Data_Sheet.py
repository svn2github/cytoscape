#!/usr/bin/env python

import string
import Relabel_redund

class Data_Sheet:
    def __init__(self):
        self.row = {}
        self.row_n = 0
        self.col_n = 0 # Excludes label column in each row
        self.col_lb = []
        self.row_lb = []
        self.null = ""

    def read_sheet_file(self, filename, sep):
        fh = open(filename, "r")
        lines = map(lambda l: l.rstrip(), fh.readlines())
        self.col_lb = self.extract_col_labels(lines, sep)
        self.row_n = len(lines)
        for line in lines:
            line_a = line.split(sep)
            label, data = self.extract_row_label_data(line_a)
            self.row_lb.append(label)
            if len(data) > self.col_n:
                self.col_n = len(data)
            self.row[ label ] = data

        self.make_rectangle()

    
    """ Perhaps the following 2 methods are only called from other
    methods in this class. """
    def extract_col_labels(self, lines, sep):
        first_line = lines.pop(0)
        """ This will extract and eliminate first line """
        col_lb_immature = first_line.split(sep)
        return col_lb_immature[1:]

    def extract_row_label_data(self, line_a):
        label = line_a[0]
        data = line_a[1:]
        return (label, data)
        

    def make_rectangle(self):
        for row_key in self.row:
            data = self.row[ row_key ]
            if len(data) < self.col_num():
                for i in range(len(data), self.col_num()):
                    data.append(self.null)

        num_col_labels = len(self.col_labels())
        if num_col_labels < self.col_num():
            for i in range(num_col_labels, self.col_num()):
                self.col_lb.append("tmp-Column-#" + `i`)

        if num_col_labels > self.col_num():
            self.col_lb = self.col_lb[:self.col_num()]

        
    def get_data(self, row_key):
        if row_key in self.row:
            return self.row[ row_key ]
        else:
            return False

    def set_data(self, row_key, data):
        self.row[row_key] = data
        self.row_n += 1
        self.row_lb.append(row_key)

    def set_col_labels(self, col_labels):
        self.col_lb = col_labels

    def row_num(self):
        return self.row_n

    def col_num(self):
        return self.col_n

    def col_labels(self):
        return self.col_lb

    def row_labels(self):
        return self.row_lb

    def numerize(self):
        for rlb in self.row_labels():
            data = self.get_data(rlb)
            data_num = []
            for s in data:
                try:
                    num = float(s)
                except ValueError:
                    num = ""
                data_num.append(num)
            self.row[ rlb ] = data_num
    
    def display_sheet(self, sep):
        print sep + string.join(self.col_labels(), sep)
        for rlb in self.row_labels():
            data_out = []
            for d in self.get_data(rlb):
                if type(d) == float:
                    data_out.append(`d`)
                else:
                    data_out.append(d)           
            print rlb + sep + string.join(data_out, sep) 

    
class Data_Sheet2(Data_Sheet):
    def read_sheet_file(self, filename, sep, d_start_col):
	self.d_start_col = d_start_col
	Data_Sheet.read_sheet_file(self, filename, sep)
    
    def extract_col_labels(self, lines, sep):
        first_line = lines.pop(0)
        """ This will extract and eliminate first line """
        col_lb_immature = first_line.split(sep)
        
        self.relabel = Relabel_redund.Relabel_redund(
            col_lb_immature[self.d_start_col:]
            )
        self.relabel.relabel()
        return self.relabel.get_new_labels()

    def extract_row_label_data(self, line_a):
        label = line_a[0]
        data = line_a[self.d_start_col:]
        return (label, data)


if __name__ == "__main__":

    import TmpFile
    tmp_obj = TmpFile.TmpFile3("""
    
        Col-0     Col-1    Col-2    Col-3    Col-4    Col-1
Row-1     3.0       2.0     -1.5
Row-2    -1.2       1.9      7.9
Row-3     2.3      -1.2      2.3      3.4      4.9      5.5
Row-4     1.0       0.0      1.9
Row-5     1.8       2.0     -1.0

""")
    ds = Data_Sheet()
    ds.read_sheet_file(tmp_obj.filename(), "\t")
    print ds.row_num(), "X", ds.col_num()
    print ds.col_labels()
    print ds.row_labels()
    print ds.get_data("Row-2")
    print ds.get_data("DoNotExist")
    ds.display_sheet("\t")

    ds.set_data("Row-A", (`1.0`, `-2.1`, `3.0`))
    ds.set_col_labels(("C1", "C2", "C3", "C4", "C5"))
    print
    ds.display_sheet("\t")
    print
    
    ds.numerize()
    ds.display_sheet("\t")
    
    ds2 = Data_Sheet2()
    ds2.read_sheet_file(tmp_obj.filename(), "\t", 2)
    ds2.display_sheet("\t")

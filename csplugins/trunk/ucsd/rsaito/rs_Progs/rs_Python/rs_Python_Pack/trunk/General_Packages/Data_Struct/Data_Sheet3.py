#!/usr/bin/env python

import string
# import Usefuls.Relabel_redund as Relabel_redund

# In get_data method of Data_Sheet3, None is used instead of False.

class Data_Sheet:
    def __init__(self, filename, sep = "\t"):
        self.row = {}
        self.row_n = 0
        self.col_n = 0 # Excludes label column in each row
        self.col_lb = []
        self.row_lb = []
        self.col_lb_to_num = {}

        self.null = ""

        self.read_sheet_file(filename, sep)

    def read_sheet_file(self, filename, sep):
        fh = open(filename, "r")
        lines = map(lambda l: l.rstrip(), fh.readlines())
        self.col_lb = self.extract_col_labels(lines, sep)

        for i in range(len(self.col_lb)):
            if self.col_lb[i] not in self.col_lb_to_num:
                self.col_lb_to_num[ self.col_lb[i] ] = i

        for line in lines:
            line_a = line.split(sep)
            label, data = self.extract_row_label_data(line_a)
            if not label in self.row:
                self.row_lb.append(label)
                self.row_n += 1
                self.row[ label ] = data
                if len(data) > self.col_n:
                    self.col_n = len(data)

        self.make_rectangle()


    """ Perhaps the following 2 methods are only called from other
    methods (mainly by __init__) in this class. """
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
            return None

    def get_datum(self, row_key, col_key):
        if row_key in self.row:
            data = self.row[ row_key ]
        else:
            return None
        
        if col_key in self.col_lb_to_num:
            column_no = self.col_lb_to_num[ col_key ]
            return data[ column_no ]
        else:
            return None
        
    def set_datum(self, row_key, col_key, datum):
        if row_key in self.row:
            data = self.row[ row_key ]
        else:
            raise "Row key error ... %s" % (row_key,)
        
        if col_key in self.col_lb_to_num:
            column_no = self.col_lb_to_num[ col_key ]
            data[ column_no ] = datum
        else:
            raise "Column key error ... %s" % (col_key,)
        

    def get_data_accord_keys(self, row_key, col_keys):
        
        ret_data = []
        for each_key in col_keys:
            ret_data.append(self.get_datum(row_key, each_key))
        return ret_data
    
    def get_data_accord_row_keys(self, row_keys, col_key):
        
        ret_data = []
        for each_key in row_keys:
            ret_data.append(self.get_datum(each_key, col_key))
        return ret_data
    

    def set_data(self, row_key, data):
        if not row_key in self.row:
            self.row_n += 1
            self.row_lb.append(row_key)
        self.row[row_key] = data

    def set_col_labels(self, col_labels):
        self.col_lb = col_labels
        for i in range(len(self.col_lb)):
            if self.col_lb[i] not in self.col_lb_to_num:
                self.col_lb_to_num[ self.col_lb[i] ] = i

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
                elif type(d) == int:
                    data_out.append(`d`)
                else:
                    data_out.append(d)
            print rlb + sep + string.join(data_out, sep)

    
    def get_datum_zscore_row_based(self, row_key, col_key):
        # Numerization required.
        from Calc_Packages.Stats.StatsI import norm
        adata = [x for x in self.get_data(row_key)
                 if type(x) == int or type(x) == float]
        adata_norm = norm(adata)
        column_no = self.col_lb_to_num[ col_key ]
        return adata_norm[ column_no ]


if __name__ == "__main__":

    import Usefuls.TmpFile
    tmp_obj = Usefuls.TmpFile.TmpFile_III("""

        Col-0     Col-1    Col-2    Col-3    Col-4    Col-1
Row-1     3.0       2.0     -1.5
Row-2     1.2       1.9      7.9
Row-3     2.3      -1.2      2.3      3.4      4.9      5.5
Row-4     1.0       0.0      1.9
Row-2    -1.0      -2.9     -2.3
Row-5     1.8       2.0     -1.0


""")

    ds = Data_Sheet(tmp_obj.filename(), "\t")
    ds.read_sheet_file(tmp_obj.filename(), "\t")
    print ds.row_num(), "X", ds.col_num()
    print ds.col_labels()
    print ds.row_labels()
    print ds.get_data("Row-2")
    print ds.get_data("DoNotExist")
    print ds.get_datum("Row-4", "Col-2")
    print ds.get_datum("Row-1", "Col-2")
    print ds.get_data_accord_keys("Row-1", [ "Col-0", "Col-1", "Col-4"])
    print "By row keys:", ds.get_data_accord_row_keys(("Row-2", "Row-5", "Row-3"), "Col-1")
    ds.display_sheet("\t")

    ds.set_data("Row-A", (`1.0`, `-2.1`, `3.0`))
    ds.set_col_labels(("C1", "C2", "C3", "C4", "C5"))
    print
    ds.display_sheet("\t")
    print

    ds.numerize()
    ds.set_datum("Row-5", "C2", 999)
    ds.display_sheet("\t")

    print ds.get_datum_zscore_row_based("Row-3", "Col-2")
    

#!/usr/bin/env python

import Data_Struct.Data_Sheet2
import Calc_Packages.Math.StatsI as Stats
from Usefuls.Num_filters import filter_floats2

class Express:
    def __init__(self, filename):
	self.data_sheet = Data_Struct.Data_Sheet2.Data_Sheet(filename)
	self.data_sheet.numerize()

    def genes(self):
        return self.data_sheet.row_labels()

    def conditions(self):
        return self.data_sheet.col_labels()

    def row_num(self):
        return self.data_sheet.row_num()

    def expression_pat(self, gene):
        return self.data_sheet.get_data(gene)

    def corr(self, gene1, gene2):
	exp1 = self.expression_pat(gene1)
        if exp1 is None or exp1 is False: return False

	exp2 = self.expression_pat(gene2)
        if exp2 is None or exp2 is False: return False

	exp1_f, exp2_f = filter_floats2(exp1, exp2)

        if len(exp1_f) < 2: return False

	return Stats.corr(exp1_f, exp2_f)

class SymAtlas_Sheet(Data_Struct.Data_Sheet2.Data_Sheet):
    def extract_col_labels(self, lines, sep):
        first_line = lines.pop(0)
        """ This will extract and eliminate first line """
        col_lb_immature = first_line.split(sep)
        return col_lb_immature[2:]

    def extract_row_label_data(self, line_a):
        label = line_a[0]
        data = line_a[2:]
        return (label, data)

class SymAtlas(Express):
    def __init__(self, filename):
	self.data_sheet = SymAtlas_Sheet(filename)
	self.data_sheet.numerize()

if __name__ == "__main__":

    import Usefuls.TmpFile

    tmp_obj = Usefuls.TmpFile.TmpFile_III("""

        Col-0     Col-1    Col-2    Col-3    Col-4
Row-1     3.0       2.0     -1.5
Row-2    -1.2       1.9      7.9
Row-3     2.3      -1.2      2.3      3.4
Row-4     1.0       0.0      1.9
Row-5     1.8       2.0     -1.0

""")

    exp = Express(tmp_obj.filename())
    print exp.genes()
    print exp.conditions()
    print exp.row_num()
    print exp.expression_pat("Row-3")
    print exp.corr("Row-3", "Row-5")

"""

    import random
    import Usefuls.Histogram

    symatlas_file = "../../Exp_data/SymAtlas_human_CD"
    symatlas = SymAtlas(symatlas_file)
    genes = symatlas.genes()

    hist = Usefuls.Histogram.Hist(-1, 1, 20)

    for i in range(10000):

        rand1 = random.randrange(0,symatlas.row_num())
        rand2 = random.randrange(0,symatlas.row_num())

        gene1 = genes[ rand1 ]
        gene2 = genes[ rand2 ]

        if (gene1 != gene2 and
            gene1[:9] != "Undefined" and
            gene2[:9] != "Undefined"):
            hist.add(symatlas.corr(gene1, gene2))

    hist.display_rate()
"""

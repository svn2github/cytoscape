#!/usr/bin/env python

import Data_Struct.Data_Sheet2
import Data_Struct.Hash
import Data_Struct.ListList
import Data_Struct.Data_with_Miss2
import Calc_Packages.Math.StatsI as Stats
from Usefuls.Num_filters import filter_floats2

import string
from Usefuls.ListProc1 import array_string

class ExprProbe:
    def __init__(self, filename):
	self.data_sheet = Data_Struct.Data_Sheet2.Data_Sheet(filename)
	self.data_sheet.numerize()

    def probes(self):
        return self.data_sheet.row_labels()

    def conditions(self):
        return self.data_sheet.col_labels()

    def row_num(self):
        return self.data_sheet.row_num()

    def expression_pat(self, probe):
        return self.data_sheet.get_data(probe)

    def corr(self, probe1, probe2):
	exp1 = self.expression_pat(probe1)
        if exp1 is None or exp1 is False: return False

	exp2 = self.expression_pat(probe2)
        if exp2 is None or exp2 is False: return False

	exp1_f, exp2_f = filter_floats2(exp1, exp2)

        if len(exp1_f) < 2: return False

	return Stats.corr(exp1_f, exp2_f)


class ExprGene:
    def __init__(self, probe_expr_file,
                 probe_annot_file,
                 Probe_column_label,
                 Gene_column_label):
        self.probe_expr = ExprProbe(probe_expr_file)
        self.probe_annot = Data_Struct.Hash.Hash_headf("A")
        self.probe_annot.read_file(
            filename = probe_annot_file,
            Key_cols_hd = ["Gene"],
            Val_cols_hd = ["Probe"])

    def genes(self):
        return self.probe_annot.keys()

    def conditions(self):
        return self.probe_expr.conditions()

    def gene_to_probeids(self, geneid):
        ret = self.probe_annot.val_force(geneid)
        if ret:
            return ret
        else:
            return []

    def expression_pat(self, geneid):

        probeids = self.gene_to_probeids(geneid)
        if not probeids:
            return None
        elif len(probeids) == 1:
            return self.probe_expr.expression_pat(probeids[0])

        exp_pat_norm = Data_Struct.ListList.ListList()

        for probeid in probeids:
            exp_pat_del = (
                Data_Struct.
                Data_with_Miss2.
                ListList_Size_Conv([self.probe_expr.expression_pat(probeid)])
                )
            exp_pat_del_norm = (
                Stats.norm(exp_pat_del.get_num_listlist(True)[0])
                )

            exp_pat_del.import_num_listlist([ exp_pat_del_norm ])

            """
            print string.join([probeid] +
                              array_string(exp_pat_del.
                                           get_idx_numlistlist_to_missing().
                                           get_all_lists()[0]), "\t")
                                           """

            exp_pat_norm.add_list(
                exp_pat_del.
                get_idx_numlistlist_to_missing().
                get_all_lists()[0])

        return exp_pat_norm.to_single_list()


if __name__ == "__main__":

    import Usefuls.TmpFile

    probe_expr_obj = Usefuls.TmpFile.TmpFile_III("""

         Cond-0    Cond-1    Cond-2    Cond-3    Cond-4
Probe-1     3.0       2.0      -1.5
Probe-2    -1.2       1.9       7.9
Probe-3     2.3      -1.2       2.3       3.4
Probe-4     1.0       0.0       1.9
Probe-5     1.8       2.0      -1.0

""")

    expr = ExprProbe(probe_expr_obj.filename())
    print expr.probes()
    print expr.conditions()
    print expr.row_num()
    print expr.expression_pat("Probe-3")
    print expr.corr("Probe-3", "Probe-5")

    probe_annot_obj = Usefuls.TmpFile.TmpFile_III("""

Probe     Gene
Probe-1   Gene-1
Probe-2   Gene-1
Probe-3   Gene-2
Probe-4   Gene-3
Probe-5   Gene-3
Probe-6   Gene-4

""")

    geneexpr = ExprGene(probe_expr_obj.filename(),
                     probe_annot_obj.filename(),
                     "Probe", "Gene")

    print geneexpr.gene_to_probeids("Gene-3")
    print geneexpr.gene_to_probeids("XXX")
    print geneexpr.expression_pat("Gene-2")
    print geneexpr.expression_pat("Gene-3")
    print geneexpr.genes()
    print geneexpr.conditions()

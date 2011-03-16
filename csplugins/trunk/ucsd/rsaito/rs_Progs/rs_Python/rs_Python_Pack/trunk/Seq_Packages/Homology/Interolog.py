#!/usr/bin/python

import string
import Data_Struct.Hash2
from Homology_descr4 import HomologyDescr

class Interolog:
    def __init__(self, p1, p2):
	self.p1 = p1
	self.p2 = p2
	self.conv1 = ""
	self.conv2 = ""
	self.interologs = []
	self.best = [ "", "" ]
	self.best_conv = [ "", "" ]
	self.best_eval1 = ""
	self.best_eval2 = ""
        self.best_ref = ""

    def get_source(self):
        return (self.p1, self.p2)

    def get_interolog(self):
	return self.interologs

    def get_best(self):
	return self.best

    def get_best_conv(self):
	return self.best_conv

    def get_best_eval(self):
	return [ self.best_eval1, self.best_eval2 ]

    def get_best_ref(self):
        return self.best_ref

    def set_homology(self, homol1, homol2):
	self.homol1 = homol1
	self.homol2 = homol2

    def set_subj_ppi(self, subj_ppi):
	self.subj_ppi = subj_ppi

    def set_conv(self, conv1, conv2):
	self.conv1 = conv1
	self.conv2 = conv2

    def calc_interolog(self):

	homolog1 = self.homol1.homologs(self.p1)
	homolog2 = self.homol2.homologs(self.p2)

	for h1 in homolog1:
	    for h2 in homolog2:
		if self.conv1 == "":
		    h1s = h1
		    h2s = h2
		else:
		    h1s = self.conv1.val_force(h1)
		    h2s = self.conv2.val_force(h2)
		if self.subj_ppi.has_pair(h1s, h2s):
		    self.interologs.append([h1, h2])

	self.calc_best()

    def calc_best(self):

	sig_eval1 = 11.0
	sig_eval2 = 11.0
	best_itr = False
	best_itr_conv = False
        best_itr_ref = False

	for itr in self.interologs:
	    eval1 = string.atof(self.homol1.homologs(self.p1)[itr[0]])
	    eval2 = string.atof(self.homol2.homologs(self.p2)[itr[1]])
	    if eval1 * eval2 < sig_eval1 * sig_eval2:
		sig_eval1 = eval1
		sig_eval2 = eval2
		best_itr = itr

	if best_itr:
	    self.best_eval1 = sig_eval1
	    self.best_eval2 = sig_eval2
	    best_itr_conv = [
		self.conv1.val(best_itr[0]),
		self.conv2.val(best_itr[1])
		]
            best_itr_ref = self.subj_ppi.pair_val(self.conv1.val(best_itr[0]),
                                                  self.conv2.val(best_itr[1]))

            self.best = best_itr
            self.best_conv = best_itr_conv
            self.best_ref = best_itr_ref


def Interolog_Pack1(p1, p2, homology, refseq2gene, reported_ppi):

    itr = Interolog(p1, p2)
    itr.set_homology(homol1 = homology, homol2 = homology)
    itr.set_conv(conv1 = refseq2gene, conv2 = refseq2gene)
    itr.set_subj_ppi(reported_ppi)
    itr.calc_interolog()
    return itr


if __name__ == "__main__":
    import Usefuls.TmpFile
    protein1 = "A"
    protein2 = "B"

    testppi_subj = Usefuls.TmpFile.TmpFile_III("""

1       22      Int-*
11      222     Int-**

""")

    testhomol1 = Usefuls.TmpFile.TmpFile_III("""

Query@ID Subject@ID E-value Identity_abs Positive_abs Overlap Query@length Subject@length Query@start Query@end Subject@start Subject@end

A        a          1.0e-3  0 0 0 0 0 0 0 0 0
A        aa         2.0e-4  0 0 0 0 0 0 0 0 0
A        aaa        5.0e-10 0 0 0 0 0 0 0 0 0

""", tospace = "@")

    test_conv1 = Usefuls.TmpFile.TmpFile_III("""

a       1
aa      11
aaa     111

""")

    testhomol2 = Usefuls.TmpFile.TmpFile_III("""

Query@ID Subject@ID E-value Identity_abs Positive_abs Overlap Query@length Subject@length Query@start Query@end Subject@start Subject@end

B        b          3.5     0 0 0 0 0 0 0 0 0
B        bb         1.0e-3  0 0 0 0 0 0 0 0 0
B        bbb        4.5e-10 0 0 0 0 0 0 0 0 0

""", tospace = "@")

    test_conv2 = Usefuls.TmpFile.TmpFile_III("""

b       2
bb      22

""")

    ppi_subj = Data_Struct.Hash2.Hash("S")

    ppi_subj.read_file(filename = testppi_subj.filename(),
		       Key_cols = [0,1], Val_cols = [2])

    homol1 = HomologyDescr4(testhomol1.filename())

    conv1 = Data_Struct.Hash2.Hash("S")
    conv1.read_file(filename = test_conv1.filename(),
		    Key_cols = [0], Val_cols = [1])

    homol2 = HomologyDescr4(testhomol2.filename())

    conv2 = Data_Struct.Hash2.Hash("S")
    conv2.read_file(filename = test_conv2.filename(),
		    Key_cols = [0], Val_cols = [1])

    itr = Interolog(p1 = protein1, p2 = protein2)
    itr.set_homology(homol1 = homol1, homol2 = homol2)

    itr.set_subj_ppi(ppi_subj)
    itr.set_conv(conv1 = conv1, conv2 = conv2)
    itr.calc_interolog()
    print itr.get_interolog()
    print itr.get_best()
    print itr.get_best_conv()
    print itr.get_best_eval()
    print itr.get_best_ref()

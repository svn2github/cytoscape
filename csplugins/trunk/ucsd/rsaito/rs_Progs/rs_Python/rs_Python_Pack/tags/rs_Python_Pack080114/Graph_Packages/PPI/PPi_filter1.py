#!/usr/bin/env python

import PPi4
import Protein

class PPi_filter1:
    def __init__(self, ppi):
        if not isinstance(ppi, PPi4.PPi4):
            raise "Instance type mismatch."

        self.ppi = ppi
        self.func_set = []
        self.param_set = []

    def get_ppi(self):
        return self.ppi

    def set_func_param(self, ifunc, iparam):
        self.func_set.append(ifunc)
        self.param_set.append(iparam)

    def ppi_filter(self):
        # Judge must be done as a part of PPi4's method because
        # judge may depend on global structure of PPI

        ppi_filtered = PPi4.PPi4()

        for pair in self.get_ppi().get_all_ppi():
            if self.pair_judge(pair) is True:
                ppi_filtered.set_pair(pair)

        return ppi_filtered

    def pair_judge(self, pair):

        for i in range(len(self.func_set)):
            if not self.func_set[i](self.get_ppi(),
                                    pair,
                                    self.param_set[i]):
                return False
        return True


if __name__ == "__main__":
    import Usefuls.TmpFile
    import Usefuls.Data_Sheet

    def test_filt_interactor1(ppi, pair, limit):

        p1, p2 = pair.get_pair()
        if (len(ppi.interactor(p1)) +
            len(ppi.interactor(p2))) < limit:
            return False
        else:
            return True


    def test_filt_interactor2(ppi, pair, thres):
        p1, p2 = pair.get_pair()
        if p1.get_value("TEST1")[0] + p2.get_value("TEST1")[0] < thres:
            return False
        else:
            return True


    tmp_obj = Usefuls.TmpFile.TmpFile_III("""

Protein-A     Protein-B   a
Protein-D     Protein-E   d
Protein-E     Protein-F   e
Protein-X     Protein-Y   f
Protein-Y     Protein-Z   f


""")

    tmp_obj2 = Usefuls.TmpFile.TmpFile_III("""

---------   Data-1  Data-2  Data-3
Protein-A      1.0     2.0    -3.2
Protein-B     -2.0     1.2     3.1
Protein-D      1.0     2.3    -5.1
Protein-E      1.0
Protein-F      2.0
Protein-X      1.0
Protein-Y      0.5
Protein-Z      0.5

    """)

    protein_set = Protein.Protein_Set()
    ppi_pre_filt = PPi4.PPi4(protein_set)
    ppi_pre_filt.read_from_file(tmp_obj.filename(), 0, 1, 2)
    ppi_pre_filt.both_dir()

    numdata = Usefuls.Data_Sheet.Data_Sheet()
    numdata.read_sheet_file(tmp_obj2.filename(), "\t")
    numdata.numerize()
    protein_set.set_values("TEST1", numdata)
    protein_b = protein_set.get_protein_by_name("Protein-Z")
    print protein_b.get_value("TEST1")

    print "Before:"
    ppi_pre_filt.ppi_display()

    ppi_filter = PPi_filter1(ppi_pre_filt)
    ppi_filter.set_func_param(test_filt_interactor1, 3)
    ppi_filter.set_func_param(test_filt_interactor2, 1)

    ppi_filtered = ppi_filter.ppi_filter()
    print "After:"
    ppi_filtered.ppi_display()
    ppi_filtered.get_protein_set().display_protein_set()



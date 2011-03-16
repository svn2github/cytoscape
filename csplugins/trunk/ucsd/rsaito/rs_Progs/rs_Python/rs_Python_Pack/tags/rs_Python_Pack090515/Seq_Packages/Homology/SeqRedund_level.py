#!/usr/bin/env python

import Homology3_descr
import Usefuls.Redund_level1

class SeqRedund_level(Usefuls.Redund_level1.Redund_level):
    def __init__(self, ilist, homology_descr):
        if not isinstance(homology_descr, Homology3_descr.HomologyDescr3):
            raise "Instance type mismatch."

        self.homology_descr = homology_descr
        Usefuls.Redund_level1.Redund_level.__init__(self, ilist)

    def _redund_check(self, seq1, seq2):
        if self.homology_descr.e_value(seq1, seq2) is None:
            return 0
        else:
            return 1

        
if __name__ == "__main__":

    homol_file = "HomologyDescr_test"
    homol = Homology3_descr.HomologyDescr3(homol_file)

    srl = SeqRedund_level(["NM_003802",
                           "MYH4_HUMAN",
                           "TAF1_HUMAN",
                           "MYH1_HUMAN",
                           "NM_004606"], homol)
    print srl.redund_level()



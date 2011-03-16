#!/usr/bin/env python

from Data_Struct.Data_Sheet2 import Data_Sheet
from Data_Struct.Hash2 import Hash
from Usefuls.rsConfig import RSC

class PullDown:
    def __init__(self, filnam):
        self.filnam = filnam
        self.pd = Data_Sheet(self.filnam, "\t")
        self.geneid_to_pd = Hash("S")
        self.geneid_to_pd.read_file_hd(filename = self.filnam,
                                       Key_cols_hd = ["Prey GeneID",
                                                      "Bait GeneID"],
                                       Val_cols_hd = ["Prey To Prey",
                                                      "Prey To Bait"])


    def pd_check(self, preyid):
        return self.pd.get_datum(preyid, "Prey To Prey")

    def pd_rev_check(self, preyid):
        return self.pd.get_datum(preyid, "Prey To Bait")

    def pcr_check(self, preyid):
        return self.pd.get_datum(preyid, "Real Time PCR")

    def geneid2pd(self, geneid1, geneid2):
        pd12 = self.geneid_to_pd.val_accord_hd(
            geneid1 + "\t" + geneid2, "Prey To Prey")
        pd21 = self.geneid_to_pd.val_accord_hd(
            geneid2 + "\t" + geneid1, "Prey To Prey")

        if pd12 == "OK":
            return "OK"
        if pd21 == "OK":
            return "OK"
        return pd12


if __name__ == "__main__":

    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

    pd = PullDown(rsc.PullDown)

    print pd.pd_check("S20051122_C05_04_D10.seq")
    print pd.pd_check("T051018_E2_B06.seq")
    print pd.pd_check("XXXXXX")
    print pd.geneid2pd("1386", "3725")
    print pd.geneid2pd("3725", "1386")
    print pd.geneid2pd("XXX", "YYY")
    print pd.geneid2pd("801", "4087")

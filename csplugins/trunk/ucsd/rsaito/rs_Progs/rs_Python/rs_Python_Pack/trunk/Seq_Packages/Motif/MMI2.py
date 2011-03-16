#!/usr/bin/env python

import string

from General_Packages.Data_Struct.Hash2 import Hash
from General_Packages.Data_Struct.NonRedSet1 import NonRedSet

class MMI:
    def __init__(self, mmi_file):

        mmi = Hash("N")
        mmi.read_file(filename = mmi_file,
                      Key_cols = [0,1],
                      Val_cols = [])

        self.mmi = mmi

    def get_mmi_from_motifs(self, motifs1, motifs2, sep, both=False):

        mmi = {}
        for m1 in motifs1:
            for m2 in motifs2:
                if self.mmi.has_pair(m1, m2):
                    mmi[ m1 + sep + m2 ] = ""
                    if both:
                        mmi[ m2 + sep + m1 ] = ""
        return mmi.keys()


    def mmi_has_pair(self, m1, m2):
	return self.mmi.has_pair(m1, m2)


    def get_all_mmis(self):
        ret = []
        done = {}

        for ekey_f in self.mmi.keys():
            m1, m2 = ekey_f.split("\t")
            ekey_r = m2 + "\t" + m1
            if (not ekey_f in done) and (not ekey_r in done):
                ret.append((m1, m2))
            done[ ekey_f ] = ""
            done[ ekey_r ] = ""

        return ret


if __name__ == "__main__":

    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsIVV_Config")

    mmi_info = MMI(rsc.iPfam)

    print mmi_info.get_mmi_from_motifs(["A", "B", "C", "bZIP_1"],
				       ["D", "E", "bZIP_2"], ":-:")
    print mmi_info.get_all_mmis()

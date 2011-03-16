#!/usr/bin/env python

import sys
sys.path.append("../")

import string
import Usefuls.Hash
import Usefuls.NonRedSet

class MMI1:
    def __init__(self, mmi_file):
        
        mmi = Usefuls.Hash.Hash("N")
        mmi.read_file(filename = mmi_file,
                      Key_cols = [0,1],
                      Val_cols = [])
    
        self.mmi = mmi

        self.motif2seqid = False

    def get_mm_pair_from_motifs(self, motifs1, motifs2, sep, both=False):

        mmi = {}
        for m1 in motifs1:
            for m2 in motifs2:
                mmi[ m1 + sep + m2 ] = ""
                if both:
                    mmi[ m2 + sep + m1 ] = ""
        return mmi.keys()


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


if __name__ == "__main__":
    mmi_file = "../../Motifs/MMI_iPfam"
    mmi_info = MMI1(mmi_file)
    print mmi_info.get_mm_pair_from_motifs(["A", "B", "C"],
					   ["D", "E"], ":-:")

    print mmi_info.get_mmi_from_motifs(["A", "B", "C", "bZIP_1"],
				       ["D", "E", "bZIP_2"], ":-:")

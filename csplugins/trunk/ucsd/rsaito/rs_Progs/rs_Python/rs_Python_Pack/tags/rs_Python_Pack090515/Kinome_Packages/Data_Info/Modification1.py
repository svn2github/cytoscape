#!/usr/bin/env python

import Modified_Seq1

class Modification:
    def __init__(self, mod_seq, site, mtype):

        self.mod_seq = mod_seq
        self.site = site
        self.mtype = mtype

    def get_site(self):

        return self.site

    def get_mtype(self):

        return self.mtype

    def get_mod_seq(self):
        return self.mod_seq

if __name__ == "__main__":
    
    from Seq_Packages.Seq.SingleSeq2 import SingleSeq

    sseq = SingleSeq("atcg")
    mod_seq = Modified_Seq1.Modified_Seq(sseq)
    modif = Modification(mod_seq, 2, "phospho")

    print modif.get_site()
    print modif.get_mtype()
    print modif.get_mod_seq()

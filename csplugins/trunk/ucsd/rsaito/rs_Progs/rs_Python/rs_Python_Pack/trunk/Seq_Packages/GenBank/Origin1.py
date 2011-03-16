#!/usr/bin/env python

from KeyPattern import *
from Seq_Packages.Seq.Useful_Seq1 import reverse_complement

class Origin:
    def __init__(self):
        self.seq = ""
        self.cseq = None

    def reader(self, fh):
        
        while True:
            line = fh.readline()
            if line == "" or line.startswith(entry_end):
                return None
            elif line.startswith(origin):
                break

        seqs = []
        while True:
            line = fh.readline()
            if line.startswith(entry_end):
                break
            nucs = nucs_search.sub("", line)
            seqs.append(nucs)

        self.seq = "".join(seqs)

        return len(self.seq)
    
    def get_seq(self):
        return self.seq

    def get_cseq(self):
        if not self.cseq:
            self.cseq = reverse_complement(self.seq)
        return self.cseq


if __name__ == "__main__":
    
    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("GenBank")
    
    from Data_Struct.Str2Dict import Str2Dict
    
    orig = Origin()
    fh = open(rsc.Primates_test, "r")
    print orig.reader(fh)
    print orig.get_cseq()
    
    fh = open(rsc.Ecoli, "r")
    print orig.reader(fh)
    print Str2Dict(orig.get_seq())

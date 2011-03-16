#!/usr/bin/env python

from KeyPattern import *

class Origin:
    def __init__(self):
        self.seq = ""

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


if __name__ == "__main__":
    
    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("GenBank")
    
    orig = Origin()
    fh = open(rsc.Primates_test, "r")
    print orig.reader(fh)

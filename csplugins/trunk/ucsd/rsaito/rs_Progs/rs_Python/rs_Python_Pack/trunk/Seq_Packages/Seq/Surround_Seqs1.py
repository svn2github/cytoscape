#!/usr/bin/env python

from SingleSeq2 import SingleSeq

class Surround_Seqs:

    def __init__(self, pos0, seqs = None):
        
        if seqs is None: seqs = []

        self.pos0 = pos0
        self.seqs = seqs

        for i in range(len(self.seqs) - 1):
            if len(self.seqs[i]) != len(self.seqs[i+1]):
                raise "Sequence length not identical."

    def add_seq(self, seq):
        if (self.seqs == [] or
            len(self.seqs[0]) == len(seq)):
            self.seqs.append(seq)
        else:
            raise "Sequence length not identical."

    def get_seqs(self):
        return self.seqs

    def upstream(self):

        ret = []
        for seq in self.seqs:
            ret.append(seq[0:self.pos0])
        return ret

    def start(self):

        ret = []
        for seq in self.seqs:
            ret.append(seq[self.pos0])
        return ret

    def downstream(self):

        ret = []
        for seq in self.seqs:
            ret.append(seq[self.pos0+1:])
        return ret

    def ret_display1(self):
        
        ret = ""
        for seq in self.get_seqs():
            ret += ">" + (seq.get_ID() or "NoID") + "\n"
            ret += seq[0:self.pos0] + "" + seq[self.pos0:] + "\n" 
        return ret

if __name__ == "__main__":
    
    seqs = Surround_Seqs(3, [SingleSeq("--atgccc", False),
                             SingleSeq("ccatcccc", False),
                             SingleSeq("tagatccc", False)])
    seqs.add_seq(SingleSeq("aaaaaaaa"))
    print seqs.upstream()
    print seqs.start()
    print seqs.downstream()
    print seqs.ret_display1()

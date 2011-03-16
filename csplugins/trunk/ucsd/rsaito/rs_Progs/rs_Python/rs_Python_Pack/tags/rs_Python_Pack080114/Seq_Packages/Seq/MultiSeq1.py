#!/usr/bin/env python

from SingleSeq2 import SingleSeq

class MultiSeq:
    def __init__(self):
        
        self.singleseqs = []
        self.start_poss = []
    
    def add_singleseq(self, seq, pos = 0):
        
        self.singleseqs.append(seq)
        self.start_poss.append(pos)

    def get_singleseqs(self):
        return self.singleseqs

    def get_seqs(self):

        ret = []
        for singleseq in self.get_singleseqs():
            ret.append(singleseq.get_seq())
        return ret

    def get_aligned(self, gapm = "-"):
        
        ret = MultiSeq()
        
        start_poss = []
        end_poss =[]
        for i in range(len(self.singleseqs)):
            singleseq = self.singleseqs[i]
            start_pos = self.start_poss[i]
            end_pos = start_pos + len(singleseq) - 1
            start_poss.append(start_pos)
            end_poss.append(end_pos)
        min_pos = min(start_poss)
        max_pos = max(end_poss)

        for i in range(len(self.singleseqs)):
            singleseq = self.singleseqs[i]
            start_pos = self.start_poss[i]
            end_pos = start_pos + len(singleseq) - 1

            ret.add_singleseq(SingleSeq(gapm * (start_pos - min_pos) +
                                        singleseq.get_seq() +
                                        gapm * (max_pos - end_pos),
                                        wash = False))

        return ret

    


if __name__ == "__main__":
    
    sseq1 = SingleSeq("aaaaa")
    sseq2 = SingleSeq("ccccc")
    sseq3 = SingleSeq("ggggg")
    
    ms = MultiSeq()
    ms.add_singleseq(sseq1, 5)
    ms.add_singleseq(sseq2, 3)
    ms.add_singleseq(sseq3, 2)

    print ms.get_seqs()
    print ms.get_aligned().get_seqs()
    

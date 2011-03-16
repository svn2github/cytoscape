#!/usr/bin/env python

import os
import tempfile
import Useful_Seq1

import General_Packages.Usefuls.String_I

class SingleSeq:

    def __init__(self, seq = "", wash = True):

        if wash:
            self.seq = Useful_Seq1.wash_seq(seq)
        else:
            self.seq = seq
        self.id = None

    def set_ID(self, id):
        self.id = id

    def get_seq(self):
        return self.seq

    def get_ID(self):
        return self.id

    """
    def set_start_pos(self, pos):
        self.start_pos = pos

    def get_start_pos(self):
        return self.start_pos
    """

    def return_fasta(self, blk):
        return Useful_Seq1.return_fasta(self.get_seq(),
                                        self.get_ID(),
                                        blk)

    def return_neatseq(self, blk):
        return Useful_Seq1.return_neatseq(self.get_seq(), blk)

    def return_fasta_obj(self):
        sfasta = SingleFasta()
        sfasta.read_singleseq(self)
        return sfasta

    def get_seq_frag(self, pos1, pos2, gap = "-"):

        seq_len = len(self.get_seq())
        before = ""
        after  = ""
        if pos1 < 0:
            before = gap * -pos1
            pos1 = 0
        if pos2 >= seq_len:
            after = gap * (pos2 - seq_len + 1)
            pos2 = seq_len - 1
        return before + self.get_seq()[pos1:pos2+1] + after

    def matcher(self, subseq):
        return General_Packages.Usefuls.String_I.matcher(
            subseq.get_seq(), self.get_seq())

    def __getitem__(self, index):
        return self.get_seq()[index]

    def __len__(self):
        return len(self.get_seq())


class SingleFasta:
    def __init__(self, single_fasta_file = None):

        self.single_fasta_file = single_fasta_file
        self.id = None
        self.erase_fasta_file = False
        
    def set_ID(self, id):
        self.id = id

    def get_ID(self):
        return self.id

    def set_ID_auto(self):
        self.set_ID(self.read_ID_from_file())

    def read_ID_from_file(self):
        ids = Useful_Seq1.read_fasta_ids(self.single_fasta_file)
        if len(ids) > 1:
            raise "Single Fasta File expected."
        return ids[0]

    def set_erase(self, erase = True):
        self.erase_fasta_file = erase

    def get_singleseq(self):
        seqs = Useful_Seq1.read_fasta(self.single_fasta_file).values()
        if len(seqs) > 1:
            raise "Single Fasta File expected."
        seq = seqs[0]
        singleseq = SingleSeq(Useful_Seq1.wash_seq(seq))
        singleseq.set_ID(self.get_ID())
        return singleseq

    def read_singleseq(self, singleseq):

        if not isinstance(singleseq, SingleSeq):
            raise "Instance type mismatch."

        self.set_ID(singleseq.get_ID())

        if self.single_fasta_file is None:
            self.single_fasta_file = tempfile.mktemp()
            fh_w = open(self.single_fasta_file, "w")
            fh_w.write(singleseq.return_fasta(50))
            self.set_erase(True)
            return self.single_fasta_file

        else:
            raise "Re-defining FASTA file..."

    def get_fasta_file(self):
        return self.single_fasta_file

    def __del__(self):
        if self.erase_fasta_file is True:
            os.remove(self.single_fasta_file)


if __name__ == "__main__":
    seq1 = SingleSeq("""
atgcatgctagctgatcgatgctagctagtcgatcgatgctagtcgatcgaaaaaaaaaaa
atgcatgctagctgatcgatgctagctagtcgatcgatgctagtcgatcgaaaaaaaaaaa
atgcatgctagctgatcgatgctagctagtcgatcgatgctagtcgatcgaaaaaaaaaaa
""")
    seq1.set_ID("TestSeq")
    print seq1.return_neatseq(30)
    print seq1.return_fasta(30)

    import Usefuls.TmpFile as TMP
    tmpseq = TMP.TmpFile_II("""
>Seq1|First The first sequence
aaaattttccccgggg
aaaattttccccgggg
""")

    seq2 = SingleFasta(tmpseq.filename())
    print seq2.get_singleseq().return_neatseq(20)
    seq2.set_ID_auto()
    print seq2.get_ID()

    seq3 = SingleFasta()
    seq3.read_singleseq(seq1)
    
    print seq3.get_fasta_file()
    print seq3.get_ID()

    seq = SingleSeq("abcdefg")
    print seq.get_seq_frag(-3, 10)
    print seq.get_seq_frag(1, 3)
              

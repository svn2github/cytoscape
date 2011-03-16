#!/usr/bin/env python

import sys
import os

import SingleSeq2
import tempfile
import Useful_Seq1

from General_Packages.Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsBioinfo_Config")

class MultiFasta:

    fastacmd_EXEC = rsc.FASTACMD # Default
    
    def set_fastacmd_EXEC(self, path):
        MultiFasta.fastacmd_EXEC = path

    def __init__(self, multifasta_file):
        self.mf_file = multifasta_file

    def get_singlefasta(self, id, start = 0, end = 0):
        tmpfile = tempfile.mktemp()
        fasta_input = "%s -d %s -s %s -L%s,%s" % (self.fastacmd_EXEC,
                                                  self.mf_file,
                                                  id,
                                                  start,
                                                  end)
        os.system(fasta_input  + " > " + tmpfile)

        if os.path.getsize(tmpfile) == 0:
            os.remove(tmpfile)
            return None

        sfasta = SingleSeq2.SingleFasta(tmpfile)
        sfasta.set_erase()

        return sfasta

    def get_singlefastafile(self, id, start = 0, end = 0):
        tmpfile = tempfile.mktemp()
        fasta_input = "%s -d %s -s %s -L%s,%s" % (self.fastacmd_EXEC,
                                                  self.mf_file,
                                                  id,
                                                  start,
                                                  end)
        os.system(fasta_input  + " > " + tmpfile)
        return tmpfile


class MultiFasta_MEM:
    def __init__(self, multifasta_file, parseid = True):
        self.seqs = {}
        self.mf_file = multifasta_file        
        seqs = Useful_Seq1.read_fasta(self.mf_file, parseid)
    
        for id in seqs:
            seqid = self.extract_id(id)
            sequence = SingleSeq2.SingleSeq(seqs[id])
            self.seqs[ seqid ] = sequence

    def get_sequences(self):
        return self.seqs

    def get_sequence(self, id):
        return self.seqs[ id ]

    def get_ids(self):
        return self.seqs.keys()
        
    def extract_id(self, id):
        """ This can be inherited to subclasses for modification. """
        return id
    
    def __iter__(self):
        return self.seqs.__iter__()
    
    def out_fasta(self, id, header = True, blk = 60):
        out = ""
        if header:
            out = ">%s\n" % id
        out += Useful_Seq1.return_fasta(self.get_sequence(id), "", blk)
        return out
    
if __name__ == "__main__":

    """
    db =sys.argv[1]
    seqid = sys.argv[2]

    mf = MultiFasta(db)
    # MultiFasta.set_fastacmd_EXEC(mf, rsc.FASTACMD)
    
    sfasta = mf.get_singlefasta(seqid, 1,22)
    print mf.get_singlefastafile(seqid, 1, 22)
    sfasta.set_ID_auto()
    print sfasta.get_singleseq().return_neatseq(20)
    print sfasta.get_singleseq().get_ID()
    """

    import Usefuls.TmpFile as TMP
    tmpseq = TMP.TmpFile_II("""
>Seq1|First The first sequence
aaaattttccccgggg
aaaattttccccgggg

>Seq2|Second The second sequence
acgtacgt
acgtacgt

>Seq3|Third|San Mittsume

aattccgg
aaccggtt

""")
    mfmem = MultiFasta_MEM(tmpseq.filename(), parseid = False)
    print mfmem.get_ids()
    for id in mfmem:
        # print id
        # print mfmem.get_sequences()[id].return_neatseq(5)
        print mfmem.out_fasta(id, header = True)
        
    


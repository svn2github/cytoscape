#!/usr/bin/env python

import sys
import os

import SingleSeq2
import tempfile
import Useful_Seq1

from Data_Struct.Dict_Ordered import Dict_Ordered
from Calc_Packages.Math.MathI import divide_int_near_equal

from General_Packages.Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsBioinfo_Config")

class FASTACMD_ERROR:
    def __init__(self, given_command, error_message):
        
        self.given_command = given_command
        self.error_message = error_message
        
    def descr(self):
               
        return """### fastacmd error occurred. ###
Given command  : %s
fastacmd Error : %s
""" % (self.given_command, self.error_message)

    def __repr__(self):
        return 'FASTACMD_ERROR(given_command = "%s", error_message = "%s")' \
    % (self.given_command,
       self.error_message)


class MultiFasta:

    fastacmd_EXEC = rsc.FASTACMD # Default
    
    def set_fastacmd_EXEC(self, path):
        MultiFasta.fastacmd_EXEC = path

    def __init__(self, multifasta_file):
        self.mf_file = multifasta_file

    def get_singlefasta(self, id, start = 0, end = 0):
        tmpfile   = tempfile.mktemp()
        errorfile = tempfile.mktemp()
        fasta_input = "%s -d %s -s %s -L%s,%s" % (self.fastacmd_EXEC,
                                                  self.mf_file,
                                                  id,
                                                  start,
                                                  end)
        
        os.system("%s > %s 2> %s" % (fasta_input, tmpfile, errorfile))
        error_message = "".join(open(errorfile))
        os.remove(errorfile)

        if error_message:         
            os.remove(tmpfile)
            raise FASTACMD_ERROR(fasta_input, error_message)

        if os.path.getsize(tmpfile) == 0:
            os.remove(tmpfile)
            raise FASTACMD_ERROR(fasta_input, "No sequence obtained")

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
    def __init__(self, multifasta_file = None, parseid = True, shuffle = False):
        # self.seqs = {}
        self.seqs = Dict_Ordered()
        
        self.mf_file = multifasta_file
        
        if multifasta_file:
            seqs = Useful_Seq1.read_fasta2(self.mf_file, parseid)

            # print "Sequence read finished. Making object..."
    
            for id in seqs:
                sequence = SingleSeq2.SingleSeq(seqs[id]) # NOTICE: It was previously seqs[seqid].
                seqid = self.extract_id(id)
                sequence.set_ID(seqid)
                self.seqs[ seqid ] = sequence

            # print "IDs read:", self.seqs.keys()[0:10]
            
        if shuffle:
            self.seqs.shuffle_keys()
        
    def get_sequences(self):
        return self.seqs

    def get_sequence(self, id):
        return self.seqs[ id ]

    def set_sequence(self, id, sequence): # Reference to multi-fasta file will be deleted.
        self.mf_file = None # Exact correspondence to multi-fasta file is no longer valid
        self.seqs[ id ] = sequence

    def get_ids(self):
        return self.seqs.keys()
        
    def extract_id(self, id):
        """ This can be inherited to subclasses for modification. """
        return id
    
    def __iter__(self):
        return self.seqs.__iter__()
    
    def divide_to_seqs_set(self, num):
                
        mfs = []        
        idnum = len(self.get_ids())
        frag_sizes = divide_int_near_equal(idnum, num)
        
        i = 0
        for frag_size in frag_sizes:
            fr = i
            to = i + frag_size
            mf = MultiFasta_MEM(None)
            for j in range(fr, to):
                id  = self.get_ids()[j]
                seq = self.get_sequence(id)
                mf.set_sequence(id, seq)
                
            # print "Sequences from %d to %d" % (fr, to)
            mfs.append(mf)
            i += frag_size
    
        return mfs
    
    def out_fasta(self, id, header = True, blk = 60):
        out = ""
        if header:
            header = "%s" % id
        else:
            header = ""
        out += Useful_Seq1.return_fasta(self.get_sequence(id),
                                        header, blk)
        return out

    def out_fasta_all(self, header = True, blk = 60):
        out_f = ""
        for id in self:
            out_f += self.out_fasta(id, header, blk)
        return out_f
    
if __name__ == "__main__":

    """
    db =sys.argv[1]
    seqid = sys.argv[2]

    mf = MultiFasta(db)
    # MultiFasta.set_fastacmd_EXEC(mf, rsc.FASTACMD)
    
    try:
        sfasta = mf.get_singlefasta(seqid, 1,22)
    except FASTACMD_ERROR, error:
        sys.stderr.write(error.descr())
        sys.exit()
    
    print mf.get_singlefastafile(seqid, 1, 22)
    sfasta.set_ID_auto()
    print sfasta.get_singleseq().return_neatseq(20)
    print sfasta.get_singleseq().get_ID()

    sys.exit()
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

>Seq4|Fourth|Yon Yottsume

aattcnnn
aaccggtt

>Seq5

aattcnnn
aaccggtt

>Seq6

aattcnnn
aaccggtt

>Seq7

aattcnnn
aaccggtt

>Seq8

aattcnnn
aaccggtt

>Seq9

aattcnnn
aaccggtt
aattcnnn
aaccggtt

>Seq10

aattcnnn
aaccggtt
aattcnnn
aaccggtt

""")
    mfmem = MultiFasta_MEM(tmpseq.filename(), parseid = False)
    print mfmem.get_ids()
    for id in mfmem:
        # print id
        # print mfmem.get_sequences()[id].return_neatseq(5)
        print mfmem.out_fasta(id, header = True)
        
    print "Mult-fasta file division..."
    mf_set = mfmem.divide_to_seqs_set(4)
    counter = 0
    for mf in mf_set:
        print "Multi-Fasta #", counter
        print mf.out_fasta_all()
        counter += 1
        

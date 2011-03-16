#!/usr/bin/env python

import SingleSeq
import tempfile
import os

class MultiFasta_fastacmd:

    fastacmd_EXEC = "/pub/software/blast-2.2.14/bin/fastacmd"
    
    def set_fastacmd_EXEC(self, path):
        MultiFasta_fastacmd.fastacmd_EXEC = path

    def __init__(self, multifasta_file):
	self.mf_file = multifasta_file
        	
    def get_seq(self, id):
	tmpfile = tempfile.mktemp()
	fasta_input = "%s -d %s -s %s " % (self.fastacmd_EXEC,
                                                self.mf_file,
                                                id)
	os.system(fasta_input  + " > " + tmpfile)
	seqobj = SingleSeq.SingleFasta(tmpfile)
	os.remove(tmpfile)
	return seqobj

    def get_seqfile(self, id):
        tmpfile = tempfile.mktemp()
	fasta_input = "%s -d %s -s %s " % (self.fastacmd_EXEC,
                                           self.mf_file,
                                           id)
	os.system(fasta_input  + " > " + tmpfile)
	return tmpfile
    
if __name__ == "__main__":
    import Usefuls.rsConfig

    rsc = Usefuls.rsConfig.RSC("../../../rsIVV_Config")
    
    db =sys.argv[1]
    seqid = sys.argv[2]

    mf = MultiFasta_fastacmd(db)
    MultiFasta_fastacmd.set_fastacmd_EXEC(
        mf,
        rsc.FASTACMD)
    
    seq = mf.get_seq(seqid)
    print seq.return_neat(60)
    print mf.get_seqfile(seqid)

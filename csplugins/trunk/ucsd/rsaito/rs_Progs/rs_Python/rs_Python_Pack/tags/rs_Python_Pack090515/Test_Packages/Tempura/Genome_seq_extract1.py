#!/usr/bin/env python

from Seq_Packages.Seq.MultiFasta2 import MultiFasta

db = "/Users/rsaito/UNIX/Work/Research/Data_Public/Genome_Seq/hg17/chr1.fa"
seqid = "chr1"
start = 1
end = 10

mf = MultiFasta(db)
print mf.get_singlefasta(seqid, start, end).get_singleseq().get_seq()
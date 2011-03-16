#!/usr/bin/env python

import sys

from Seq_Packages.Seq.MultiFasta2 import MultiFasta_MEM
from Seq_Packages.Seq.Useful_Seq1 import read_fasta

mf = MultiFasta_MEM(sys.argv[1], parseid = False)

for header_line in mf:
    r = header_line.split("|")
    onc_id = r[3].split(".")[0]
    descr = " ".join(header_line.split(" ")[1:]) 
    print ">lcl|%s %s" % (onc_id, descr)
    print mf.out_fasta(header_line, header = False)



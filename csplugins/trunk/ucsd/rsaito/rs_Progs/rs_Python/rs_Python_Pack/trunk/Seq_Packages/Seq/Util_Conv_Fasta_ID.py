#!/usr/bin/env python

import sys
from Data_Struct.Hash2 import Hash
from MultiFasta2 import MultiFasta

fasta_file = sys.argv[1]
conv_file = sys.argv[2]

id_info = Hash("S")
id_info.read_file(conv_file,
                  Key_cols = [ 0 ],
                  Val_cols = [ 1 ])

mf = MultiFasta(fasta_file)

for old_id in id_info:
    new_id = id_info[old_id]
    sf = mf.get_singlefasta(old_id)
    sseq = sf.get_singleseq()
    sseq.set_ID("%s %s" % (new_id, old_id))
    print sseq.return_fasta(50)

    
    
    


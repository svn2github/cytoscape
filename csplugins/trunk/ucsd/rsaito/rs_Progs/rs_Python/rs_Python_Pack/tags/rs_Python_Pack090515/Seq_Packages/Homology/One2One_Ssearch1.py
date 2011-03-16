#!/usr/bin/env python

import Fasta_align_pack2
import Seq_Packages.Seq.MultiFasta2 as MultiFasta

from Usefuls.Table_maker import Table_row
from Homology_term1 import *

def ss_output(id1, id2, ss, tb):
    if ss:
        eval    = ss.eval()
        ident   = int(ss.ident() * ss.overlp())
        similar = int(ss.similar() * ss.overlp())
        overlap = ss.overlp()
        q_len   = ss.q_len()
        s_len   = ss.s_len()
        q_start = ss.q_start()
        q_end   = ss.q_end()
        s_start = ss.s_start()
        s_end   = ss.s_end()
    else:
        eval    = ""
        ident   = ""
        similar = ""
        overlap = ""
        q_len   = ""
        s_len   = ""
        q_start = ""
        q_end   = ""
        s_start = ""
        s_end   = ""          
    tb.append(t_query_ID, id1)
    tb.append(t_subject_ID, id2)
    tb.append(t_e_value, `eval`)
    tb.append(t_identity_abs, `ident`)
    tb.append(t_positive_abs, `similar`)
    tb.append(t_overlap, `overlap`)
    tb.append(t_query_len, `q_len`)
    tb.append(t_subject_len, `s_len`)
    tb.append(t_query_start, `q_start`)
    tb.append(t_query_end, `q_end`)
    tb.append(t_subject_start, `s_start`)
    tb.append(t_subject_end, `s_end`)
    tb.output("\t")

class One2One_Ssearch:
    def __init__(self,
                 mfasta_file1,
                 mfasta_file2):
        
        self.mf1 = MultiFasta.MultiFasta(mfasta_file1)
        self.mf2 = MultiFasta.MultiFasta(mfasta_file2)
        
    def align(self, id1, id2):    
    
        ss  = Fasta_align_pack2.Ssearch()
        sf1 = self.mf1.get_singlefasta(id1)
        if not sf1:
            return None
        sf2 = self.mf2.get_singlefasta(id2)
        if not sf2:
            return None
        
        ss.set_fasta_obj(sf1, sf2)
        ss.exec_fasta()
        return ss

    def exhaustive(self, *args, **kwargs):

        idpair_table_file = args[0]
        fh = open(idpair_table_file) 
        tb = Table_row()
        for line in fh:
            r = line.rstrip().split("\t")
            id1, id2 = r
            ss = self.align(id1, id2)
            ss_output(id1, id2, ss, tb)

if __name__ == "__main__":
    
    mfasta_db1 = raw_input("Fasta DB #1:")
    mfasta_db2 = raw_input("Fasta DB #2:")
    idpair_tb  = raw_input("ID pair table:")
    
    fasta = One2One_Ssearch(mfasta_db1, mfasta_db2)
    fasta.exhaustive(idpair_tb)
    """
    while True:
        id1 = raw_input("ID #1:")
        id2 = raw_input("ID #2:")
        ss = fasta.align(id1, id2)
        print ss.ident()
    """ 
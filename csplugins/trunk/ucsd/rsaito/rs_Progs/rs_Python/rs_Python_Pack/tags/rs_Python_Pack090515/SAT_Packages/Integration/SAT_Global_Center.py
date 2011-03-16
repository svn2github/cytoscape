#!/usr/bin/env python

import sys

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("rsSAT_Config")
rsc_gene = RSC_II("NCBI_GeneInfo")

from Data_Struct.Hash2 import Hash

strand_s = {"+" : "+", "-" : "-" }
strand_o = {"+" : "-", "-" : "+" }

def get_accession2geneid():

    class Hash_without_version_k(Hash):
        def conv_key(self, k):
            return k.split(".")[0]

    global accession2geneid

    if 'accession2geneid' not in globals():
        sys.stderr.write("Reading gene info ... (accession -> geneid)\n")
        accession2geneid  = Hash_without_version_k("S")
        accession2geneid.read_file(rsc_gene.gene2accession_hs,
                                   Key_cols = [3],
                                   Val_cols = [1])
        
    return accession2geneid

def get_geneid2accession():

    class Hash_without_version_v(Hash):
        def conv_val(self, v):
            return v.split(".")[0]

    global geneid2accession

    if 'geneid2accession' not in globals():
        sys.stderr.write("Reading gene info ... (geneid -> accession)\n")
        geneid2accession  = Hash_without_version_v("A")
        geneid2accession.read_file(rsc_gene.gene2accession_hs,
                                   Key_cols = [1],
                                   Val_cols = [3])
        
    return geneid2accession
    



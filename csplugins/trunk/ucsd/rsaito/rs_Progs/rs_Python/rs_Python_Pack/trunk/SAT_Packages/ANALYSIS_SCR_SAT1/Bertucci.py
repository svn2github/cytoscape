#!/usr/bin/env python

from Data_Struct.Hash2 import Hash
from Usefuls.rsConfig import RSC_II
rsc      = RSC_II("rsSAT_Config")
rsc_gene = RSC_II("NCBI_GeneInfo")

class Hash_without_version_k(Hash):
    def conv_key(self, k):
        return k.split(".")[0]

class Hash_without_version_v(Hash):
    def conv_val(self, v):
        return v.split(".")[0]

def accession_to_updown_hash():
    updown = Hash("S")
    updown.read_file_hd(rsc.Colon_Bertucci,
                        Key_cols_hd = ["Access. N"],
                        Val_cols_hd = ["Status"])
    del updown.all_data()['']

    accession2geneid  = Hash_without_version_k("S")
    accession2geneid.read_file(rsc_gene.gene2accession_hs,
                               Key_cols = [3],
                               Val_cols = [1])
    
    geneid2accessions = Hash_without_version_v("A")
    geneid2accessions.read_file(rsc_gene.gene2accession_hs,
                                Key_cols = [1],
                                Val_cols = [3])
    
    ret = {}
    for repr_accession in updown:
        ud = updown[ repr_accession ]
        geneid = accession2geneid.val_force(repr_accession)
        if not geneid:
            continue
        accessions = geneid2accessions.val(geneid)
        for acc in accessions:
            ret[ acc ] = ud

    return ret

if __name__ == "__main__":
    h = accession_to_updown_hash()
    
    for k in h:
        print k, h[k]
        
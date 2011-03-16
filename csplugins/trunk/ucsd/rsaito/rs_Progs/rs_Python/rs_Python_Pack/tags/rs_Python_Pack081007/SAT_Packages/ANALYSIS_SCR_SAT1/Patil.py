#!/usr/bin/env python

from Data_Struct.Hash2 import Hash
import BioData_Packages.Gene.NCBI_GeneInfo as GeneInfo 

from Usefuls.rsConfig import RSC_II
rsc      = RSC_II("rsSAT_Config")
rsc_gene = RSC_II("NCBI_GeneInfo")

class Hash_without_version(Hash):
    def conv_val(self, v):
        return v.split(".")[0]
    
class Hash_Patil_get_genesymbol(Hash):
    def conv_key(self, k):
        return k.split(" || ")[1]
    
    
def accession_to_foldchange_hash():
    fc = Hash_Patil_get_genesymbol("S")
    fc.read_file_hd(rsc.Hepatic_Patil,
                    Key_cols_hd = ["Gene Name"],
                    Val_cols_hd = ["Fold Change"])
    del fc.all_data()['']
    symbol2geneid = GeneInfo.get_symbol2geneid_hash()
    
    geneid2accessions = Hash_without_version("A")
    geneid2accessions.read_file(rsc_gene.gene2accession_hs,
                                Key_cols = [1],
                                Val_cols = [3])

    ret = {}
    for genesymbol in fc.keys():
        foldchange = fc[ genesymbol ]
        if genesymbol not in symbol2geneid:
            continue
        geneid = symbol2geneid[ genesymbol ]
        for accession in geneid2accessions.val_force(geneid):
            ret[ accession ] = foldchange
            
    return ret

    
if __name__ == "__main__":
    a2f = accession_to_foldchange_hash()
    for ac in a2f:
        print ac, a2f[ac]
         

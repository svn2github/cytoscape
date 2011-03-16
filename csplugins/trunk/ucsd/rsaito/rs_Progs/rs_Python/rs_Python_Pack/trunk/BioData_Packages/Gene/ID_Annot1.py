#!/usr/bin/env python

from Data_Struct.Hash2 import Hash
from Usefuls.rsConfig import RSC_II
rsc = RSC_II("NCBI_GeneInfo")

class Hash_wo_version(Hash):
    def conv_key(self, k):
        return k.split(".")[0]
    

accession_to_geneid = Hash_wo_version("S")
accession_to_geneid.read_file(rsc.gene2accession,
                              Key_cols = [3],
                              Val_cols = [1])

refseq_to_geneid    = Hash_wo_version("S")
refseq_to_geneid.read_file(rsc.gene2refseq,
                           Key_cols = [3],
                           Val_cols = [1])

geneid_to_info      = Hash("S")
geneid_to_info.read_file(rsc.GeneInfo,
                         Key_cols = [1],
                         Val_cols = [2, 8])

converter = { "gb"  : accession_to_geneid,
              "ref" : refseq_to_geneid }

converter_keys = ("ref", "gb")

def annot(input_id):
    symb = ""
    desc = ""
    geneid = ""
    match_db = ""
    
    for dbase in converter_keys:
        if (input_id in converter[ dbase ] and
            converter[ dbase ][ input_id ] in geneid_to_info):
            geneid = converter[ dbase ][ input_id ]
            symb, desc = geneid_to_info[ geneid ].split("\t")
            match_db = dbase
            break
        
    return geneid, symb, desc, match_db

if __name__ == "__main__":
    import sys
    
    # print annot("AK135156")
    
    for line in open(sys.argv[1]):
        print "\t".join(annot(line.rstrip()))


    
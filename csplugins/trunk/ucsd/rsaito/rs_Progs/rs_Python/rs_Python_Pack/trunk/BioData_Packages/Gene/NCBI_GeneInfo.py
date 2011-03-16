#!/usr/bin/env python

from Usefuls.rsConfig import RSC_II
rsc = RSC_II("NCBI_GeneInfo")

def get_symbol2geneid_hash():
    fh = open(rsc.GeneInfo_hs)
    
    ret = {}
    for line in fh:
        if line.startswith("#"):
            continue
        r = line.rstrip().split("\t")
        geneid = r[1]
        symb = [ r[2] ]
        symbs = r[4].split("|")

        for each_symb in symb + symbs:
            if each_symb != "-":
                ret[ each_symb ] = geneid
                # print each_symb, geneid
            
    return ret

if __name__ == "__main__":
    h = get_symbol2geneid_hash()
    print h["PRO2000"]
    print h["ATAD2"]
      
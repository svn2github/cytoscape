#!/usr/bin/env python

from Usefuls.Synonyms4 import Synonyms

class NCBI_Gene_Synonyms(Synonyms):
    """ Reads Gene synonym information. """
    
    def read_file(self):

        fh = open(self.get_filename())
        
        for line in fh:
            if line.startswith("#"):
                continue
            r = line.rstrip().split("\t")
            main_symb = self.ccs(r[2])
            symbs = r[4].split("|")
    
            for each_symb in [ main_symb ] + symbs:
                if each_symb == "" or each_symb.isspace():
                    continue
                self.append(self.ccs(each_symb), main_symb)
        
        fh.close()

class NCBI_Gene:
    def __init__(self, gene_name, ncbi_gene_synonyms):
        self.gene_name = gene_name
        self.main_name = ncbi_gene_synonyms.to_main_force(gene_name)

    def get_gene_name(self):
        return self.gene_name
    
    def get_main_name(self):
        return self.main_name


if __name__ == "__main__":
    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("NCBI_GeneInfo")
    
    syno = NCBI_Gene_Synonyms(rsc.GeneInfo, case_mode = False)
    print syno.to_main_force("abc27")
    print syno.to_main_force("XXXXX")
    print syno.to_main_force("VLCAD")

    print NCBI_Gene("abc27", syno).get_main_name()

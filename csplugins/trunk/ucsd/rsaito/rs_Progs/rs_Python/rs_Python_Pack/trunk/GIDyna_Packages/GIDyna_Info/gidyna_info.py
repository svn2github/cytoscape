#!/usr/bin/env python
'''
Created on Mar 9, 2011

@author: rsaito
'''

from Data_Struct.Hash3 import Hash

class EMap_Sourav:
    def __init__(self):
        import Usefuls.rsConfig
        rsc = Usefuls.rsConfig.RSC_II("rsGIDyna_Config")
        self.emap = Hash("S")
        self.emap.read_file_hd(filename = rsc.GI_Emap_Sourav,
                               Key_cols_hd = [ "GENE 1", "GENE 2" ],
                               Val_cols_hd = [ "Untreated (S-score)",
                                               "MMS (S-score)",
                                               "log10(conditional p-value)" ])

    
    def get_MMS_minus(self, gene1, gene2):

        gene_pair = "\t".join((gene1, gene2))
        return float(self.emap.val_accord_hd(gene_pair, "Untreated (S-score)"))

    def get_MMS_plus(self, gene1, gene2):

        gene_pair = "\t".join((gene1, gene2))
        return float(self.emap.val_accord_hd(gene_pair, "MMS (S-score)"))

    def get_diff_signif(self, gene1, gene2):
      
        gene_pair = "\t".join((gene1, gene2))
        return float(self.emap.val_accord_hd(gene_pair, "log10(conditional p-value)"))  


if __name__ == "__main__":
    emap_sourav = EMap_Sourav()
    print emap_sourav.get_MMS_minus("YMR291W", "CLN1")
    print emap_sourav.get_MMS_plus("YMR291W", "CLN1")
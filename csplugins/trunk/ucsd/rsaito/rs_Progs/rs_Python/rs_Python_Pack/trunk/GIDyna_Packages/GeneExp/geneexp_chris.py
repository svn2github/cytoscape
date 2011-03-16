#!/usr/bin/env python
'''
Created on Mar 10, 2011

@author: rsaito
'''

from Data_Struct.Data_Sheet3 import Data_Sheet

class GeneExp_Chris:
    def __init__(self):
        
        import Usefuls.rsConfig
        rsc = Usefuls.rsConfig.RSC_II("rsGIDyna_Config")
        self.sheet = Data_Sheet(rsc.Exp_Chris)
        self.sheet.numerize()

    def get_exp(self, gene, cond):
        
        return self.sheet.get_datum(gene, cond)


if __name__ == "__main__":
    
    genexp_chris = GeneExp_Chris()
    print genexp_chris.get_exp("YHR055C", "ADR1")
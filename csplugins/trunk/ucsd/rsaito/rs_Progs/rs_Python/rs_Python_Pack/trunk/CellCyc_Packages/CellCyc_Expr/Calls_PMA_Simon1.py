#!/usr/bin/env python

import sys
import Data_Struct.Data_Sheet3
from Usefuls.Instance_check import instance_class_check
import Usefuls.Synonyms3
import Data_Struct.DictSet1
import BioData_Packages.Gene.NCBI_Synonym3

class Simon_Sheet_Calls(Data_Struct.Data_Sheet3.Data_Sheet):
    def __init__(self, filename, synonyms = None, sep = "\t"):
        if synonyms:
            instance_class_check(synonyms, Usefuls.Synonyms3.Synonyms)

        self.synonyms = synonyms
        self.symbol2probeid = Data_Struct.DictSet1.DictSet()
        Data_Struct.Data_Sheet3.Data_Sheet.__init__(self, filename, sep)

    def extract_col_labels(self, lines, sep):
        first_line = lines.pop(0)
        """ This will extract and eliminate first line """
        col_lb_immature = first_line.split(sep)
        return col_lb_immature[4:]

    def extract_row_label_data(self, line_a):
        label = line_a[0]
        data = line_a[4:]
        
        gene_symbol = line_a[2]
        if self.synonyms:
            gene_symbol = self.synonyms.to_main_force(gene_symbol)
            
        self.symbol2probeid.append(gene_symbol, label)

        return (label, data)

    def get_datum_accord_symbol(self, symbol, col_key):

        if self.synonyms:
            symbol = self.synonyms.to_main_force(symbol)
        
        if not self.symbol2probeid.has_key(symbol): # Much faster than symbol in self.symbol2...
            return None
        else:
            row_keys = self.symbol2probeid[ symbol ]

        ret = map(lambda row_key:self.get_datum(row_key, col_key), row_keys)
        return ret

    def judge_accord_symbol(self, symbol, col_key, thres = 0.000001):
        calls = self.get_datum_accord_symbol(symbol, col_key)
        if calls is None:
            sys.stderr.write("Warning: Expression data for %s not found.\n" % symbol)
            return True
        elif 1.0 * calls.count("P") / len(calls) >= thres:
            return True
        else:
            return False

if __name__ == "__main__":
    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
    rsc_geneinfo = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")
    synonyms = BioData_Packages.Gene.NCBI_Synonym3.NCBI_Gene_Synonyms(rsc_geneinfo.GeneInfo_hs, case_mode = False)
    simon_calls = Simon_Sheet_Calls(rsc.Simons_calls, synonyms)

    print simon_calls.get_data("177_at")
    print simon_calls.get_datum_accord_symbol("PRKAR1A", "X05_0262.CEL"), simon_calls.judge_accord_symbol("PRKAR1A", "X05_0262.CEL")
    print simon_calls.get_datum_accord_symbol("TRADD", "X05_0262.CEL"), simon_calls.judge_accord_symbol("TRADD", "X05_0262.CEL")
    print simon_calls.get_datum_accord_symbol("GDI2", "X05_0262.CEL"), simon_calls.judge_accord_symbol("GDI2", "X05_0262.CEL")
    print simon_calls.get_datum_accord_symbol("CACYBP", "X05_0262.CEL"), simon_calls.judge_accord_symbol("CACYBP", "X05_0262.CEL")
    print simon_calls.get_datum_accord_symbol("XXX", "X05_0262.CEL"), simon_calls.judge_accord_symbol("XXX", "X05_0262.CEL")
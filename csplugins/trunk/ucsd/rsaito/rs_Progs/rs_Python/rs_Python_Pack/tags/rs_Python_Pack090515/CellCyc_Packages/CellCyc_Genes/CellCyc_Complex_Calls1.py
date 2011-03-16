#!/usr/bin/env python

from CellCyc_Packages.CellCyc_Expr.Calls_PMA_Simon1 import Simon_Sheet_Calls
from BioData_Packages.Gene.NCBI_Synonym3 import NCBI_Gene_Synonyms

class CellCyc_Complex_Calls:
    def __init__(self, call_file, geneinfo_file, convfile):
        
        synonyms = NCBI_Gene_Synonyms(geneinfo_file, case_mode = False)
        simon_calls = Simon_Sheet_Calls(call_file, synonyms)

        self.synonyms = synonyms
        self.expr = simon_calls
        self.read_conv(convfile)
    
    def read_conv(self, convfile):
        """ self.conv[ node ] has structure [ procedure [ node1, node2, ...]] """
        
        self.conv = {}
        
        fh = open(convfile, "r")
        for line in fh:
            if line.isspace() or line.startswith("#"):
                continue
            r = line.rstrip().split("\t")
            node_name = r[0].replace(" ", "")
            self.conv[ node_name ] = ["", []]
            if len(r) >= 2:
                self.conv[ node_name ][0] = r[1].replace(" ", "")
            if len(r) >= 3:                
                for subnode in r[2:]:
                    if not subnode.startswith('#'):
                        self.conv[ node_name ][1].append(subnode.replace(" ", ""))
        fh.close()

    def get_calc_type(self, node_name):
        if node_name in self.conv:
            return self.conv[ node_name ][0]
        else:
            return ""

    def get_elems(self, node_name):
        if (node_name in self.conv and
            self.conv[ node_name ][1] != []):
            return self.conv[ node_name ][1]
        else:
            return [ node_name ]

    def calc_exp_node(self, node_name, cond):
        
        elems = self.get_elems(node_name)
        calc_type = self.get_calc_type(node_name)
        
        if calc_type == "IGNORE":
            return True

        expl = map(lambda each_node: 
                   self.expr.judge_accord_symbol(each_node, cond),
                   elems)

        if calc_type == "":
            ret = expl[0]
        elif calc_type == "OR":
            if True in expl:
                ret = True
            else:
                ret = False
        elif calc_type == "AND":
            if False in expl:
                ret = False
            else:
                ret = True
        
        """
        print "* Calc_Exp_Node:", node_name
        print "Isoforms or components integ arithmetics:", self.get_calc_type(node_name)
        for i in range(len(elems)):
            print elems[i], self.synonyms.to_main_force(elems[i]), expl[i]
        print "Expression:", expl
        print "Judge:", ret
        print
        """
        
        return ret

    def calc_exp_complex(self, complex_name, cond, sep = "_"):
        nodes = complex_name.split(sep)
        expl = map(lambda node: self.calc_exp_node(node, cond), nodes)

        """
        for i in range(len(nodes)):
            print "Node", nodes[i], expl[i]
        """

        if False in expl:
            return False
        elif None in expl:
            return "?"
        else:
            return True


if __name__ == "__main__":

    import Usefuls.rsConfig

    rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
    rsc_geneinfo = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

    cellcyc_cmplx_exp = CellCyc_Complex_Calls(rsc.Simons_calls,
                                              rsc_geneinfo.GeneInfo_hs,
                                              rsc.Cell_cyc_Syno_Calls)
    
    print cellcyc_cmplx_exp.calc_exp_node("Cdc2", "X05_0345.CEL")
    print cellcyc_cmplx_exp.calc_exp_node("CyclinB", "X05_0262.CEL")
    print cellcyc_cmplx_exp.calc_exp_node("CyclinA", "X05_0262.CEL")
    print cellcyc_cmplx_exp.calc_exp_node("CyclinA", "X05_0345.CEL")
    print cellcyc_cmplx_exp.calc_exp_node("CyclinD", "X05_0345.CEL")
    print cellcyc_cmplx_exp.calc_exp_node("CAK", "X05_0262.CEL")
    print cellcyc_cmplx_exp.calc_exp_node("XXX", "X05_0262.CEL")
    
    print cellcyc_cmplx_exp.calc_exp_complex("XXX", "X05_0262.CEL")
    
    print cellcyc_cmplx_exp.calc_exp_complex("CyclinB", "X05_0262.CEL") 
    print cellcyc_cmplx_exp.calc_exp_node("CyclinA", "X05_0262.CEL")
    print cellcyc_cmplx_exp.calc_exp_complex("Cdc2_p11_p2_CyclinB", "X05_0262.CEL")
    
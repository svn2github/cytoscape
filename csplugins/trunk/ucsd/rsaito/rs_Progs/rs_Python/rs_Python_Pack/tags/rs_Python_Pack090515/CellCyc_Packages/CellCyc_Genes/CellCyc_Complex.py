#!/usr/bin/env python

from CellCyc_Packages.CellCyc_Expr.Botstein1 import Botstein_Sheet
import BioData_Packages.Gene.NCBI_Synonym

class CellCyc_Complex_Exp:
    def __init__(self, expr_file, geneinfo_file, convfile):
        """ self.conv[ node ] has structure [ procedure [ node1, node2, ...]] """

        synonyms = BioData_Packages.\
            Gene.NCBI_Synonym.NCBI_Gene_Synonyms(geneinfo_file, case_mode = False)
        bot_expr = Botstein_Sheet(expr_file,
                                  synonyms)

        self.expr = bot_expr
        self.conv = {}
        fh = open(convfile, "r")
        for line in fh:
            r = line.rstrip().split("\t")
            node_name = r[0]
            self.conv[ node_name ] = ["", []]
            if len(r) >= 2:
                self.conv[ node_name ][0] = r[1]
                for subnode in r[2:]:
                    if not subnode.startswith('#'):
                        self.conv[ node_name ][1].append(subnode)
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

    def calc_exp_node(self, node_name, cond, filter = None):
        expl = []
        for each_node in self.get_elems(node_name):
            each_exp  = self.expr.get_datum_filter(each_node, cond, filter)
            # print "Sub_node", each_node, "in", node_name, "has expression", each_exp
            if (each_exp is not None and each_exp != ""):
                expl.append(each_exp)

        if expl == []:
            return None

        # print node_name, "consists of subnode(s)", self.get_elems(node_name)
        # print "Expression", expl

        calc_type = self.get_calc_type(node_name)
        if calc_type == "mean":
            return 1.0 * sum(expl) / len(expl)
        elif calc_type == "min":
            return min(expl)
        elif calc_type == "sum":
            return sum(expl)
        else:
            return expl[0]

    def calc_exp_complex(self, complex_name, cond, filter = None, sep = "_"):
        nodes = complex_name.split(sep)
        expl = []
        for node in nodes:
            each_exp = self.calc_exp_node(node, cond, filter)
            #print "Node", node, "in complex", complex_name, "has expression", each_exp
            if each_exp is not None:
                expl.append(each_exp)

        #print complex_name, "consists of nodes", nodes
        #print "Expression of nodes in complex", expl

        if expl == []:
            return None
        else:
            return min(expl)

    def get_datum(self, complex_name, cond):
        return self.calc_exp_complex(complex_name, cond)

    def get_datum_filter(self, complex_name, cond, filter = None):
        return self.calc_exp_complex(complex_name, cond, filter)


if __name__ == "__main__":

    import Usefuls.rsConfig

    rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
    rsc_geneinfo = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

    cellcyc_cmplx_exp = CellCyc_Complex_Exp(rsc.Botstein_expr,
                                            rsc_geneinfo.GeneInfo_hs,
                                            rsc.Cell_cyc_Syno)
    print cellcyc_cmplx_exp.calc_exp_node("CyclinB", "A#2", filter = ("Scaled Fourier", 10, lambda dat, thr: dat >= thr))
    print cellcyc_cmplx_exp.calc_exp_node("CyclinB", "C#0")
    print cellcyc_cmplx_exp.calc_exp_node("p57", "A#2")
    print cellcyc_cmplx_exp.calc_exp_node("CAK", "A#2")
    print cellcyc_cmplx_exp.calc_exp_node("XXX", "A#2")
    print cellcyc_cmplx_exp.calc_exp_complex("Cdc2_p11_p2_CyclinB", "C#0")
    print cellcyc_cmplx_exp.get_datum("CyclinB", "A#2")
    print cellcyc_cmplx_exp.get_datum_filter("p21", "C#0", filter = ("Scaled Fourier", 10, lambda dat, thr: dat >= thr))

#!/usr/bin/env python

import CellCyc_Packages.CellCyc_Expr.Botstein1 as Botstein
from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex import CellCyc_Complex_Exp
from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II2

import Graph_Packages.Graph.Graph1 as Graph
from Path_Expr1 import Path_Search_expr

class single_Path_Expr_Score:
    def __init__(self, path, expr_c):
        self.path   = path
        self.expr_c = expr_c

        # Note that graph object is in self.path.get_graph()

    def calc_score(self, cond):
        print "--- In func ---"
        for node in self.path.get_path_node_names():
            print node, self.expr_c.calc_exp_complex(node, cond)


if __name__ == "__main__":

    import Usefuls.TmpFile
    hypo_path = Usefuls.TmpFile.TmpFile_III("""

STK15      PLK      a
PLK        MAPK13   b
MAPK13     XXXX     c
PLK        CDC2     d
PLK        CDC_X    d
CDC2       KPNA2    e
CDC_X      KPNA2    e
KPNA2      XXXX     f
CDC2       YYYY     g
YYYY       KPNA2    h
KPNA2      XXXX     i

""")

    hypo_expr = Usefuls.TmpFile.TmpFile_III("""

X   Gene_Symbol  X  X  X  X   A#2   A#3
X   STK15        X  X  X  X   1.3   1.2
X   PLK          X  X  X  X   1.2   1.2
X   MAPK13       X  X  X  X   -1.2   1.2
X   CDC2         X  X  X  X   1.4   1.2
X   YYYY         X  X  X  X   1.5   1.2

""")

    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
    rsc_geneinfo = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

    cellcyc_cmplx_exp = CellCyc_Complex_Exp(rsc.Botstein_expr,
                                            rsc_geneinfo.GeneInfo_hs,
                                            rsc.Cell_cyc_Syno)

    import Graph_Packages.Graph.Node1 as Node

    # bot_expr = Botstein.Botstein_Sheet(rsc.Botstein_expr)
    bot_expr = Botstein.Botstein_Sheet(hypo_expr.filename())
    bot_expr.numerize()

    # graph1 = Graph.Graph()
    # graph1.read_from_file2(hypo_path.filename(), 0, 1, None)

    graph1 = read_cellcyc_path_II2(rsc.Cell_cyc_path_mammal_descr6)

    path_search_expr = Path_Search_expr(graph1, 2)
    start_node = Node.Node_Factory().make("CyclinD")
    goal_node = Node.Node_Factory().make("Cdc2_p2_CyclinB")
    path_search_expr.set_start_node(start_node)
    path_search_expr.set_goal_node(goal_node)
    path_search_expr.loop_steps_until_goal()

    """ Obtaining pathways through all the nodes in "node_mark_set" """
    node_mark_set = path_search_expr.get_node_mark_set()
    for node in node_mark_set:
        print node.get_node_name(), ":"
        # print path_search_expr.get_node_mark(node).get_paths()
        for path_rank in path_search_expr.get_node_mark(node).get_paths():
            for path in path_rank:
                single_Path_Expr_Score(path, cellcyc_cmplx_exp).calc_score("A#2")
        print







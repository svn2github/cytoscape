#!/usr/bin/env python

import CellCyc_Packages.CellCyc_Expr.Botstain1 as Botstain
import Graph_Packages.Graph.Path_search1 as Path_search
import Graph_Packages.Graph.Graph1 as Graph
from Usefuls.Instance_check import instance_class_check


class Path_Search_expr(Path_search.Path_Search):
    def loop_steps_until_goal_incorp_expr(self,
                                          bot_expr,
                                          col_label,
                                          thres,
                                          goal_end_mode = False):
        instance_class_check(bot_expr, Botstain.Botstain_Sheet)

        gene_names = bot_expr.row_labels()
        on_gene_names = bot_expr.get_genes_above_thres_simp(
            col_label,
            thres)
        on_nodes = []
        graph_nodes = self.get_graph().get_node_set().get_nodes()

        for graph_node in graph_nodes:
            on_flag = True
            for unt in graph_node.get_node_name().split("_"):
                unit = unt.upper()
                # print "Checking", unit, len(unit),\
                #      (unit in gene_names), (unit not in on_gene_names)
                if len(unit) <= 1: continue
                if unit in gene_names and (
                    unit not in on_gene_names):
                    # print graph_node.get_node_name(), "OFF"
                    on_flag = False
                    break
            if on_flag:
                on_nodes.append(graph_node)

        """
        print "On nodes are", len(on_nodes), ":"
        for node in on_nodes:
            print node
            """
                
        self.loop_steps_until_goal_consider_ON(on_nodes)
        
        
if __name__ == "__main__":

    import Usefuls.TmpFile
    hypo_path = Usefuls.TmpFile.TmpFile_III("""

STK15      PLK      a
PLK        MAPK13   b
MAPK13     XXXX     c
PLK        CDC2     d
CDC2       KPNA2    e
KPNA2      XXXX     f
CDC2       YYYY     g
YYYY       KPNA2    h
KPNA2      XXXX     i

""")

    hypo_expr = Usefuls.TmpFile.TmpFile_III("""

X   Gene_Symbol  X  X  X  X   A#2   A#3
X   STK15        X  X  X  X   1.0   1.0
X   PLK          X  X  X  X   1.0   1.0
X   MAPK13       X  X  X  X   1.0   1.0
X   CDC2         X  X  X  X   1.0   1.0
X   YYYY         X  X  X  X   -1.0   1.0

""")
    
    import Usefuls.rsConfig
    rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

    import Graph_Packages.Graph.Node1 as Node
    
    # bot_expr = Botstain.Botstain_Sheet(rsc.Botstain_expr)
    bot_expr = Botstain.Botstain_Sheet(hypo_expr.filename())
    bot_expr.numerize()

    graph1 = Graph.Graph()
    graph1.read_from_file2(hypo_path.filename(), 0, 1, None)

    path_search_expr = Path_Search_expr(graph1, 3)
    path_search_expr.set_start_node(Node.Node_Factory().make("STK15"))
    path_search_expr.set_goal_node(Node.Node_Factory().make("XXXX"))

    path_search_expr.loop_steps_until_goal_incorp_expr(
        bot_expr,
        "A#2", 0)
    print path_search_expr.get_info_I()
    print path_search_expr.get_node_mark(Node.Node_Factory().make("XXXX"))
    print path_search_expr.get_node_mark(Node.Node_Factory().make("XXXX")).get_num_pathways()

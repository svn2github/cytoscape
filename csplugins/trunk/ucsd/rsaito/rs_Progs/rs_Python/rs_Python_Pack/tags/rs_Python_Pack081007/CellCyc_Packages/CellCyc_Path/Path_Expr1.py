#!/usr/bin/env python

import CellCyc_Packages.CellCyc_Expr.Botstain1 as Botstain
import Graph_Packages.Graph.Path_search1 as Path_search
import Graph_Packages.Graph.Graph1 as Graph
from Usefuls.Instance_check import instance_class_check
from Calc_Packages.Math.MathI import cumulat

class Path_Search_expr(Path_search.Path_Search):
    """ Node name will be separated by "_" 
    Ex. "CDC2_p" -> ("CDC2", "p")
    len("p") -> 1 ... discarded """

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
                # "CDC2_p" -> ("CDC2", "p") ... len("p") -> 1
                unit = unt.upper()
                # print "Checking", unit, len(unit),\
                #    (unit in gene_names), (unit not in on_gene_names)
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


    def get_info_I_exp(self,
                       bot_expr,
                       col_label,
                       thres):

        out = ""

        info1 = self.get_nodes_info_goal_path()
        info2 = self.get_nodes_info_goal_path_II()

        path_lens = info1.keys()
        path_lens.sort()
        nodes = {}

        for path_len in info1:
            for node in info1[ path_len ]:
                nodes[ node ] = ""

        for node in nodes:
            ret_tmp = []
            for path_len in path_lens:
                if node in info1[ path_len ]:
                    ret_tmp.append(info1[ path_len ][ node ])
                else:
                    ret_tmp.append(0)
            ret_tmp = cumulat(ret_tmp)
            ret = []
            for elem in ret_tmp:
                ret.append(`elem`)
            out += "\t".join(("[ Overview ]",
                              node.get_node_name(),
                              "\t".join(ret))) + "\n"
            path_count = 0
            for path_len in path_lens:
                if node in info2[ path_len ]:
                    for path in info2[ path_len ][ node ]:
                        node_exp_min = None
                        node_exp_ret = []
                        for node_in_path in path.get_path_node_names():
                            if bot_expr.get_datum(node_in_path, 
                                                  col_label) is None:
                                exp_str = "-"
                            else:
                                exp_str = "%.3f" % bot_expr.get_datum(node_in_path, 
                                                                      col_label)
                                if (node_exp_min is None or 
                                    node_exp_min > bot_expr.get_datum(node_in_path, 
                                                                      col_label)):
                                    node_exp_min = bot_expr.get_datum(node_in_path, 
                                                                      col_label)
                                        
                            node_exp_ret.append(node_in_path)
                            node_exp_ret.append(exp_str)
                            
                            if node_exp_min is None:
                                node_exp_min_str = "-"
                            else:
                                node_exp_min_str = "%.3f" % node_exp_min
                            
                        out += "\t".join((
                                "[ Description ]",
                                node.get_node_name(),
                                "\t".join(ret),
                                `path_count`,
                                node_exp_min_str,
                                "\t".join(node_exp_ret))) + "\n"
                path_count += 1
        return out

    # `bot_expr.get_datum(node.get_node_name(), col_label)`,
        

    def get_exp_along_path(self,
                           bot_expr,
                           col_labels):

        step2nodes = self.get_nodes_step_goal_path_II() # Option
        out = [[ "Step", "Node" ] + col_labels]
        steps = step2nodes.keys()
        steps.sort()
        for step in steps:
            for node in step2nodes[step]:
                line = [ `step`, node.get_node_name() ]
                for col_label in col_labels:
                    exp_level = bot_expr.get_datum(node.get_node_name(),
                                                   col_label)
                    if type(exp_level) is int or type(exp_level) is float:
                        line.append("%.3lf" % (exp_level,))
                    else:
                        line.append("")
                out.append(line)

        return out


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
        "A#2", 1.0)
    print "Object:", path_search_expr
    print "Info  :"
    print path_search_expr.get_info_I_exp(bot_expr,
                                          "A#2",
                                          1.0)
    print "NMark :"
    print path_search_expr.get_node_mark(Node.Node_Factory().make("XXXX"))
    print "NPath :", path_search_expr.get_node_mark(Node.Node_Factory().make("XXXX")).get_num_pathways()

    print path_search_expr.get_exp_along_path(bot_expr,
                                              ["A#2", "A#3"])
                                              

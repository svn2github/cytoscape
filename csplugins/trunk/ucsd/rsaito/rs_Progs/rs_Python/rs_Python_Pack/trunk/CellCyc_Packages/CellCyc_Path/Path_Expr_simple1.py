#!/usr/bin/env python

import Graph_Packages.Graph.Path_search_simple1 as Path_search
from Usefuls.Hash_recorder import Hash_recorder

class Path_Search_expr(Path_search.Path_Search):
    """ Node name will be separated by "_"
    Ex. "CDC2_p" -> ("CDC2", "p")
    len("p") -> 1 ... discarded """

    def get_nodes_info_goal_path_II_str_exp_Simon(self, exp_func, conditions, via_node = None):

        exp_complex = Hash_recorder(exp_func)
    
        goal_path_info = self.get_info_goal_path()
        path_count = 0
    
        ret = []
        
        for path_len in goal_path_info:
            ret.append("*** Path length %s ***" % path_len)
            for path in goal_path_info[ path_len ]:
                if (via_node and
                    not path.check_node_deep(via_node)): # <-- Intermediate node check
                    continue
                ret.append("Path %s:" % path_count)
                pnode = None
                for node in path:
                    if pnode is None:
                        wt = ""
                    else:
                        wt = self.get_graph().get_edge_weight(pnode, node)
                    calls = ""
                    for exp_label in conditions: # <-- Expression calculation
                        calls += { True:  "O",
                                   False: "-" }[ exp_complex[(node.get_node_name(), exp_label)] ]
                    ret.append("\t".join((wt, calls, node.get_node_name())))
                    pnode = node
                path_count += 1
                ret.append("")
                
        return "\n".join(ret)


if __name__ == "__main__":

    import Graph_Packages.Graph.Graph1 as Graph
    import Graph_Packages.Graph.Node1 as Node

    from CellCyc_Packages.CellCyc_Expr.Simons_data_labels import *
    
    import Usefuls.rsConfig
    rsc_cellcyc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
    rsc_geneinf = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")
    
    from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex_Calls1 import CellCyc_Complex_Calls
    cellcyc_cmplx_exp = CellCyc_Complex_Calls(rsc_cellcyc.Simons_calls,
                                              rsc_geneinf.GeneInfo_hs,
                                              rsc_cellcyc.Cell_cyc_Syno_Calls)
    
    from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II2
    graph1 = read_cellcyc_path_II2(rsc_cellcyc.Cell_cyc_path_mammal_descr6)
       
    start_node_name = "CyclinD"
    via_elem = "E2F4"
    # goal_node_name = "E2F1_DP"
    goal_node_name = "Cdc2_p2_CyclinB"
    extra_steps = 3
    
    start_node = Node.Node_Factory().make(start_node_name)
    goal_node = Node.Node_Factory().make(goal_node_name)
       
    path_search_calls = Path_Search_expr(graph1, extra_steps)
    path_search_calls.set_start_node(start_node)
    path_search_calls.set_goal_node(goal_node)
    
    path_search_calls.loop_steps_until_goal(goal_end_mode = False, on_nodes = None)

    print path_search_calls.\
        get_nodes_info_goal_path_II_str_exp_Simon(cellcyc_cmplx_exp.calc_exp_complex,
                                                  Serum_starvation_Thymidine_block_synchronization,
                                                  via_elem
                                                  )




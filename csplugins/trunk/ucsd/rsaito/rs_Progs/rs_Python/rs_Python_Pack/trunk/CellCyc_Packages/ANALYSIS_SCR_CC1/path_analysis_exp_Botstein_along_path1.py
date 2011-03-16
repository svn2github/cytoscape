#!/usr/bin/env python

import os

import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
import Graph_Packages.Graph.Path_search_simple1 as Path_search

# from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex_Calls1 import CellCyc_Complex_Calls
from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex import CellCyc_Complex_Exp

from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II2

# from CellCyc_Packages.CellCyc_Expr.Simons_data_labels import *
from CellCyc_Packages.CellCyc_Expr.Botstein_data_labels import *

from Usefuls.Hash_recorder import Hash_recorder

import Usefuls.rsConfig
rsc_cellcyc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
rsc_geneinf = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

Node_None_Mark = '-'

graph1 = read_cellcyc_path_II2(rsc_cellcyc.Cell_cyc_path_mammal_descr6)


# cellcyc_cmplx_exp = CellCyc_Complex_Calls(rsc_cellcyc.Simons_calls,
#                                           rsc_geneinf.GeneInfo_hs,
#                                           rsc_cellcyc.Cell_cyc_Syno_Calls)


cellcyc_cmplx_exp = CellCyc_Complex_Exp(rsc_cellcyc.Botstein_expr,
                                        rsc_geneinf.GeneInfo_hs,
                                        rsc_cellcyc.Cell_cyc_Syno)

node_set_names = graph1.get_node_set().get_node_names()
node_set_names.sort()

start_node_name = "CyclinD"
# goal_node_name = "E2F1_DP"
goal_node_name = "Cdc2_p2_CyclinB"
extra_steps = 3

start_node = Node.Node_Factory().make(start_node_name)
goal_node = Node.Node_Factory().make(goal_node_name)

# exp_labels = Serum_starvation_Thymidine_block_synchronization
exp_labels = conditions_A

path_search_calls = Path_search.Path_Search(graph1, extra_steps)
path_search_calls.set_start_node(start_node)
path_search_calls.set_goal_node(goal_node)

path_search_calls.loop_steps_until_goal(goal_end_mode = False, on_nodes = None)

goal_path_info = path_search_calls.get_info_goal_path()
path_count = 0

via_node = None
ret = []

exp_complex = Hash_recorder(cellcyc_cmplx_exp.calc_exp_complex)

for path_len in goal_path_info:
    ret.append("*** Path length %s ***" % path_len)
    for path in goal_path_info[ path_len ]:
        if (via_node and
            not path.check_node(via_node)):
            continue
        ret.append("Path %s:" % path_count)
        pnode = None
        for node in path:
            if pnode is None:
                wt = ""
            else:
                wt = path_search_calls.get_graph().get_edge_weight(pnode, node)
            expl = ""
            for exp_label in exp_labels:
                expval = exp_complex.get(node.get_node_name(), exp_label)
                if expval is None:
                    expl += "      "
                else:
                    expl += "%+3.2f " % expval
            ret.append("\t".join((wt, expl, node.get_node_name())))
            pnode = node
        path_count += 1
        ret.append("")
        
print "\n".join(ret)
 









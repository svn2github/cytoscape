#!/usr/bin/env python

import os

import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
#import CellCyc_Packages.CellCyc_Path.Path_Expr1 as Path_Expr
import Graph_Packages.Graph.Path_search1 as Path_search

from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex_Calls1 import CellCyc_Complex_Calls
from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II2

from Usefuls.Table_maker import Table_row

from CellCyc_Packages.CellCyc_Expr.Simons_data_labels import *

import Usefuls.rsConfig
rsc_cellcyc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")
rsc_geneinf = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

Node_None_Mark = '-'

graph1 = read_cellcyc_path_II2(rsc_cellcyc.Cell_cyc_path_mammal_descr6)

start_node_name = "CyclinD"
# goal_node_name = "E2F1_DP"
goal_node_name = "Cdc2_p2_CyclinB"
mediate_node_name = Node_None_Mark
extra_steps = 3

start_node = Node.Node_Factory().make(start_node_name)
goal_node = Node.Node_Factory().make(goal_node_name)

if mediate_node_name and mediate_node_name != Node_None_Mark:
    mediate_node = Node.Node_Factory().make(mediate_node_name)
else:
    mediate_node = None



cellcyc_cmplx_exp = CellCyc_Complex_Calls(rsc_cellcyc.Simons_calls,
                                          rsc_geneinf.GeneInfo_hs,
                                          rsc_cellcyc.Cell_cyc_Syno_Calls)

tb = Table_row()

tb.append("Condition", "")

for analysis_node in graph1.get_nodes():
    tb.append(analysis_node.get_node_name(), "")      

for cond in Serum_starvation_Thymidine_block_synchronization: 

    on_nodes = []
    for node in graph1.get_nodes():
        if cellcyc_cmplx_exp.calc_exp_complex(node.get_node_name(), cond) is True:
            print node.get_node_name(), "is ON."
            on_nodes.append(node)
    
    path_search_calls = Path_search.Path_Search(graph1, extra_steps)
    path_search_calls.set_start_node(start_node)
    path_search_calls.set_goal_node(goal_node)
    # path_search_calls.loop_steps_until_goal_consider_ON(on_nodes)
    path_search_calls.loop_steps_until_goal()

    tb.append("Condition", cond)
    for analysis_node in path_search_calls.get_node_mark_set().get_node_set():
        tb.append(analysis_node.get_node_name(),
                  `path_search_calls.get_num_used_goal_path(analysis_node)`)
    tb.record()

tb.output_record('\t')

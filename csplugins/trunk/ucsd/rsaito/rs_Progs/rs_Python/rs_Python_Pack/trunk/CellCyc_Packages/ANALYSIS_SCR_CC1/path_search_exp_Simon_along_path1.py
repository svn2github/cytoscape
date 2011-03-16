#!/usr/bin/env python

import os

import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
# import CellCyc_Packages.CellCyc_Path.Path_Expr1 as Path_Expr
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


cellcyc_cmplx_exp = CellCyc_Complex_Calls(rsc_cellcyc.Simons_calls,
                                          rsc_geneinf.GeneInfo_hs,
                                          rsc_cellcyc.Cell_cyc_Syno_Calls)

node_set_names = graph1.get_node_set().get_node_names()
node_set_names.sort()

start_node_name = "CyclinD"
# goal_node_name = "E2F1_DP"
goal_node_name = "Cdc2_p2_CyclinB"
extra_steps = 3

start_node = Node.Node_Factory().make(start_node_name)
goal_node = Node.Node_Factory().make(goal_node_name)

path_search_calls = Path_search.Path_Search(graph1, extra_steps)
path_search_calls.set_start_node(start_node)
path_search_calls.set_goal_node(goal_node)
path_search_calls.loop_steps_until_goal()

node_count   = path_search_calls.get_goal_path_node_count()
num_pathways = path_search_calls.get_node_mark(goal_node).get_num_pathways()


tb = Table_row()
for exp_label in Serum_starvation_Thymidine_block_synchronization:
    tb.append("Condition", exp_label)
    path_search_calls = Path_search.Path_Search(graph1, extra_steps)
    path_search_calls.set_start_node(start_node)
    path_search_calls.set_goal_node(goal_node)
    on_nodes = []
    for node_name in node_set_names:
        call = cellcyc_cmplx_exp.calc_exp_complex(node_name, exp_label)
        if call is True:
            on_nodes.append(Node.Node_Factory().make(node_name))
    
    path_search_calls.loop_steps_until_goal_consider_ON(on_nodes)
 
    for node_name in node_set_names:
        syno_mode = cellcyc_cmplx_exp.get_calc_type(node_name)
        if syno_mode == "IGNORE":
            numpath = "-"
        else:
            numpath = `path_search_calls.get_num_used_goal_path(Node.Node_Factory().make(node_name))`
        tb.append(node_name, numpath)
    tb.output("\t")






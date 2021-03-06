#!/usr/bin/env python

import os

import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
import CellCyc_Packages.CellCyc_Path.Path_Expr1 as Path_Expr
import CellCyc_Packages.CellCyc_Expr.Botstein1 as Botstein

from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex import CellCyc_Complex_Exp
from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II2

# from GNUplot.GNUplot_xlabel1 import GNUplot

from Usefuls.Table_maker import Table_row

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

rsc_geneinfo = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

Node_None_Mark = '-'

bot_expr = Botstein.Botstein_Sheet(rsc.Botstein_expr)
bot_expr.numerize()

cellcyc_cmplx_exp = CellCyc_Complex_Exp(rsc.Botstein_expr,
                                        rsc_geneinfo.GeneInfo_hs,
                                        rsc.Cell_cyc_Syno)

graph1 = read_cellcyc_path_II2(rsc.Cell_cyc_path_mammal_descr6)


start_node_name = "CyclinD"
# goal_node_name = "E2F1_DP"
goal_node_name = "Cdc2_p2_CyclinB"
mediate_node_name = Node_None_Mark
extra_steps = 3

analysis_node = Node.Node_Factory().make("Cdk4_CyclinD")

"""
conditions = [ "A#0",
               "A#2",
               "A#4",
               "A#6",
               "A#8",
               "A#10",
               "A#12",
               "A#14",
               "A#16",
               "A#20",
               "A#30" ]
"""

conditions = [ "C#0",
               "C#1",
               "C#2",
               "C#3",
               "C#4",
               "C#5",
               "C#6",
               "C#7",
               "C#8",
               "C#9",
               "C#10",
               "C#11",
               "C#12",
               "C#13",
               "C#14",
               "C#15",
               "C#16",
               "C#17",
               "C#18",
               "C#19",
               "C#20",
               "C#21",
               "C#22",
               "C#23",
               "C#24",
               "C#25",
               "C#26",
               "C#27",
               "C#28",
               "C#29",
               "C#30",
               "C#31",
               "C#32",
               "C#33",
               "C#34",
               "C#35",
               "C#36",
               "C#37",
               "C#38",
               "C#39",
               "C#40",
               "C#41",
               "C#42",
               "C#43",
               "C#44",
               "C#45",
               "C#46" ]

start_node = Node.Node_Factory().make(start_node_name)
goal_node = Node.Node_Factory().make(goal_node_name)

if mediate_node_name and mediate_node_name != Node_None_Mark:
    mediate_node = Node.Node_Factory().make(mediate_node_name)
else:
    mediate_node = None



# path_search_expr = Path_Expr.Path_Search_expr(graph1, extra_steps)
# path_search_expr.set_start_node(start_node)
# path_search_expr.set_goal_node(goal_node)
# path_search_expr.loop_steps_until_goal()
# print path_search_expr.get_nodes_info_goal_path_II_str()

tb = Table_row()

tb.append("Condition", "")
path_search_expr = Path_Expr.Path_Search_expr(graph1, extra_steps)
for analysis_node in path_search_expr.get_node_mark_set().get_node_set():
    tb.append(analysis_node.get_node_name(), "")

for cond in conditions:
    path_search_expr = Path_Expr.Path_Search_expr(graph1, extra_steps)
    path_search_expr.set_start_node(start_node)
    path_search_expr.set_goal_node(goal_node)

    path_search_expr.loop_steps_until_goal_incorp_expr(
       bot_expr,
       cond, 0.0)
    # path_search_expr.loop_steps_until_goal()

    tb.append("Condition", cond)
    for analysis_node in path_search_expr.get_node_mark_set().get_node_set():
        tb.append(analysis_node.get_node_name(),
                  `path_search_expr.get_num_used_goal_path(analysis_node)`)
    tb.record()

tb.output_record('\t')






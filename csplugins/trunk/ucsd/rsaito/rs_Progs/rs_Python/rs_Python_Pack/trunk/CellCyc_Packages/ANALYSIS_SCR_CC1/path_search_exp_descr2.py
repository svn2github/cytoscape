#!/usr/bin/env python

import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
import CellCyc_Packages.CellCyc_Path.Path_Expr1 as Path_Expr
import CellCyc_Packages.CellCyc_Expr.Botstein1 as Botstein

from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex import CellCyc_Complex_Exp
from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II

# from GNUplot.GNUplot_xlabel1 import GNUplot

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

rsc_geneinfo = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")

cellcyc_cmplx_exp = CellCyc_Complex_Exp(rsc.Botstein_expr,
                                        rsc_geneinfo.GeneInfo_hs,
                                        rsc.Cell_cyc_Syno)

graph1 = read_cellcyc_path_II(rsc.Cell_cyc_path_mammal_descr5_2_2)

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

start_node = Node.Node_Factory().make("CyclinD")
#start_node = Node.Node_Factory().make("E2F1")
#analysis_node = Node.Node_Factory().make("Cdc25A_p")

#goal_node = Node.Node_Factory().make("E2F4_DP_p_Rb_p")
#goal_node = Node.Node_Factory().make("CyclinA")
goal_node = Node.Node_Factory().make("E2F1_DP")
#goal_node = Node.Node_Factory().make("Cdk4_p_CyclinD")
#goal_node = Node.Node_Factory().make("Cdk2_p2_CyclinA")
#goal_node = Node.Node_Factory().make("Cdc2_p2_CyclinB")

npaths = []

cond = conditions[0]


path_search_expr = Path_Expr.Path_Search_expr(graph1, 3)
path_search_expr.set_start_node(start_node)
path_search_expr.set_goal_node(goal_node)

"""
path_search_expr.loop_steps_until_goal_incorp_expr(
    bot_expr,
    cond, -10)
"""

path_search_expr.loop_steps_until_goal()

print "Info  :"
print path_search_expr.get_info_I_exp(
    cellcyc_cmplx_exp,
    "A#2",
    1.0)

"""
print "***", cond, "***"
print path_search_expr.get_num_used_goal_path(analysis_node)
npaths.append(path_search_expr.get_num_used_goal_path(analysis_node))

for path in path_search_expr.get_path_goal_path(analysis_node):
    print path.ret_path_bold(analysis_node)
"""



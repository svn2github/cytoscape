#!/usr/bin/env python

import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
import CellCyc_Packages.CellCyc_Path.Path_Expr1 as Path_Expr
import CellCyc_Packages.CellCyc_Expr.Botstain1 as Botstain

from CellCyc_Packages.CellCyc_Genes.CellCyc_Complex import CellCyc_Complex_Exp
from CellCyc_Packages.CellCyc_Path.Path_Read import read_cellcyc_path_II

# from GNUplot.GNUplot_xlabel1 import GNUplot

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

rsc_geneinfo = Usefuls.rsConfig.RSC_II("NCBI_GeneInfo")
    
cellcyc_cmplx_exp = CellCyc_Complex_Exp(rsc.Botstain_expr,
                                        rsc_geneinfo.GeneInfo_hs,
                                        rsc.Cell_cyc_Syno)

graph1 = read_cellcyc_path_II(rsc.Cell_cyc_path_mammal_descr5_2_2)

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

"""
print "Info  :"
print path_search_expr.get_info_I_exp(
    cellcyc_cmplx_exp, 
    "A#2",
    1.0)
"""

"""
print "***", cond, "***"
print path_search_expr.get_num_used_goal_path(analysis_node)
npaths.append(path_search_expr.get_num_used_goal_path(analysis_node))

for path in path_search_expr.get_path_goal_path(analysis_node):
    print path.ret_path_bold(analysis_node)
"""
    
for line in path_search_expr.get_exp_along_path(cellcyc_cmplx_exp,
                                                conditions):
    print "\t".join(line)

#!/usr/bin/env python

import Graph_Packages.Graph.Node1 as Node
import Graph_Packages.Graph.Graph1 as Graph
import CellCyc_Packages.CellCyc_Path.Path_Expr1 as Path_Expr
import CellCyc_Packages.CellCyc_Expr.Botstain1 as Botstain

from GNUplot.GNUplot_xlabel1 import GNUplot

import Usefuls.rsConfig
rsc = Usefuls.rsConfig.RSC_II("rsCellCyc_Config")

bot_expr = Botstain.Botstain_Sheet(rsc.Botstain_expr)
bot_expr.numerize()

graph1 = Graph.Graph()
graph1.read_from_file2(rsc.Cell_cyc_path_mammal, 0, 1, None)

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
analysis_node = Node.Node_Factory().make("Cdk2_p_p")
goal_node = Node.Node_Factory().make("Cdc2")

gp = GNUplot()
gp.set_conditions(conditions)

npaths = []

for cond in conditions:
    path_search_expr = Path_Expr.Path_Search_expr(graph1, 3)
    path_search_expr.set_start_node(start_node)
    path_search_expr.set_goal_node(goal_node)
    path_search_expr.loop_steps_until_goal_incorp_expr(
        bot_expr,
        cond, 0)

    print "***", cond, "***"
    a_node_mark = path_search_expr.get_node_mark(analysis_node)
    if a_node_mark:
        """
        print "***", cond, path_search_expr.get_node_mark(
            analysis_node).get_num_pathways(), "***"
        print path_search_expr.get_node_mark(
            analysis_node).output1()
            """

        npaths.append(path_search_expr.get_node_mark(
            analysis_node).get_num_pathways())
        for path_set in path_search_expr.get_node_mark(
            analysis_node).get_paths():
            for path in path_set:
                print path.ret_path_bold(analysis_node)
        
        # for path in path_search_expr.get_path_goal_path(analysis_node):
        #    print path.ret_path_bold(analysis_node)
            
    else:
        print "NO PATHWAY"
        npaths.append(0)


gp.set_data("NumPath", npaths)
gp.gnuplot_line()

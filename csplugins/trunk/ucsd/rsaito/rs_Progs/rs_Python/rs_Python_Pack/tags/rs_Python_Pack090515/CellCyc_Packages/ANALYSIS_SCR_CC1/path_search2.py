#!/usr/bin/env python

from Graph_Packages.Graph.Graph1 import Graph
from Graph_Packages.Graph.Node1 import Node_Factory
from Graph_Packages.Graph.Path_search1 import Path_Search

from General_Packages.Usefuls.rsConfig import RSC_II
rsc_cc = RSC_II("rsCellCyc_Config")

node_factory = Node_Factory()

graph1 = Graph()
graph1.read_from_file2(rsc_cc.Cell_cyc_path_mammal, 0, 1, None)
# graph1.graph_display()

starting_nodes = ("CyclinA",
                  "CyclinB", 
                  "CyclinD", 
                  "CyclinE")

goal_nodes = ("E2F1_DP",
              "E2F4_DP")

for snode in starting_nodes:
    for gnode in goal_nodes:
        starting_node = node_factory.make(snode)
        goal_node = node_factory.make(gnode)

        path_search1 = Path_Search(graph1, 3)
        path_search1.set_start_node(starting_node)
        path_search1.set_goal_node(goal_node)
        path_search1.loop_steps_until_goal(goal_node)

        outfile = snode + "_" + gnode
        fh = open(outfile, "w")
        fh.write(path_search1.get_info_I())
        fh.close()
        # path_search1.display_info()
        # print path_search1.get_node_mark(goal_node)
        # print path_search1.get_node_mark_set().output_node_marks()



# CyclinA,B,D,E ---> E2F1_DP,E2F4_DP
# How number of shortest paths change according to gene expression data.
# Reverse (R) elimination

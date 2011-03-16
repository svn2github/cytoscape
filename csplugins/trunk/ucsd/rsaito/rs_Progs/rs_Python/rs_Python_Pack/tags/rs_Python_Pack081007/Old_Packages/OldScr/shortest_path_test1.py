#!/usr/bin/env python

import sys
sys.path.append("../")

import Graph.Path_search1
import Graph.Graph1

GRAPHFILE = "tmpgraph.txt" # TAB delimited file for graph
LIMIT     = 4              # Number of allowed pathways

graph = Graph.Graph1.Graph()
graph.read_from_file2(filename = GRAPHFILE,
                      col1 = 0, # Column for source node
                      col2 = 1, # Column for destination node
                      weight = None)

path_search = Graph.Path_search1.Path_Search1(graph, LIMIT)
starting_node = graph.get_node_by_name("Node-A")
terminal_node = graph.get_node_by_name("Node-F")
path_search.set_start_node(starting_node)
path_search.loop_steps_until_goal(terminal_node) # Pathway search

print path_search.get_node_mark(terminal_node)
# Shows pathway information which has reached terminal node.

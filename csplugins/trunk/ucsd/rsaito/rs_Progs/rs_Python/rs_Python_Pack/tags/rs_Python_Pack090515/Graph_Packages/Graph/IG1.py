#!/usr/bin/env python

""" Calculates interaction generality. """

from Graph1 import Graph
import Node1
import Edge1

def IG_I(graph, node1, node2):
    interactors = list(set(graph.interactors(node1) + graph.interactors(node2)))
    # print interactors
    ig = 1
    for node in interactors:
        if node is not node1 and node is not node2:
            # print node.get_node_name(), len(graph.interactors(node)), graph.interactors(node) 
            if len(graph.interactors(node)) == 1:
                ig += 1
    return ig

if __name__ == "__main__":
    graph = Graph()
    graph.add_edge_by_node_names("A", "B")
    graph.add_edge_by_node_names("A", "C")
    graph.add_edge_by_node_names("A", "D")
    graph.add_edge_by_node_names("A", "E")
    graph.add_edge_by_node_names("A", "G")
    graph.add_edge_by_node_names("E", "J")
    graph.add_edge_by_node_names("B", "K")
    graph.add_edge_by_node_names("B", "G")
    graph.add_edge_by_node_names("B", "F")
    graph.add_edge_by_node_names("F", "H")
    graph.add_edge_by_node_names("H", "I")
    graph.both_dir()
    graph.graph_display()
    """
    print IG_I(graph, 
               Node1.Node_Factory().make("A"),
               Node1.Node_Factory().make("B"))
    """
    for edge in graph.get_all_edges():
        node1, node2 = edge.get_nodes()
        print node1.get_node_name(), node2.get_node_name(), \
            IG_I(graph, node1, node2)
    
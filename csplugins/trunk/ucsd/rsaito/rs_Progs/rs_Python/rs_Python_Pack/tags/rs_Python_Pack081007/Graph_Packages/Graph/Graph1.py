#!/usr/bin/env python

# This module is necessary to create several kinds of graphs.

import Node1
import Edge1

import Data_Struct.Hash2 as Hash2

class Graph:
    def __init__(self):
        self.node_set = Node1.Node_Set()
        self.edge_set = Edge1.Edge_Set()

    def get_node_set(self):
        return self.node_set

    def get_edge_set(self):
        return self.edge_set

    """
    ***************************************************
    Besides __init__, only the following four methods,
    add_edge, add_edge_weight, add_edge_obj and del_edge
    manipulates edge, and node set.
    ***************************************************
    """

    def add_edge(self, s_node, d_node):

        s_node = self.node_set.add_node(s_node)
        d_node = self.node_set.add_node(d_node)
        self.edge_set.add_edge(s_node, d_node)

        return s_node, d_node

    def add_edge_weight(self, s_node, d_node, weight):

        s_node = self.node_set.add_node(s_node)
        d_node = self.node_set.add_node(d_node)
        self.edge_set.add_edge_weight(s_node, d_node, weight)

        return s_node, d_node

    # edge must be created by Node_Factory and Edge_Factory
    def add_edge_obj(self, edge):

        s_node, d_node = edge.get_nodes()
        s_node = self.node_set.add_node(s_node)
        d_node = self.node_set.add_node(d_node)
        self.edge_set.add_edge_obj(edge)

        return s_node, d_node

    def del_edge(self, s_node, d_node):
        """ "Alone Node" will be eliminated from the graph. """

        self.edge_set.del_edge(s_node, d_node)

        if (self.interactors(s_node) == []):
            self.node_set.delete_node(s_node)

        if (self.interactors(d_node) == []):
            self.node_set.delete_node(d_node)

    def del_edge_both(self, s_node, d_node):

        self.del_edge(s_node, d_node)
        if not s_node is d_node:
            self.del_edge(d_node, s_node)

    def del_node(self, node):

        for interacting_node in self.interactors(node):
            self.del_edge_both(node, interacting_node)

        # Below is necessary if del_edge method does not delete
        # the node automatically.
        # self.node_set.delete_node(node)


    def add_edge_by_node_names(self,
                               s_node_name,
                               d_node_name,
                               weight = None):

        s_node = Node1.Node(s_node_name)
        d_node = Node1.Node(d_node_name)
        self.add_edge_weight(s_node, d_node, weight)

        return s_node, d_node


    def get_all_edges(self):

        return self.edge_set.get_all_edges()

    def get_all_pairs(self):

        return self.edge_set.get_all_pairs()

    def get_non_redu_edges(self): # Only one direction.

        return self.edge_set.get_non_redu_edges()

    def get_non_redu_pairs(self): # Only one direction.

        return self.edge_set.get_non_redu_pairs()


    # other_graph must be created by Node_Factory and Edge_Factory
    def add_graph(self, other_graph):

        if not isinstance(other_graph, Graph):
            raise "Instance type mismatch: Graph expected."

        all_edges = other_graph.get_all_edges()
        for edge in all_edges:
            self.add_edge_obj(edge)

    # This may not be implemented as method of Edge because
    # nodes in the file are primitives
    def read_hash_tab(self, hash):
        if not isinstance(hash, Hash2.Hash):
            raise "Instance type mismatch."

        for pair in hash.keys():
            s_node_name, d_node_name = pair.split("\t")
            weight = hash.val(pair)
            self.add_edge_by_node_names(s_node_name, d_node_name,
                                        weight)

    def read_from_file(self, filename, col1, col2, valcol):
        h = Hash2.Hash("S")
        h.read_file(filename,
                    Key_cols = [col1,col2],
                    Val_cols = [valcol])

        self.read_hash_tab(h)

    def read_hash_tab2(self, hash, weight):
        # Same value weight is used.
        if not isinstance(hash, Hash2.Hash):
            raise "Instance type mismatch."

        for pair in hash.keys():
            s_node_name, d_node_name = pair.split("\t")
            self.add_edge_by_node_names(s_node_name, d_node_name,
                                        weight)

    def read_from_file2(self, filename, col1, col2, weight):

        h = Hash2.Hash("N")
        h.read_file(filename,
                    Key_cols = [col1,col2],
                    Val_cols = [])

        self.read_hash_tab2(h, weight)


    def read_dict(self, idict):
        for s_node_name in idict.keys():
            for d_node_name in idict[s_node_name].keys():
                weight = idict[s_node_name][d_node_name]
                self.add_edge_by_node_names(s_node_name,
                                            d_node_name,
                                            weight)

    def read_dict2(self, idict, weight):
        for s_node_name in idict.keys():
            for d_node_name in idict[s_node_name].keys():
                self.add_edge_by_node_names(s_node_name,
                                            d_node_name,
                                            weight)

    def both_dir(self):

        self.edge_set.both_dir()

    def get_edge(self, s_node, d_node):

        return self.edge_set.get_edge(s_node, d_node)

    def get_edge_weight(self, s_node, d_node):

        return self.edge_set.get_edge_weight(s_node, d_node)

    def get_node_by_name(self, node_name):

        return self.node_set.get_node_by_name(node_name)

    def get_edge_by_node_names(self, s_node_name, d_node_name):

        s_node = self.node_set.get_node_by_name(s_node_name)
        d_node = self.node_set.get_node_by_name(d_node_name)

        return self.get_edge(s_node, d_node)


    def get_nodes(self):

        return self.node_set.get_nodes()

    def get_node_names(self):

        return self.node_set.get_node_names()

    def interactors(self, node):

        return self.edge_set.interactors(node)

    def source_nodes(self, d_node):
        return self.edge_set.source_nodes(d_node)

    def destination_nodes(self, s_node):
        return self.edge_set.destination_nodes(s_node)


    def graph_info1(self):
        print "Node Set:"
        for node in self.get_nodes():
            print node, node.get_node_name()
        print "Edge Set:"
        for edge in self.get_all_edges():
            s_node, d_node = edge.get_nodes()
            print "Edge", \
                  s_node.get_node_name(), \
                  d_node.get_node_name(), \
                  "...", edge
            print "Value", edge.get_weight()


    def graph_display(self):
        for edge in self.get_all_edges():
            s_node, d_node = edge.get_nodes()
            weight = edge.get_weight()
            if type(weight) is float or type(weight) is int:
                weight = `weight`
            elif weight is None: # Temporary added.
                weight = ""
            print "\t".join([s_node.get_node_name(),
                             d_node.get_node_name(),
                             weight])


    def pair_judge(self):
        return True


    def graph_filter(self):
        # Judge must be done as a part of Graph4's method because
        # judge may depend on global structure of GRAPH

        graph_filtered = Graph()

        for pair in self.get_all_edges():
            if self.pair_judge(pair) is True:
                graph_filtered.add_edge_obj(pair)

        return graph_filtered


if __name__ == "__main__":
    import Usefuls.TmpFile
    tmp_obj = Usefuls.TmpFile.TmpFile_III("""

Node-A     Node-B   a
Node-A     Node-B   b
Node-B     Node-A   c
Node-D     Node-E   d
Node-E     Node-F   e
Node-X     Node-X   *


""")

    tmp_obj2 = Usefuls.TmpFile.TmpFile_III("""

Node-A     Node-B   t
Node-A     Node-B   u
Node-B     Node-A   v
Node-D     Node-E   w
Node-E     Node-F   x
Node-A     Node-B   t
Node-A     Node-C   t


""")

    print "***** Basic Check *****"
    graph = Graph()
    nodeA = Node1.Node_Factory().make("A")
    nodeB = Node1.Node_Factory().make("B")
    nodeC = Node1.Node_Factory().make("C")
    nodeD = Node1.Node_Factory().make("D")
    graph.add_edge_weight(nodeA, nodeB, "GRAPH1")
    graph.add_edge_weight(nodeB, nodeC, "GRAPH2")
    graph.add_edge_weight(nodeB, nodeA, "GRAPH2")
    graph.add_edge_weight(nodeD, nodeB, "GRAPH3")
    graph.add_edge_weight(nodeD, nodeD, "GRAPH4")
    print "All graphs:"
    for pair in graph.get_all_edges():
        print pair, pair.get_nodes()
    print "Non-redundant graphs:"
    print graph.get_non_redu_edges()

    graph.graph_info1()
    print "Interactors of", nodeB,":"
    print graph.interactors(nodeB)
    print graph.source_nodes(nodeB)
    print graph.destination_nodes(nodeB)
    graph.del_edge_both(nodeA, nodeB)
    graph.del_edge_both(nodeD, nodeD)
    graph.graph_info1()

    print

    print "***** GRAPH Addition Check *****"
    graph2 = Graph()
    nodeA2 = Node1.Node("A2")
    nodeB2 = Node1.Node("B2")
    nodeC2 = Node1.Node("C2")
    graph2.add_edge_weight(nodeA2, nodeB2, "graph1")
    graph2.add_edge_weight(nodeB2, nodeC2, "graph2")
    graph2.add_edge_weight(nodeB2, nodeA2, "graph2")
    graph2.add_graph(graph)
    graph2.graph_display()

    print

    print "***** Pair Set Check *****"
    pair1 = Edge1.Edge(nodeA2, nodeC2)
    pair1.set_weight("Yaa")
    graph2.add_edge_obj(pair1)
    print graph2.get_all_edges()
    # print graph2.get_non_redu_graph()
    graph2.graph_display()

    print

    print "***** File Read Check 1 *****"
    graph3 = Graph()
    graph3.read_from_file(tmp_obj.filename(), 0, 1, 2)
    graph3.del_node(Node1.Node_Factory().make("Node-A"))
    graph3.graph_display()
    graph3.graph_info1()
    print "All:", graph3.get_all_edges()
    print "Non-redu:", graph3.get_non_redu_edges()

    print

    print "***** File Read Check 2 *****"
    graph4 = Graph()
    graph4.read_from_file2(tmp_obj.filename(), 0, 1, "Test")
    graph4.graph_display()

    print

    print "***** Hash *****"

    graph5_hash = ({
    "A": { "B": "ab", "C": "ac" },
    "C": {"C": "cc", "D": "cd"},
    "E": {"F": "ef" }})

    graph5 = Graph()
    graph5.read_dict(graph5_hash)
    graph5.both_dir()
    graph5.graph_display()

    print

    print "***** Hash 2 *****"

    class ValueClass:
        pass
    vct = ValueClass()

    graph6 = Graph()
    graph6.read_dict2(graph5_hash, "TESTING")
    graph6.graph_display()
    print graph6.get_edge_by_node_names("A", "B").get_nodes()

    print graph2.get_edge(nodeA2, nodeB2)
    print graph6.get_nodes()
    print graph6.get_node_names()

    print nodeA
    print "Interactors:"
    print graph.interactors(nodeA)
    print graph.source_nodes(nodeA)
    print graph.destination_nodes(nodeA)
    print "Before:"
    graph6.graph_info1()
    graph6.add_edge_by_node_names("A", "X", vct)
    print "After:"
    graph6.graph_info1()
    print
    # graph6.graph_display()
    # graph6.graph_cytoscape_simple1()

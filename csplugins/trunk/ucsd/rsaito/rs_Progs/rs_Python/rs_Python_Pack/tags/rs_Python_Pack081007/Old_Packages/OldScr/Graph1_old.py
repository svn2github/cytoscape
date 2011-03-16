#!/usr/bin/env python

# This module is necessary to create several kinds of graphs.

import sys
sys.path.append("../")
import string

import Node1
import Edge1
import Data_Struct.Hash

class Graph:
    def __init__(self):
	self.directed_edge = {}
        self.directed_edge_rev = {}
	self.node_set = Node1.Node_Set()

    def get_node_set(self):

        return self.node_set

    """
    ***************************************************
    Besides __init__, only the following two methods,
    add_edge and del_edge, controls edge, and node set.
    ***************************************************
    """

    def add_edge(self, s_node, d_node):

        s_node = self.node_set.add_node(s_node)
        d_node = self.node_set.add_node(d_node)

        if s_node in self.directed_edge:
            if d_node in self.directed_edge[s_node]:
                # Overwrite
                self.directed_edge[s_node][d_node] = Edge1.Edge(s_node,
                                                               d_node)
            else:
                self.directed_edge[s_node][d_node] = Edge1.Edge(s_node,
                                                               d_node)
        else:
            self.directed_edge[s_node] = {
                d_node: Edge1.Edge(s_node, d_node) }

        if d_node in self.directed_edge_rev:
            if s_node in self.directed_edge_rev[d_node]:
                self.directed_edge_rev[d_node][s_node] = \
                    self.directed_edge[s_node][d_node] # Overwrite
            else:
                self.directed_edge_rev[d_node][s_node] = \
                    self.directed_edge[s_node][d_node]
        else:
            self.directed_edge_rev[d_node] = {
                s_node: self.directed_edge[s_node][d_node] }

        return s_node, d_node

    def del_edge(self, p1, p2):

        del self.directed_edge[p1][p2]
        del self.directed_edge_rev[p2][p1]

        if self.directed_edge[p1] == {}:
            del self.directed_edge[p1]
        if self.directed_edge_rev[p2] == {}:
            del self.directed_edge_rev[p2]

        if ((not self.directed_edge.has_key(p1)) and
            (not self.directed_edge_rev.has_key(p1))):
            self.node_set.delete_node(p1)
        if ((not self.directed_edge.has_key(p2)) and
            (not self.directed_edge_rev.has_key(p2))):
            self.node_set.delete_node(p2)

    def del_edge_both(self, p1, p2):

        self.del_edge(p1, p2)
        if not p1 is p2:
            self.del_edge(p2, p1)

    """ Needs modification """
    def add_edge_weight(self, p1, p2, weight):

        p1, p2 = self.add_edge(p1, p2)
        self.directed_edge[ p1 ][ p2 ].set_weight(weight)

        return p1, p2

    def add_edge_by_node_names(self,
                               node_name1,
                               node_name2,
                               weight = None):

        p1 = Node1.Node(node_name1)
        p2 = Node1.Node(node_name2)
        self.add_edge_weight(p1, p2, weight)

        return p1, p2


    def get_all_edges(self):
	edgelist = []
        for p1 in self.directed_edge.keys():
            for p2 in self.directed_edge[p1].keys():
                edgelist.append(self.directed_edge[p1][p2])

	return edgelist

    def get_non_redu_edges(self): # Only one direction.
        edgelist = []
        done = {}
        for p1 in self.directed_edge.keys():
            for p2 in self.directed_edge[p1].keys():
                pair12 = (p1, p2)
                if pair12 in done: continue
                pair21 = (p2, p1)
                edgelist.append(self.directed_edge[p1][p2])
                done[ pair12 ] = ""
                done[ pair21 ] = ""
        return edgelist

    def add_graph(self, other_graph):

        if not isinstance(other_graph, Graph):
            raise "Instance type mismatch: Graph expected."

        for p1 in other_graph.directed_edge.keys():
            for p2 in other_graph.directed_edge[p1].keys():
                self.add_edge_obj(other_graph.directed_edge[p1][p2])


    # This may not be implemented as method of Edge because
    # nodes in the file are primitives
    def read_hash_tab(self, hash):
        if not isinstance(hash, Data_Struct.Hash.Hash):
            raise "Instance type mismatch."

        for pair in hash.keys():
            node_name1, node_name2 = pair.split("\t")
            p1 = Node1.Node(node_name1)
            p2 = Node1.Node(node_name2)
            self.add_edge_weight(p1, p2, hash.val(pair))

    def read_from_file(self, filename, col1, col2, valcol):
        h = Data_Struct.Hash.Hash("S")
        h.read_file(filename,
                    Key_cols = [col1,col2],
                    Val_cols = [valcol])

        self.read_hash_tab(h)

    def read_hash_tab2(self, hash, weight):
	# Same value weight is used.
        if not isinstance(hash, Data_Struct.Hash.Hash):
            raise "Instance type mismatch."

        for pair in hash.keys():
            node_name1, node_name2 = pair.split("\t")
            p1 = Node1.Node(node_name1)
            p2 = Node1.Node(node_name2)
            self.add_edge_weight(p1, p2, weight)

    def read_from_file2(self, filename, col1, col2, weight):

        h = Data_Struct.Hash.Hash("N")
        h.read_file(filename,
                    Key_cols = [col1,col2],
                    Val_cols = [])

        self.read_hash_tab2(h, weight)


    def read_dict(self, idict):
	for node_name1 in idict.keys():
            p1 = Node1.Node(node_name1)
	    for node_name2 in idict[node_name1].keys():
                p2 = Node1.Node(node_name2)
                self.add_edge_weight(p1, p2,
                                 idict[node_name1][node_name2])

    def read_dict2(self, idict, weight):
	for node_name1 in idict.keys():
            p1 = Node1.Node(node_name1)
	    for node_name2 in idict[node_name1].keys():
                p2 = Node1.Node(node_name2)
                self.add_edge_weight(p1, p2, weight)

    def both_dir(self):
        for p1 in self.directed_edge.keys():
            for p2 in self.directed_edge[p1].keys():
                pair = self.directed_edge[p1][p2]
                weight = pair.get_weight()
                self.add_edge_weight(p2, p1, weight)


    def get_edge(self, p1, p2):
	if (p1 in self.directed_edge and
            p2 in self.directed_edge[p1]):
	    return self.directed_edge[p1][p2]
	else:
	    return False

    def get_edge_weight(self, p1, p2):
	if p1 in self.directed_edge and p2 in self.directed_edge[p1]:
	    return self.directed_edge[p1][p2].get_weight()
	else:
	    return False

    def get_node_by_name(self, node_name):
        return self.node_set.get_node_by_name(node_name)

    def get_edge_by_node_names(self,
                                 node_name1,
                                 node_name2):

        p1 = self.node_set.get_node_by_name(node_name1)
        p2 = self.node_set.get_node_by_name(node_name2)

        return self.get_edge(p1, p2)


    def get_nodes(self):

	return self.node_set.get_nodes()

    def get_node_names(self):

	return self.node_set.get_node_names()

    def interactor(self, p):
	if p in self.directed_edge:
	    return self.directed_edge[p].keys()
	else:
	    return []

    def interactor_rev(self, p):
	if p in self.directed_edge_rev:
	    return self.directed_edge_rev[p].keys()
	else:
	    return []

    def graph_info1(self):
        print "Node Set:"
        for node in self.get_nodes():
            print node, node.get_node_name()
        print "Edge:"
        for p1 in self.directed_edge.keys():
            for p2 in self.directed_edge[p1].keys():
                pair = self.directed_edge[p1][p2]
                print "Pair", \
                      pair.get_nodes()[0].get_node_name(), \
                      pair.get_nodes()[1].get_node_name(), \
                      "...", pair
                print "Nodes ...", pair.get_nodes()
                print "Value", pair.get_weight()

    def graph_info_rev1(self):
        print "Node Set:"
        for node in self.get_nodes():
            print node, node.get_node_name()
        print "GRAPH:"
        for p1 in self.directed_edge_rev.keys():
            for p2 in self.directed_edge_rev[p1].keys():
                pair = self.directed_edge_rev[p1][p2]
                print "Pair", \
                      pair.get_nodes()[0].get_node_name(), \
                      pair.get_nodes()[1].get_node_name(), \
                      "...", pair
                print "Pair rev", p1.get_node_name(), p2.get_node_name()
                print "Nodes ...", pair.get_nodes()
                print "Value", pair.get_weight()


    def graph_display(self):
        for p1 in self.directed_edge.keys():
            for p2 in self.directed_edge[p1].keys():
                weight = self.directed_edge[p1][p2].get_weight()
                print string.join([p1.get_node_name(),
                                   p2.get_node_name(), weight], "\t")


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
    tmp_obj = Usefuls.TmpFile.TmpFile3("""

Node-A     Node-B   a
Node-A     Node-B   b
Node-B     Node-A   c
Node-D     Node-E   d
Node-E     Node-F   e
Node-X     Node-X   *


""")

    tmp_obj2 = Usefuls.TmpFile.TmpFile3("""

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
    print graph.interactor(nodeB)
    print graph.interactor_rev(nodeB)
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
    print graph.interactor(nodeA)
    print graph.interactor_rev(nodeA)
    print "Before:"
    graph6.graph_info1()
    graph6.add_edge_by_node_names("A", "X", vct)
    print "After:"
    graph6.graph_info1()
    print
    graph6.graph_info_rev1()
    # graph6.graph_display()
    # graph6.graph_cytoscape_simple1()

#!/usr/bin/env python

from General_Packages.Obj_Oriented.Obj_Factory1 import Obj_Factory2
from General_Packages.Data_Struct.NonRedSet1 import NonRedList

# Do not import Node module. Node module and Edge module
# must be separated and integrated by Graph module.

class Edge:
    def __init__(self, s_node, d_node):
        self.s_node = s_node
        self.d_node = d_node
        self.weight = None

    def set_weight(self, weight):
        self.weight = weight

    def set_graph(self, graph):
        self.graph = graph

    def get_nodes(self):
        return self.s_node, self.d_node

    def get_s_node(self):
        return self.s_node

    def get_d_node(self):
        return self.d_node

    def get_weight(self):
        return self.weight

    def __str__(self):
        return "Edge " + `id(self)` + ":" + \
               `self.get_s_node(), self.get_d_node(), self.get_weight()`


class Edge_Factory(Obj_Factory2):
    def set_classobj(self):
        self.classobj = Edge


class Edge_Set:
    def __init__(self):

        self.directed_edge = {}
        self.directed_edge_rev = {}
        self.edge_factory = Edge_Factory()

    """
    ***************************************************
    Besides __init__, only the following two methods,
    add_edge and del_edge, manipulates directed_edge(_rev)
    ***************************************************
    """

    def add_edge(self, s_node, d_node, edge = None):

        if edge is None:
            edge = self.edge_factory.make(s_node, d_node)
        else:
            reg_s_node = edge.get_s_node()
            reg_d_node = edge.get_d_node()
            if (not(reg_s_node is s_node) or
                not(reg_d_node is d_node)):
                raise "Edge error..."

        if s_node in self.directed_edge:
            if d_node in self.directed_edge[s_node]:
                # Overwrite
                self.directed_edge[s_node][d_node] = edge
            else:
                self.directed_edge[s_node][d_node] = edge

        else:
            self.directed_edge[s_node] = { d_node: edge }

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


    def add_edge_obj(self, edge):

        s_node, d_node = edge.get_nodes()
        self.add_edge(s_node, d_node, edge)


    def del_edge(self, s_node, d_node):

        del self.directed_edge[s_node][d_node]
        del self.directed_edge_rev[d_node][s_node]

        if self.directed_edge[s_node] == {}:
            del self.directed_edge[s_node]
        if self.directed_edge_rev[d_node] == {}:
            del self.directed_edge_rev[d_node]

    def del_edge_both(self, s_node, d_node):

        self.del_edge(s_node, d_node)
        if not s_node is d_node:
            self.del_edge(d_node, s_node)

    def get_edge(self, s_node, d_node):

        if (s_node in self.directed_edge and
            d_node in self.directed_edge[s_node]):
            return self.directed_edge[s_node][d_node]
        else:
            return False

    def get_edge_weight(self, s_node, d_node):
        edge = self.get_edge(s_node, d_node)
        if edge is False:
            return False
        else:
            return edge.get_weight()

    def add_edge_weight(self, s_node, d_node, weight):

        self.add_edge(s_node, d_node)
        self.get_edge(s_node, d_node).set_weight(weight)


    def get_all_edges(self):
	edgelist = []
        for s_node in self.directed_edge.keys():
            for d_node in self.directed_edge[s_node].keys():
                edgelist.append(self.directed_edge[s_node][d_node])

	return edgelist

    def get_all_pairs(self):
        ret = []

        for edge in self.get_all_edges():
            node1, node2 = edge.get_nodes()
            weight = edge.get_weight()
            ret.append((node1, node2, weight))

        return ret

    def get_non_redu_edges(self): # Only one direction.
        edgelist = []
        done = {}
        for s_node in self.directed_edge.keys():
            for d_node in self.directed_edge[s_node].keys():
                pair12 = (s_node, d_node)
                if pair12 in done: continue
                pair21 = (d_node, s_node)
                edgelist.append(self.directed_edge[s_node][d_node])
                done[ pair12 ] = ""
                done[ pair21 ] = ""

        return edgelist

    def get_non_redu_pairs(self): # Only one direction.

        ret = []

        for edge in self.get_non_redu_edges():
            node1, node2 = edge.get_nodes()
            weight = edge.get_weight()
            ret.append((node1, node2, weight))

        return ret

    def both_dir(self):
        for s_node in self.directed_edge.keys():
            for d_node in self.directed_edge[s_node].keys():
                pair = self.directed_edge[s_node][d_node]
                weight = pair.get_weight()
                self.add_edge_weight(d_node, s_node, weight)


    def destination_nodes(self, s_node):
	if s_node in self.directed_edge:
	    return self.directed_edge[s_node].keys()
	else:
	    return []

    def source_nodes(self, d_node):
	if d_node in self.directed_edge_rev:
	    return self.directed_edge_rev[d_node].keys()
	else:
	    return []

    def interactors(self, node):
        ret = NonRedList(
            self.source_nodes(node) +
            self.destination_nodes(node))
        return ret

if __name__ == "__main__":
    edge1 = Edge("Node 1", "Node 2")
    edge1.set_weight(100)
    print edge1.get_nodes()
    print edge1.get_s_node()
    print edge1.get_d_node()
    print edge1.get_weight()

    factory1 = Edge_Factory()
    edge2 = factory1.make("A", "B")
    edge3 = factory1.make("B", "C")
    edge4 = factory1.make("A", "B")
    edge5 = factory1.make("B", "A")
    edgeX = factory1.make("X", "Y")
    print id(edge2), edge2.get_nodes()
    print id(edge3), edge3.get_nodes()
    print id(edge4), edge4.get_nodes()

    edge_set = Edge_Set()
    edge_set.add_edge("A", "B")
    edge_set.add_edge("C", "D")
    edge_set.add_edge("E", "F")
    edge_set.add_edge("B", "A")
    edge_set.add_edge("A", "D")
    edge_set.add_edge("C", "A")

    # edge_set.del_edge_both("A", "B")
    edge_set.add_edge_weight("C", "D", 2)
    print edge_set.get_edge("C", "D")
    edge_set.add_edge_obj(edgeX)
    # edge_set.both_dir()

    print "#####"
    print edge_set.get_all_edges()
    print edge_set.get_non_redu_edges()


    print edge_set.directed_edge
    print edge_set.directed_edge_rev
    print edge_set.source_nodes("A")
    print edge_set.destination_nodes("A")
    print edge_set.interactors("A")
    print edge_set.get_all_pairs()
    print edge_set.get_non_redu_pairs()

#!/usr/bin/env python

import Graph1
import Node1

class Path:
    def __init__(self, graph = None):
        self.path = []
        self.used_nodes = {}
        self.graph = graph

    def __iter__(self):
        return self.path.__iter__()

    def add_node(self, node):
        self.path.append(node)
        self.used_nodes[ node ] = self.used_nodes.get(node, 0) + 1

    def get_path(self):
        return self.path

    def get_used_nodes_dict(self):
        return self.used_nodes

    def check_node(self, node):
        return self.used_nodes.has_key(node)

    def check_node_deep(self, cnode, check_func = lambda chekk, onode: chekk in onode):
        
        for node in self:
            if check_func(cnode, node.get_node_name()):
                return True
        return False
    
    def last_node(self):
        return self.path[-1]

    def next_nodes(self):
        return self.graph.destination_nodes(self.last_node())

    def copy(self):
        new_path = Path(self.graph)
        new_path.path = self.path[:]
        new_path.used_nodes = self.used_nodes.copy()
        return new_path

    def get_path_node_names(self):
        node_name_set = []
        for node in self.path:
            node_name_set.append(node.get_node_name())
        return node_name_set

    def __len__(self):
        return len(self.path)

    def __str__(self):
        return "Path: " + " -> ".join(self.get_path_node_names())

    def ret_path_bold(self, bnode):
        ret_node = []
        for node in self.get_path():
            if bnode is node:
                ret_node.append("[" + node.get_node_name() + "]")
            else:
                ret_node.append(node.get_node_name())
        return " -> ".join(ret_node)


if __name__ == "__main__":

    node_factory = Node1.Node_Factory()
    
    path1 = Path()
    path1.add_node(node_factory.make("-A-"))
    path1.add_node(node_factory.make("-B-"))
    path1.add_node(node_factory.make("-C-"))
    path1.add_node(node_factory.make("XXX_YYY_ZZZ"))
    path1.add_node(node_factory.make("GOAL"))
    print path1.check_node_deep("YYY", lambda chekk, onode: chekk in onode.split("_"))

    print path1
    
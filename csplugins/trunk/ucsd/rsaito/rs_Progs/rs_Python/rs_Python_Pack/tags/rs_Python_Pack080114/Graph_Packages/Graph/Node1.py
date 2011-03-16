#!/usr/bin/env python

from General_Packages.Obj_Oriented.Obj_Factory1 import Obj_Factory

class Node:
    def __init__(self, node_name):
        self.node_name = node_name

    def get_node_name(self):
        return self.node_name

    def set_graph(self, graph):
        self.graph = graph

    def __str__(self):
        return "Node " + `id(self)` + ":" + self.get_node_name()


class Node_Factory(Obj_Factory):
    def set_classobj(self):
        self.classobj = Node

class Node_Set:
    def __init__(self):
        self.node_set = {}
        self.node_name_set = {}
        self.node_factory = Node_Factory()

    def add_node_by_name(self, node_name):
        node = self.node_factory.make(node_name)
        self.node_set[ node ] = node_name
        self.node_name_set[ node_name ] = node
        return node

    def add_node(self, inode):
        node_name = inode.get_node_name()
        return self.add_node_by_name(node_name)

    def delete_node(self, node):
        if not self.has_node(node):
            raise "Node " + node + " not in the set."
        del self.node_set[ node ]
        del self.node_name_set[ node.get_node_name() ]

    def delete_node_by_name(self, node_name):
        node = self.get_node_by_name(node_name)
        self.delete_node(node)

    def has_node(self, node):
        return node in self.node_set

    def node_same_name(self, node):
        node_name = node.get_node_name()
        if node_name in self.node_name_set:
            return self.node_name_set[ node_name ]
        else:
            return False

    def get_node_by_name(self, node_name):

        if node_name in self.node_name_set:
            return self.node_name_set[ node_name ]
        else:
            return False

    def get_nodes(self):
        return self.node_set.keys()

    def get_node_names(self):
        return self.node_name_set.keys()

    def display_node_set(self):
        for node in self.node_set:
            print node, node.get_node_name()

    def __iter__(self):
        return self.node_set.keys().__iter__()

if __name__ == "__main__":
    node = Node("Node 1")
    print node
    print node.get_node_name()

    nodeset = Node_Set()
    nodeset.add_node_by_name("Rintaro")
    nodeset.add_node_by_name("Saito")
    nodeset.add_node_by_name("Dosan")
    nodeset.add_node_by_name("Saito")
    nodeset.display_node_set()

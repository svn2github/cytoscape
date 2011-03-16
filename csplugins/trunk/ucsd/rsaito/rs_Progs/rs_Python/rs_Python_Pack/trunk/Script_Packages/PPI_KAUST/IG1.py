#!/usr/bin/env python

import sys

from Graph_Packages.Graph.Graph1 import Graph
from Graph_Packages.Graph.Node1 import Node
from Graph_Packages.Graph.Edge1 import Edge
from Graph_Packages.Graph.IG1 import IG_I

ppinet = Graph()
ppinet.read_from_file2(sys.argv[1], 0, 1, "")

for edge in ppinet.get_all_edges():
    node1, node2 = edge.get_nodes()
    print "\t".join((node1.get_node_name(), node2.get_node_name(),
                    `IG_I(ppinet, node1, node2)`))


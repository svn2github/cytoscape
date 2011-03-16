#!/usr/bin/env python

from networkx import in_degree_centrality, out_degree_centrality, betweenness_centrality, \
    closeness_centrality, eigenvector_centrality, average_clustering
from Graph_Packages.Graph.Graph1 import Graph

import Usefuls.TmpFile

tmp_obj = Usefuls.TmpFile.TmpFile_III("""

Node-A     Node-B   a
Node-A     Node-B   b
Node-B     Node-A   c
Node-D     Node-E   d
Node-E     Node-F   e
Node-X     Node-X   *


""")

graph1 = Graph()
graph1.read_from_file(tmp_obj.filename(), 0, 1, 2)

nx = graph1.get_networkx_digraph()

for s_node in nx:
    for d_node in nx[s_node]:
        print s_node, d_node, nx[s_node][d_node]

print in_degree_centrality(nx)
print out_degree_centrality(nx)
print betweenness_centrality(nx)
print closeness_centrality(nx)
# print eigenvector_centrality(nx)
# print average_clustering(nx) # Not defined for directed graphs.

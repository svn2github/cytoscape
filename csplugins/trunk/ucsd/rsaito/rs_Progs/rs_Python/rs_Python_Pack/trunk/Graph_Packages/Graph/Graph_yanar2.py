#!/usr/bin/env python

from networkx import *
from Graph_yanar1 import get_abj, all_pairs_shortest_path

path_list = []
def id_search2(limit, goal, path):
    n = len(path)
    m = path[n - 1]
    if n == limit:
        if m == goal:
            path_list.append(path[:])
    else:
        for x in adjacent[m]:
            if x not in path:
                if pathway[x].has_key(goal):
                    lim = limit-n
                    val = lim -len(pathway[x][goal])
                    if val >= 0:
                        path.append(x)
                        id_search2(limit, goal, path)
                        path.pop()
    return [path_list,len(path_list)]

if __name__ == "__main__":
    #G=networkx graph

    from Usefuls.rsConfig import RSC_II
    rsc = RSC_II("rsGraph_Config")
    
    def kegg2g(file):
        G = DiGraph()
        for line in open(file):
            line = line.lstrip().rstrip()
            if line.startswith("<relation"):
                elems = line.split('"')
                G.add_edge(int(elems[1]),int(elems[3]))
        return G


    """ read_edge_list """
    file = rsc.yanar_sample1
    G = gnp_random_graph(100,40)
    #adjacent = get_abj(G)

    adjacent = get_abj(G)
    pathway = all_pairs_shortest_path(G)
    for x in range(1, 8):
        print x, 'moves'
        print id_search2(x, 5, [0])


